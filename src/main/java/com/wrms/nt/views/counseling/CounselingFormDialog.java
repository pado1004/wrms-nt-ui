package com.wrms.nt.views.counseling;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.wrms.nt.domain.common.entity.Priority;
import com.wrms.nt.domain.counseling.entity.Counseling;
import com.wrms.nt.domain.counseling.service.CounselingService;

/**
 * 상담 등록/수정 Dialog
 */
public class CounselingFormDialog extends Dialog {

    private final CounselingService counselingService;
    private final Runnable onSaveCallback;

    // Form Fields
    private final TextField customerNameField = new TextField("고객명");
    private final TextField contactField = new TextField("연락처");
    private final ComboBox<String> counselingTypeComboBox = new ComboBox<>("상담유형");
    private final ComboBox<Priority> priorityComboBox = new ComboBox<>("우선순위");
    private final TextArea contentArea = new TextArea("상담내용");

    private Counseling counseling;

    public CounselingFormDialog(CounselingService counselingService, Runnable onSaveCallback) {
        this.counselingService = counselingService;
        this.onSaveCallback = onSaveCallback;

        setWidth("600px");
        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);

        createDialogLayout();
    }

    private void createDialogLayout() {
        H3 title = new H3("상담 등록");

        // Form Layout
        FormLayout formLayout = new FormLayout();
        configureFields();
        formLayout.add(
            customerNameField,
            contactField,
            counselingTypeComboBox,
            priorityComboBox,
            contentArea
        );
        formLayout.setColspan(contentArea, 2);

        // Buttons
        Button saveButton = new Button("저장", e -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("취소", e -> close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        add(title, formLayout, buttonLayout);
    }

    private void configureFields() {
        // 고객명 (필수)
        customerNameField.setRequired(true);
        customerNameField.setPlaceholder("고객명을 입력하세요");
        customerNameField.setWidthFull();

        // 연락처
        contactField.setPlaceholder("010-1234-5678");
        contactField.setWidthFull();

        // 상담유형
        counselingTypeComboBox.setItems(
            "제품문의",
            "불만접수",
            "기술지원",
            "환불요청",
            "기타"
        );
        counselingTypeComboBox.setWidthFull();
        counselingTypeComboBox.setValue("제품문의");

        // 우선순위
        priorityComboBox.setItems(Priority.values());
        priorityComboBox.setItemLabelGenerator(priority ->
            priority.getDescription() + " (" + priority.getSlaHours() + "시간)"
        );
        priorityComboBox.setWidthFull();
        priorityComboBox.setValue(Priority.NORMAL);

        // 상담내용 (필수)
        contentArea.setRequired(true);
        contentArea.setPlaceholder("상담 내용을 입력하세요");
        contentArea.setWidthFull();
        contentArea.setHeight("200px");
    }

    public void open(Counseling counseling) {
        this.counseling = counseling;
        if (counseling != null && counseling.getId() != null) {
            // 수정 모드
            ((H3) getChildren().findFirst().orElse(new H3())).setText("상담 수정");
            populateForm(counseling);
        } else {
            // 등록 모드
            ((H3) getChildren().findFirst().orElse(new H3())).setText("상담 등록");
            clearForm();
        }
        open();
    }

    private void populateForm(Counseling counseling) {
        customerNameField.setValue(counseling.getCustomerName() != null ? counseling.getCustomerName() : "");
        contactField.setValue(counseling.getContact() != null ? counseling.getContact() : "");
        counselingTypeComboBox.setValue(counseling.getCounselingType() != null ? counseling.getCounselingType() : "제품문의");
        priorityComboBox.setValue(counseling.getPriorityEnum() != null ? counseling.getPriorityEnum() : Priority.NORMAL);
        contentArea.setValue(counseling.getContent() != null ? counseling.getContent() : "");
    }

    private void clearForm() {
        customerNameField.clear();
        contactField.clear();
        counselingTypeComboBox.setValue("제품문의");
        priorityComboBox.setValue(Priority.NORMAL);
        contentArea.clear();
    }

    private void save() {
        // Validation
        if (customerNameField.isEmpty()) {
            Notification.show("고객명을 입력하세요", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (contentArea.isEmpty()) {
            Notification.show("상담 내용을 입력하세요", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // TODO: 실제 로그인 사용자 ID로 변경 필요
            Long currentUserId = 1L; // 임시로 관리자 ID 사용

            if (counseling == null || counseling.getId() == null) {
                // 새 상담 등록
                counselingService.createCounseling(
                    customerNameField.getValue(),
                    contactField.getValue(),
                    counselingTypeComboBox.getValue(),
                    contentArea.getValue(),
                    priorityComboBox.getValue(),
                    currentUserId
                );

                Notification.show("상담이 등록되었습니다", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                // 상담 수정
                counseling.setCustomerName(customerNameField.getValue());
                counseling.setContact(contactField.getValue());
                counseling.setCounselingType(counselingTypeComboBox.getValue());
                counseling.setPriorityEnum(priorityComboBox.getValue());
                counseling.setContent(contentArea.getValue());
                counselingService.save(counseling);

                Notification.show("상담이 수정되었습니다", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }

            close();
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
        } catch (Exception e) {
            Notification.show("저장 중 오류가 발생했습니다: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
