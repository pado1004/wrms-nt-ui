package com.wrms.nt.views.counseling;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.wrms.nt.domain.counseling.entity.Counseling;
import com.wrms.nt.domain.counseling.entity.CounselingHistory;
import com.wrms.nt.domain.counseling.entity.CounselingStatus;
import com.wrms.nt.domain.counseling.service.CounselingHistoryService;
import com.wrms.nt.domain.counseling.service.CounselingService;
import com.wrms.nt.domain.user.entity.User;
import com.wrms.nt.domain.user.service.UserService;
import com.wrms.nt.views.MainLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 상담 상세 화면
 */
@Route(value = "counseling/:id", layout = MainLayout.class)
@PageTitle("상담 상세 | WRMS NT")
public class CounselingDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final CounselingService counselingService;
    private final CounselingHistoryService historyService;
    private final UserService userService;

    private Counseling counseling;
    private Long counselingId;

    // UI Components
    private final H2 titleHeader = new H2();
    private final VerticalLayout infoSection = new VerticalLayout();
    private final VerticalLayout historySection = new VerticalLayout();
    private final VerticalLayout commentSection = new VerticalLayout();

    private TransferDialog transferDialog;
    private EscalationDialog escalationDialog;

    public CounselingDetailView(CounselingService counselingService,
                                CounselingHistoryService historyService,
                                UserService userService) {
        this.counselingService = counselingService;
        this.historyService = historyService;
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        createHeader();
        add(titleHeader, infoSection, historySection, commentSection);

        // Initialize dialogs
        transferDialog = new TransferDialog(counselingService, userService, this::refreshData);
        escalationDialog = new EscalationDialog(counselingService, this::refreshData);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> idParam = event.getRouteParameters().get("id");
        if (idParam.isPresent()) {
            try {
                counselingId = Long.parseLong(idParam.get());
                loadCounselingData();
            } catch (NumberFormatException e) {
                Notification.show("잘못된 상담 ID입니다", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                event.forwardTo(CounselingListView.class);
            }
        } else {
            event.forwardTo(CounselingListView.class);
        }
    }

    private void createHeader() {
        Button backButton = new Button("목록으로", VaadinIcon.ARROW_LEFT.create());
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(CounselingListView.class)));

        HorizontalLayout headerLayout = new HorizontalLayout(backButton, titleHeader);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.expand(titleHeader);

        add(headerLayout);
    }

    private void loadCounselingData() {
        Optional<Counseling> optionalCounseling = counselingService.findById(counselingId);
        if (optionalCounseling.isEmpty()) {
            Notification.show("상담을 찾을 수 없습니다", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            getUI().ifPresent(ui -> ui.navigate(CounselingListView.class));
            return;
        }

        counseling = optionalCounseling.get();
        refreshUI();
    }

    private void refreshData() {
        loadCounselingData();
    }

    private void refreshUI() {
        titleHeader.setText("상담 #" + counseling.getId());

        // Clear sections
        infoSection.removeAll();
        historySection.removeAll();
        commentSection.removeAll();

        // Build UI
        buildInfoSection();
        buildHistorySection();
        buildCommentSection();
    }

    private void buildInfoSection() {
        infoSection.setPadding(true);
        infoSection.getStyle()
            .set("background-color", "var(--lumo-contrast-5pct)")
            .set("border-radius", "var(--lumo-border-radius)");

        // 기본 정보
        HorizontalLayout row1 = new HorizontalLayout();
        row1.setWidthFull();
        row1.add(
            createInfoItem("고객명", counseling.getCustomerName()),
            createInfoItem("연락처", counseling.getContact()),
            createInfoItem("상담유형", counseling.getCounselingType())
        );

        // 상태 및 우선순위
        HorizontalLayout row2 = new HorizontalLayout();
        row2.setWidthFull();
        row2.add(
            createInfoItem("상태", getStatusBadge(counseling.getStatusEnum())),
            createInfoItem("우선순위", getPriorityBadge(counseling.getPriorityEnum())),
            createInfoItem("SLA 기한", formatSlaDate(counseling))
        );

        // 담당자 정보
        HorizontalLayout row3 = new HorizontalLayout();
        row3.setWidthFull();
        row3.add(
            createInfoItem("현재 담당자", getCounselorName(counseling.getCounselorId())),
            createInfoItem("에스컬레이션 레벨", String.valueOf(counseling.getEscalationLevel())),
            createInfoItem("이관 횟수", String.valueOf(counseling.getTransferCount()))
        );

        // 상담 내용
        Div contentDiv = new Div();
        contentDiv.getStyle().set("margin-top", "var(--lumo-space-m)");
        H4 contentTitle = new H4("상담 내용");
        Paragraph contentParagraph = new Paragraph(counseling.getContent());
        contentParagraph.getStyle()
            .set("white-space", "pre-wrap")
            .set("background-color", "var(--lumo-base-color)")
            .set("padding", "var(--lumo-space-m)")
            .set("border-radius", "var(--lumo-border-radius)");
        contentDiv.add(contentTitle, contentParagraph);

        // 액션 버튼
        HorizontalLayout actionButtons = createActionButtons();

        infoSection.add(row1, row2, row3, contentDiv, actionButtons);
    }

    private VerticalLayout createInfoItem(String label, String value) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-secondary-text-color)");

        Span valueSpan = new Span(value != null ? value : "-");
        valueSpan.getStyle().set("font-weight", "bold");

        layout.add(labelSpan, valueSpan);
        return layout;
    }

    private VerticalLayout createInfoItem(String label, Span valueBadge) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-secondary-text-color)");

        layout.add(labelSpan, valueBadge);
        return layout;
    }

    private Span getStatusBadge(CounselingStatus status) {
        Span badge = new Span(status != null ? status.getDescription() : "-");
        badge.getElement().getThemeList().add("badge");

        if (status != null) {
            switch (status) {
                case ESCALATED:
                    badge.getElement().getThemeList().add("error");
                    break;
                case RESOLVED:
                case CLOSED:
                    badge.getElement().getThemeList().add("success");
                    break;
                case IN_PROGRESS:
                    badge.getElement().getThemeList().add("primary");
                    break;
                default:
                    badge.getElement().getThemeList().add("contrast");
            }
        }

        return badge;
    }

    private Span getPriorityBadge(com.wrms.nt.domain.common.entity.Priority priority) {
        Span badge = new Span(priority != null ? priority.getDescription() : "-");
        badge.getElement().getThemeList().add("badge");

        if (priority != null) {
            switch (priority) {
                case URGENT:
                    badge.getElement().getThemeList().add("error");
                    badge.setText("🔴 " + priority.getDescription());
                    break;
                case HIGH:
                    badge.getElement().getThemeList().add("error");
                    badge.getElement().getStyle().set("background-color", "orange");
                    badge.setText("🟠 " + priority.getDescription());
                    break;
                case NORMAL:
                    badge.getElement().getThemeList().add("contrast");
                    break;
                case LOW:
                    badge.getElement().getThemeList().add("contrast");
                    badge.getElement().getStyle().set("opacity", "0.6");
                    break;
            }
        }

        return badge;
    }

    private String formatSlaDate(Counseling counseling) {
        if (counseling.getSlaDueDate() == null) {
            return "-";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatted = counseling.getSlaDueDate().format(formatter);

        if (counseling.isSlaViolated()) {
            return formatted + " ⚠️ 초과";
        }

        return formatted;
    }

    private String getCounselorName(Long counselorId) {
        if (counselorId == null) {
            return "미할당";
        }

        Optional<User> user = userService.findById(counselorId);
        return user.map(User::getName).orElse("알 수 없음");
    }

    private HorizontalLayout createActionButtons() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.getStyle().set("margin-top", "var(--lumo-space-m)");

        // 상태 변경 버튼
        ComboBox<CounselingStatus> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(CounselingStatus.values());
        statusComboBox.setItemLabelGenerator(CounselingStatus::getDescription);
        statusComboBox.setValue(counseling.getStatusEnum());
        statusComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null && !e.getValue().equals(e.getOldValue())) {
                changeStatus(e.getValue());
            }
        });

        // 이관 버튼
        Button transferButton = new Button("이관", VaadinIcon.EXCHANGE.create());
        transferButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        transferButton.addClickListener(e -> transferDialog.open(counseling));
        transferButton.setEnabled(counseling.canTransfer());

        // 에스컬레이션 버튼
        Button escalateButton = new Button("에스컬레이션", VaadinIcon.ARROW_UP.create());
        escalateButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        escalateButton.addClickListener(e -> escalationDialog.open(counseling));

        // 해결 완료 버튼
        Button resolveButton = new Button("해결 완료", VaadinIcon.CHECK.create());
        resolveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        resolveButton.addClickListener(e -> resolveCounseling());
        resolveButton.setEnabled(
            counseling.getStatusEnum() == CounselingStatus.IN_PROGRESS ||
            counseling.getStatusEnum() == CounselingStatus.ESCALATED
        );

        layout.add(statusComboBox, transferButton, escalateButton, resolveButton);
        return layout;
    }

    private void changeStatus(CounselingStatus newStatus) {
        try {
            Long currentUserId = counseling.getCounselorId();
            counselingService.updateStatus(counseling.getId(), newStatus, currentUserId);

            Notification.show("상태가 변경되었습니다", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            refreshData();
        } catch (Exception e) {
            Notification.show("상태 변경 중 오류가 발생했습니다: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void resolveCounseling() {
        try {
            Long currentUserId = counseling.getCounselorId();
            counselingService.resolveCounseling(counseling.getId(), "해결 완료", currentUserId);

            Notification.show("상담이 해결되었습니다", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            refreshData();
        } catch (Exception e) {
            Notification.show("해결 처리 중 오류가 발생했습니다: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void buildHistorySection() {
        H3 title = new H3("처리 이력");
        historySection.add(title);

        List<CounselingHistory> histories = historyService.findByCounselingId(counselingId);

        if (histories.isEmpty()) {
            historySection.add(new Paragraph("처리 이력이 없습니다."));
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (CounselingHistory history : histories) {
            Div historyItem = new Div();
            historyItem.getStyle()
                .set("padding", "var(--lumo-space-m)")
                .set("background-color", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius)")
                .set("margin-bottom", "var(--lumo-space-s)");

            String timestamp = history.getPerformedAt().format(formatter);
            String performer = getUserName(history.getPerformedBy());
            String action = history.getActionTypeEnum() != null ? history.getActionTypeEnum().getDescription() : "알 수 없음";

            Span header = new Span(timestamp + " - " + performer + " - " + action);
            header.getStyle().set("font-weight", "bold");

            historyItem.add(header);

            if (history.getComment() != null && !history.getComment().isEmpty()) {
                Paragraph comment = new Paragraph(history.getComment());
                comment.getStyle().set("margin-top", "var(--lumo-space-s)");
                historyItem.add(comment);
            }

            historySection.add(historyItem);
        }
    }

    private void buildCommentSection() {
        H3 title = new H3("메모 추가");

        TextArea commentArea = new TextArea();
        commentArea.setPlaceholder("처리 메모를 입력하세요...");
        commentArea.setWidthFull();
        commentArea.setHeight("100px");

        Button addButton = new Button("메모 추가", e -> addComment(commentArea.getValue(), commentArea));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        commentSection.add(title, commentArea, addButton);
    }

    private void addComment(String comment, TextArea commentArea) {
        if (comment == null || comment.trim().isEmpty()) {
            Notification.show("메모를 입력하세요", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            Long currentUserId = counseling.getCounselorId();
            counselingService.addComment(counseling.getId(), comment, currentUserId);

            Notification.show("메모가 추가되었습니다", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            commentArea.clear();
            refreshData();
        } catch (Exception e) {
            Notification.show("메모 추가 중 오류가 발생했습니다: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private String getUserName(Long userId) {
        if (userId == null) {
            return "시스템";
        }

        Optional<User> user = userService.findById(userId);
        return user.map(User::getName).orElse("알 수 없음");
    }
}
