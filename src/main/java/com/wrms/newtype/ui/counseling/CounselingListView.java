package com.wrms.newtype.ui.counseling;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wrms.newtype.counseling.api.domain.CounselingStatus;
import com.wrms.newtype.counseling.api.dto.response.CounselingResponse;
import com.wrms.newtype.counseling.api.service.CounselingQueryService;
import com.wrms.newtype.shared.domain.Priority;
import com.wrms.newtype.ui.layout.MainLayout;
import com.wrms.newtype.user.api.dto.response.UserResponse;
import com.wrms.newtype.user.api.service.UserQueryService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 상담 목록 뷰
 *
 * 상담 목록을 조회하고 관리하는 화면입니다.
 */
@Route(value = "counseling-list", layout = MainLayout.class)
@PageTitle("상담 관리 | WRMS NT")
public class CounselingListView extends VerticalLayout {

    private final CounselingQueryService counselingQueryService;
    private final UserQueryService userQueryService;
    private final Grid<CounselingResponse> grid = new Grid<>(CounselingResponse.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<String> statusFilter = new ComboBox<>();
    private final ComboBox<String> priorityFilter = new ComboBox<>();

    private CounselingFormDialog formDialog;

    public CounselingListView(CounselingQueryService counselingQueryService, UserQueryService userQueryService) {
        this.counselingQueryService = counselingQueryService;
        this.userQueryService = userQueryService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        configureToolbar();

        add(getToolbar(), grid);
        updateList();

        // Initialize dialog
        // TODO: CounselingCommandService 주입 필요
        formDialog = new CounselingFormDialog(counselingQueryService, this::updateList);
    }

    /**
     * Grid 설정
     */
    private void configureGrid() {
        grid.setSizeFull();

        // ID
        grid.addColumn(CounselingResponse::id)
            .setHeader("ID")
            .setAutoWidth(true)
            .setFlexGrow(0);

        // 고객명
        grid.addColumn(CounselingResponse::customerName)
            .setHeader("고객명")
            .setAutoWidth(true);

        // 상담유형
        grid.addColumn(CounselingResponse::counselingType)
            .setHeader("상담유형")
            .setAutoWidth(true);

        // 상태 (Badge)
        grid.addColumn(new ComponentRenderer<>(counseling -> {
            Span badge = new Span(counseling.status().getDescription());
            badge.getElement().getThemeList().add("badge");

            switch (counseling.status()) {
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

            return badge;
        })).setHeader("상태").setAutoWidth(true);

        // 우선순위 (Badge with icon)
        grid.addColumn(new ComponentRenderer<>(counseling -> {
            Priority priority = counseling.priority();
            Span badge = new Span(priority.getDescription());
            badge.getElement().getThemeList().add("badge");

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

            return badge;
        })).setHeader("우선순위").setAutoWidth(true);

        // 담당자
        grid.addColumn(counseling -> getCounselorName(counseling.counselorId()))
            .setHeader("담당자")
            .setAutoWidth(true);

        // SLA 기한 (with warning)
        grid.addColumn(new ComponentRenderer<>(counseling -> {
            if (counseling.slaDueDate() == null) {
                return new Span("-");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
            String formatted = counseling.slaDueDate().format(formatter);

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setAlignItems(Alignment.CENTER);

            Span dateSpan = new Span(formatted);

            // SLA 위반 경고 (임시로 false 처리, 나중에 CounselingResponse에 메서드 추가 필요)
            // if (counseling.isSlaViolated()) {
            //     Icon warningIcon = VaadinIcon.WARNING.create();
            //     warningIcon.setColor("var(--lumo-error-color)");
            //     warningIcon.setSize("16px");
            //     dateSpan.getStyle().set("color", "var(--lumo-error-color)");
            //     layout.add(warningIcon, dateSpan);
            // } else {
            layout.add(dateSpan);
            // }

            return layout;
        })).setHeader("SLA 기한").setAutoWidth(true);

        // 등록일
        grid.addColumn(counseling -> {
            if (counseling.createdAt() != null) {
                return counseling.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            return "";
        }).setHeader("등록일").setAutoWidth(true);

        // 상세 버튼
        grid.addColumn(new ComponentRenderer<>(counseling -> {
            Button viewButton = new Button("상세", VaadinIcon.EYE.create());
            viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            viewButton.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("counseling-detail/" + counseling.id()))
            );
            return viewButton;
        })).setHeader("").setAutoWidth(true).setFlexGrow(0);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    /**
     * 툴바 설정
     */
    private void configureToolbar() {
        // 검색
        searchField.setPlaceholder("고객명으로 검색...");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        // 상태 필터
        statusFilter.setPlaceholder("상태 필터");
        statusFilter.setItems("전체", "등록됨", "할당됨", "처리중", "이관됨", "에스컬레이션됨", "해결됨", "종료됨");
        statusFilter.setValue("전체");
        statusFilter.addValueChangeListener(e -> updateList());

        // 우선순위 필터
        priorityFilter.setPlaceholder("우선순위 필터");
        priorityFilter.setItems("전체", "긴급", "높음", "보통", "낮음");
        priorityFilter.setValue("전체");
        priorityFilter.addValueChangeListener(e -> updateList());
    }

    /**
     * 툴바 레이아웃
     */
    private HorizontalLayout getToolbar() {
        H2 title = new H2("상담 목록");

        Button addButton = new Button("상담 등록", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> formDialog.open(null));

        Button refreshButton = new Button("새로고침", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(
            title, searchField, statusFilter, priorityFilter, refreshButton, addButton
        );
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        return toolbar;
    }

    /**
     * 목록 업데이트
     */
    private void updateList() {
        List<CounselingResponse> counselings = counselingQueryService.findAll();

        // 검색 필터
        String searchTerm = searchField.getValue();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            counselings = counselingQueryService.searchByCustomerName(searchTerm);
        }

        // 상태 필터
        String statusValue = statusFilter.getValue();
        if (statusValue != null && !"전체".equals(statusValue)) {
            CounselingStatus status = mapStatusFromKorean(statusValue);
            if (status != null) {
                counselings = counselings.stream()
                    .filter(c -> c.status() == status)
                    .toList();
            }
        }

        // 우선순위 필터
        String priorityValue = priorityFilter.getValue();
        if (priorityValue != null && !"전체".equals(priorityValue)) {
            Priority priority = mapPriorityFromKorean(priorityValue);
            if (priority != null) {
                counselings = counselings.stream()
                    .filter(c -> c.priority() == priority)
                    .toList();
            }
        }

        grid.setItems(counselings);
    }

    private String getCounselorName(Long counselorId) {
        if (counselorId == null) {
            return "미할당";
        }

        Optional<UserResponse> user = userQueryService.findById(counselorId);
        return user.map(UserResponse::name).orElse("알 수 없음");
    }

    private CounselingStatus mapStatusFromKorean(String korean) {
        switch (korean) {
            case "등록됨": return CounselingStatus.REGISTERED;
            case "할당됨": return CounselingStatus.ASSIGNED;
            case "처리중": return CounselingStatus.IN_PROGRESS;
            case "이관됨": return CounselingStatus.TRANSFERRED;
            case "에스컬레이션됨": return CounselingStatus.ESCALATED;
            case "해결됨": return CounselingStatus.RESOLVED;
            case "종료됨": return CounselingStatus.CLOSED;
            default: return null;
        }
    }

    private Priority mapPriorityFromKorean(String korean) {
        switch (korean) {
            case "긴급": return Priority.URGENT;
            case "높음": return Priority.HIGH;
            case "보통": return Priority.NORMAL;
            case "낮음": return Priority.LOW;
            default: return null;
        }
    }
}

