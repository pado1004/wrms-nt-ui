package com.wrms.nt.views.counseling;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.wrms.nt.domain.counseling.entity.Counseling;
import com.wrms.nt.domain.counseling.entity.TransferReason;
import com.wrms.nt.domain.counseling.service.CounselingService;
import com.wrms.nt.domain.user.entity.User;
import com.wrms.nt.domain.user.service.UserService;

import java.util.List;

/**
 * 상담 이관 Dialog
 */
public class TransferDialog extends Dialog {

    private final CounselingService counselingService;
    private final UserService userService;
    private final Runnable onTransferCallback;

    private final ComboBox<User> targetUserComboBox = new ComboBox<>("이관 대상");
    private final ComboBox<TransferReason> reasonComboBox = new ComboBox<>("이관 사유");
    private final TextArea commentArea = new TextArea("이관 메모");
    private final Paragraph warningMessage = new Paragraph();

    private Counseling counseling;

    public TransferDialog(CounselingService counselingService, UserService userService, Runnable onTransferCallback) {
        this.counselingService = counselingService;
        this.userService = userService;
        this.onTransferCallback = onTransferCallback;

        setWidth("500px");
        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);

        createDialogLayout();
    }

    private void createDialogLayout() {
        H3 title = new H3("상담 이관");

        // Form Layout
        FormLayout formLayout = new FormLayout();
        configureFields();
        formLayout.add(
            targetUserComboBox,
            reasonComboBox,
            commentArea
        );

        // Warning Message
        warningMessage.getStyle()
            .set("color", "var(--lumo-error-text-color)")
            .set("background-color", "var(--lumo-error-color-10pct)")
            .set("padding", "var(--lumo-space-m)")
            .set("border-radius", "var(--lumo-border-radius)");
        warningMessage.setVisible(false);

        // Buttons
        Button transferButton = new Button("이관하기", e -> transfer());
        transferButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("취소", e -> close());

        HorizontalLayout buttonLayout = new HorizontalLayout(transferButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        add(title, formLayout, warningMessage, buttonLayout);
    }

    private void configureFields() {
        // 이관 대상 상담사
        targetUserComboBox.setItemLabelGenerator(user ->
            user.getName() + " (" + user.getDepartment() + ")"
        );
        targetUserComboBox.setWidthFull();
        targetUserComboBox.setRequired(true);

        // 이관 사유
        reasonComboBox.setItems(TransferReason.values());
        reasonComboBox.setItemLabelGenerator(TransferReason::getDescription);
        reasonComboBox.setWidthFull();
        reasonComboBox.setRequired(true);
        reasonComboBox.setValue(TransferReason.WORKLOAD_DISTRIBUTION);

        // 이관 메모
        commentArea.setPlaceholder("이관 사유를 상세히 입력하세요");
        commentArea.setWidthFull();
        commentArea.setHeight("120px");
    }

    public void open(Counseling counseling) {
        this.counseling = counseling;

        // 가용한 상담사 목록 로드
        List<User> availableUsers = userService.findActiveUsers();
        // 현재 담당자 제외
        availableUsers.removeIf(user -> user.getId().equals(counseling.getCounselorId()));
        targetUserComboBox.setItems(availableUsers);

        // 이관 횟수 확인 및 경고 표시
        int transferCount = counseling.getTransferCount() != null ? counseling.getTransferCount() : 0;
        if (transferCount >= 2) {
            warningMessage.setText("⚠️ 경고: 이미 " + transferCount + "회 이관되었습니다. " +
                "3회 초과 시 자동으로 관리자에게 에스컬레이션됩니다.");
            warningMessage.setVisible(true);
        } else {
            warningMessage.setVisible(false);
        }

        open();
    }

    private void transfer() {
        // Validation
        if (targetUserComboBox.isEmpty()) {
            Notification.show("이관 대상을 선택하세요", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (reasonComboBox.isEmpty()) {
            Notification.show("이관 사유를 선택하세요", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // TODO: 실제 로그인 사용자 ID로 변경 필요
            Long currentUserId = counseling.getCounselorId(); // 현재 담당자

            counselingService.transferCounseling(
                counseling.getId(),
                targetUserComboBox.getValue().getId(),
                reasonComboBox.getValue(),
                commentArea.getValue(),
                currentUserId
            );

            Notification.show("상담이 이관되었습니다", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            close();
            if (onTransferCallback != null) {
                onTransferCallback.run();
            }
        } catch (Exception e) {
            Notification.show("이관 중 오류가 발생했습니다: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
