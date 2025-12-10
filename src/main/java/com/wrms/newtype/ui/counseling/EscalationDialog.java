package com.wrms.newtype.ui.counseling;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.wrms.newtype.counseling.api.dto.request.EscalateCounselingRequest;
import com.wrms.newtype.counseling.api.dto.response.CounselingResponse;
import com.wrms.newtype.counseling.api.service.CounselingCommandService;

/**
 * 에스컬레이션 Dialog
 */
public class EscalationDialog extends Dialog {

    private final CounselingCommandService counselingCommandService;
    private final Runnable onEscalationCallback;

    private final TextField reasonField = new TextField("에스컬레이션 사유");
    private final TextArea summaryArea = new TextArea("현재까지 처리 내용");
    private final TextArea proposalArea = new TextArea("제안 해결 방안");
    private final Paragraph infoMessage = new Paragraph();

    private CounselingResponse counseling;

    public EscalationDialog(CounselingCommandService counselingCommandService, Runnable onEscalationCallback) {
        this.counselingCommandService = counselingCommandService;
        this.onEscalationCallback = onEscalationCallback;

        setWidth("600px");
        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);

        createDialogLayout();
    }

    private void createDialogLayout() {
        H3 title = new H3("에스컬레이션");

        // Form Layout
        FormLayout formLayout = new FormLayout();
        configureFields();
        formLayout.add(
            reasonField,
            summaryArea,
            proposalArea
        );

        // Info Message
        infoMessage.getStyle()
            .set("color", "var(--lumo-primary-text-color)")
            .set("background-color", "var(--lumo-primary-color-10pct)")
            .set("padding", "var(--lumo-space-m)")
            .set("border-radius", "var(--lumo-border-radius)");
        infoMessage.setText("⚠️ 에스컬레이션 시 우선순위가 자동으로 상향 조정됩니다.");

        // Buttons
        Button escalateButton = new Button("에스컬레이션", e -> escalate());
        escalateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("취소", e -> close());

        HorizontalLayout buttonLayout = new HorizontalLayout(escalateButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        add(title, formLayout, infoMessage, buttonLayout);
    }

    private void configureFields() {
        // 에스컬레이션 사유
        reasonField.setPlaceholder("예: 복잡한 이슈, 정책 결정 필요, VIP 고객");
        reasonField.setWidthFull();
        reasonField.setRequired(true);

        // 현재까지 처리 내용
        summaryArea.setPlaceholder("지금까지 어떤 처리를 했는지 요약해주세요");
        summaryArea.setWidthFull();
        summaryArea.setHeight("120px");
        summaryArea.setRequired(true);

        // 제안 해결 방안
        proposalArea.setPlaceholder("어떻게 해결하면 좋을지 제안해주세요 (선택사항)");
        proposalArea.setWidthFull();
        proposalArea.setHeight("100px");
    }

    public void open(CounselingResponse counseling) {
        this.counseling = counseling;

        // 현재 레벨 표시
        int currentLevel = counseling.escalationLevel() != null ? counseling.escalationLevel() : 0;
        String levelInfo = String.format("현재 레벨: %d → 에스컬레이션 후: %d", currentLevel, currentLevel + 1);

        Paragraph levelParagraph = new Paragraph(levelInfo);
        levelParagraph.getStyle().set("font-weight", "bold");

        // 기존 infoMessage 위에 레벨 정보 추가
        if (getChildren().toList().size() > 2) {
            remove(getChildren().toList().get(2));
        }
        addComponentAtIndex(2, levelParagraph);

        open();
    }

    private void escalate() {
        // Validation
        if (reasonField.isEmpty()) {
            Notification.show("에스컬레이션 사유를 입력하세요", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (summaryArea.isEmpty()) {
            Notification.show("현재까지 처리 내용을 입력하세요", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // TODO: 실제 로그인 사용자 ID로 변경 필요
            Long currentUserId = counseling.counselorId(); // 현재 담당자

            String comment = summaryArea.getValue();
            if (!proposalArea.isEmpty()) {
                comment += "\n\n[제안 해결 방안]\n" + proposalArea.getValue();
            }

            EscalateCounselingRequest request = new EscalateCounselingRequest(
                reasonField.getValue(),
                comment,
                currentUserId
            );
            
            counselingCommandService.escalate(counseling.id(), request);

            Notification.show("에스컬레이션이 완료되었습니다", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            close();
            if (onEscalationCallback != null) {
                onEscalationCallback.run();
            }
        } catch (Exception e) {
            Notification.show("에스컬레이션 중 오류가 발생했습니다: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}

