package com.wrms.newtype.ui.notification;

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
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.wrms.newtype.notification.api.dto.response.NotificationResponse;
import com.wrms.newtype.notification.api.service.NotificationCommandService;
import com.wrms.newtype.notification.api.service.NotificationQueryService;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 알림 패널
 *
 * 사용자의 알림 목록을 표시하고 관리하는 다이얼로그 컴포넌트입니다.
 */
public class NotificationPanel extends Dialog {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;
    private final Long userId;
    private final Runnable onUpdateCallback;

    private final VerticalLayout contentLayout = new VerticalLayout();
    private final Button markAllReadButton = new Button("모두 읽음 처리");

    public NotificationPanel(NotificationQueryService notificationQueryService,
                            NotificationCommandService notificationCommandService,
                            Long userId,
                            Runnable onUpdateCallback) {
        this.notificationQueryService = notificationQueryService;
        this.notificationCommandService = notificationCommandService;
        this.userId = userId;
        this.onUpdateCallback = onUpdateCallback;

        setWidth("500px");
        setHeight("600px");
        setCloseOnOutsideClick(true);
        setCloseOnEsc(true);

        createDialogLayout();
        loadNotifications();
    }

    private void createDialogLayout() {
        H3 title = new H3("알림");

        markAllReadButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        markAllReadButton.addClickListener(e -> {
            notificationCommandService.markAllAsReadByUserId(userId);
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
            loadNotifications();
        });

        HorizontalLayout header = new HorizontalLayout(title, markAllReadButton);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        contentLayout.setPadding(false);
        contentLayout.setSpacing(false);
        contentLayout.setWidthFull();

        add(header, contentLayout);
    }

    private void loadNotifications() {
        contentLayout.removeAll();

        List<NotificationResponse> notifications = notificationQueryService.findByUserId(userId);

        if (notifications.isEmpty()) {
            Span emptyMessage = new Span("알림이 없습니다.");
            emptyMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.MEDIUM);
            contentLayout.add(emptyMessage);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (NotificationResponse notification : notifications) {
            Component notificationItem = createNotificationItem(notification, formatter);
            contentLayout.add(notificationItem);
        }
    }

    private Component createNotificationItem(NotificationResponse notification, DateTimeFormatter formatter) {
        Div item = new Div();
        item.getStyle()
            .set("padding", "var(--lumo-space-m)")
            .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
            .set("cursor", "pointer");

        if (!notification.isRead()) {
            item.getStyle().set("background-color", "var(--lumo-primary-color-5pct)");
        }

        // 알림 타입 아이콘
        Icon icon = getNotificationIcon(notification.type());
        icon.setSize("20px");

        // 알림 내용
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        Span title = new Span(notification.title());
        title.getStyle().set("font-weight", "bold").set("font-size", "var(--lumo-font-size-m)");

        Span message = new Span(notification.message());
        message.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-secondary-text-color)");

        Span time = new Span(notification.createdAt().format(formatter));
        time.getStyle().set("font-size", "var(--lumo-font-size-xs)").set("color", "var(--lumo-tertiary-text-color)");

        content.add(title, message, time);

        // 상담 링크 (상담 관련 알림인 경우)
        HorizontalLayout layout = new HorizontalLayout(icon, content);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.START);
        layout.setWidthFull();
        layout.expand(content);

        if (notification.counselingId() != null) {
            Button link = new Button("상담 보기");
            link.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY, com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL);
            link.addClickListener(e -> {
                getUI().ifPresent(ui -> ui.navigate("counseling-detail/" + notification.counselingId()));
                close();
            });
            link.getStyle().set("font-size", "var(--lumo-font-size-xs)");
            content.add(link);
        }

        // 클릭 이벤트: 읽음 처리 및 상담 페이지로 이동
        item.addClickListener(e -> {
            if (!notification.isRead()) {
                notificationCommandService.markAsRead(notification.id());
                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }
                loadNotifications();
            }

            if (notification.counselingId() != null) {
                getUI().ifPresent(ui -> ui.navigate("counseling-detail/" + notification.counselingId()));
                close();
            }
        });

        item.add(layout);
        return item;
    }

    private Icon getNotificationIcon(com.wrms.newtype.notification.api.domain.NotificationType type) {
        return switch (type) {
            case ASSIGNED -> VaadinIcon.USER.create();
            case TRANSFERRED -> VaadinIcon.EXCHANGE.create();
            case ESCALATED -> VaadinIcon.ARROW_UP.create();
            case COMMENT_ADDED -> VaadinIcon.COMMENT.create();
            case SLA_WARNING, SLA_VIOLATED -> VaadinIcon.WARNING.create();
            case RESOLVED -> VaadinIcon.CHECK.create();
            case CLOSED -> VaadinIcon.CLOSE.create();
            default -> VaadinIcon.BELL.create();
        };
    }
}

