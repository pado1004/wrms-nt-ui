package com.wrms.nt.views.notification;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.wrms.nt.domain.notification.entity.Notification;
import com.wrms.nt.domain.notification.entity.NotificationType;
import com.wrms.nt.domain.notification.service.NotificationService;
import com.wrms.nt.views.counseling.CounselingDetailView;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 알림 패널
 *
 * 사용자의 알림 목록을 표시하고 관리하는 다이얼로그 컴포넌트입니다.
 */
public class NotificationPanel extends Dialog {

    private final NotificationService notificationService;
    private final Long userId;
    private final VerticalLayout notificationList;
    private final Runnable onUpdateCallback;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public NotificationPanel(NotificationService notificationService, Long userId, Runnable onUpdateCallback) {
        this.notificationService = notificationService;
        this.userId = userId;
        this.onUpdateCallback = onUpdateCallback;
        this.notificationList = new VerticalLayout();

        setHeaderTitle("알림");
        setWidth("500px");
        setMaxHeight("600px");

        createContent();
        createFooter();
        loadNotifications();
    }

    /**
     * 컨텐츠 영역 생성
     */
    private void createContent() {
        notificationList.setPadding(false);
        notificationList.setSpacing(false);
        add(notificationList);
    }

    /**
     * 푸터 생성
     */
    private void createFooter() {
        Button markAllReadBtn = new Button("모두 읽음으로 표시", e -> {
            notificationService.markAllAsReadByUserId(userId);
            loadNotifications();
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        });
        markAllReadBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        Button closeBtn = new Button("닫기", e -> close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout footer = new HorizontalLayout(markAllReadBtn, closeBtn);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setWidthFull();

        getFooter().add(footer);
    }

    /**
     * 알림 목록 로드
     */
    private void loadNotifications() {
        notificationList.removeAll();

        List<Notification> notifications = notificationService.findByUserId(userId);

        if (notifications.isEmpty()) {
            Span emptyMessage = new Span("알림이 없습니다.");
            emptyMessage.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.Padding.LARGE
            );
            notificationList.add(emptyMessage);
            return;
        }

        for (Notification notification : notifications) {
            notificationList.add(createNotificationItem(notification));
        }
    }

    /**
     * 알림 아이템 생성
     */
    private Component createNotificationItem(Notification notification) {
        HorizontalLayout item = new HorizontalLayout();
        item.setWidthFull();
        item.setPadding(true);
        item.setSpacing(true);
        item.addClassName("notification-item");
        item.getStyle()
            .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
            .set("cursor", "pointer");

        // 읽지 않은 알림 강조
        if (!notification.getIsRead()) {
            item.getStyle().set("background-color", "var(--lumo-primary-color-10pct)");
        }

        // 타입별 아이콘
        Icon icon = getIconForType(notification.getTypeEnum());
        icon.addClassName(LumoUtility.IconSize.MEDIUM);

        // 알림 내용
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        Span title = new Span(notification.getTitle());
        title.addClassName(LumoUtility.FontWeight.SEMIBOLD);

        Span message = new Span(notification.getMessage());
        message.addClassName(LumoUtility.TextColor.SECONDARY);
        message.getStyle().set("font-size", "var(--lumo-font-size-s)");

        Span time = new Span(notification.getCreatedAt().format(TIME_FORMATTER));
        time.addClassName(LumoUtility.TextColor.TERTIARY);
        time.getStyle().set("font-size", "var(--lumo-font-size-xs)");

        content.add(title, message, time);

        // 읽음 표시 아이콘
        Div readIndicator = new Div();
        if (!notification.getIsRead()) {
            readIndicator.getStyle()
                .set("width", "8px")
                .set("height", "8px")
                .set("border-radius", "50%")
                .set("background-color", "var(--lumo-primary-color)")
                .set("margin-left", "auto");
        }

        item.add(icon, content, readIndicator);
        item.setFlexGrow(1, content);
        item.setAlignItems(FlexComponent.Alignment.START);

        // 클릭 이벤트: 읽음 처리 및 상담 상세로 이동
        item.addClickListener(e -> {
            if (!notification.getIsRead()) {
                notificationService.markAsRead(notification.getId());
                loadNotifications();
                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }
            }

            // 상담 ID가 있으면 상세 페이지로 이동
            if (notification.getCounselingId() != null) {
                close();
                item.getUI().ifPresent(ui ->
                    ui.navigate(CounselingDetailView.class, notification.getCounselingId())
                );
            }
        });

        return item;
    }

    /**
     * 알림 타입별 아이콘 반환
     */
    private Icon getIconForType(NotificationType type) {
        if (type == null) {
            return VaadinIcon.INFO_CIRCLE.create();
        }

        return switch (type) {
            case ASSIGNED -> VaadinIcon.USER_CHECK.create();
            case TRANSFERRED -> VaadinIcon.EXCHANGE.create();
            case ESCALATED -> VaadinIcon.ARROW_UP.create();
            case STATUS_CHANGED -> VaadinIcon.REFRESH.create();
            case COMMENT_ADDED -> VaadinIcon.COMMENT.create();
            case SLA_WARNING -> VaadinIcon.WARNING.create();
            case SLA_VIOLATED -> VaadinIcon.EXCLAMATION_CIRCLE.create();
            case RESOLVED -> VaadinIcon.CHECK_CIRCLE.create();
            case CLOSED -> VaadinIcon.LOCK.create();
        };
    }
}
