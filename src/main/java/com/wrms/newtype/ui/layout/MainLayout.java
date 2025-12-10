package com.wrms.newtype.ui.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.wrms.newtype.notification.api.service.NotificationCommandService;
import com.wrms.newtype.notification.api.service.NotificationQueryService;
import com.wrms.newtype.ui.counseling.CounselingListView;
import com.wrms.newtype.ui.customer.CustomerListView;
import com.wrms.newtype.ui.dashboard.DashboardView;
import com.wrms.newtype.ui.notification.NotificationPanel;

/**
 * 메인 레이아웃
 *
 * 애플리케이션의 전체 레이아웃을 담당합니다.
 * 헤더, 사이드 네비게이션, 메인 콘텐츠 영역으로 구성됩니다.
 */
@PageTitle("WRMS NT")
@SpringComponent
@UIScope
public class MainLayout extends AppLayout {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;
    private Span notificationBadge;

    // TODO: 실제 인증 구현 시 변경 필요
    private static final Long CURRENT_USER_ID = 1L;

    public MainLayout(NotificationQueryService notificationQueryService,
                      NotificationCommandService notificationCommandService) {
        this.notificationQueryService = notificationQueryService;
        this.notificationCommandService = notificationCommandService;
        createHeader();
        createDrawer();
    }

    /**
     * 헤더 생성
     */
    private void createHeader() {
        H1 logo = new H1("WRMS NT");
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM
        );

        // 알림 버튼 생성
        Div notificationBtnWrapper = createNotificationButton();

        // 사용자 정보 표시 (추후 인증 기능 추가시 사용)
        Span userInfo = new Span("관리자");
        userInfo.addClassNames(LumoUtility.Margin.MEDIUM);

        // 오른쪽 영역 (알림 + 사용자 정보)
        HorizontalLayout rightSection = new HorizontalLayout(notificationBtnWrapper, userInfo);
        rightSection.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        rightSection.setSpacing(true);

        var header = new HorizontalLayout(new DrawerToggle(), logo, rightSection);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(logo);  // logo가 중간 공간을 차지하도록
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM
        );

        addToNavbar(header);
    }

    /**
     * 알림 버튼 생성
     */
    private Div createNotificationButton() {
        // 알림 아이콘
        Icon bellIcon = VaadinIcon.BELL.create();
        bellIcon.addClassName(LumoUtility.IconSize.MEDIUM);

        // 버튼 생성
        Button notificationBtn = new Button(bellIcon);
        notificationBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Badge 생성 (읽지 않은 알림 개수)
        notificationBadge = new Span();
        notificationBadge.getStyle()
            .set("position", "absolute")
            .set("top", "0")
            .set("right", "0")
            .set("background-color", "var(--lumo-error-color)")
            .set("color", "white")
            .set("border-radius", "50%")
            .set("min-width", "18px")
            .set("height", "18px")
            .set("padding", "2px")
            .set("font-size", "10px")
            .set("font-weight", "bold")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center");

        // Badge를 버튼에 추가
        Div buttonWrapper = new Div(notificationBtn, notificationBadge);
        buttonWrapper.getStyle().set("position", "relative");

        // 초기 Badge 업데이트
        updateNotificationBadge();

        // 버튼 클릭 이벤트: 알림 패널 열기
        notificationBtn.addClickListener(e -> {
            NotificationPanel panel = new NotificationPanel(
                notificationQueryService,
                notificationCommandService,
                CURRENT_USER_ID,
                this::updateNotificationBadge
            );
            panel.open();
        });

        return buttonWrapper;
    }

    /**
     * 알림 Badge 업데이트
     */
    private void updateNotificationBadge() {
        int unreadCount = notificationQueryService.countUnreadByUserId(CURRENT_USER_ID);

        if (unreadCount > 0) {
            notificationBadge.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
    }

    /**
     * 사이드 네비게이션 생성
     */
    private void createDrawer() {
        SideNav nav = new SideNav();

        // 대시보드 메뉴
        nav.addItem(new SideNavItem(
            "대시보드",
            DashboardView.class,
            VaadinIcon.DASHBOARD.create()
        ));

        // 상담 관리 메뉴
        nav.addItem(new SideNavItem(
            "상담 관리",
            CounselingListView.class,
            VaadinIcon.COMMENTS.create()
        ));

        // 고객 관리 메뉴
        nav.addItem(new SideNavItem(
            "고객 관리",
            CustomerListView.class,
            VaadinIcon.USERS.create()
        ));

        // 통계 메뉴 (추후 구현)
        SideNavItem statsItem = new SideNavItem("통계");
        statsItem.setPrefixComponent(VaadinIcon.BAR_CHART.create());
        statsItem.setEnabled(false);
        nav.addItem(statsItem);

        // 설정 메뉴 (추후 구현)
        SideNavItem settingsItem = new SideNavItem("설정");
        settingsItem.setPrefixComponent(VaadinIcon.COG.create());
        settingsItem.setEnabled(false);
        nav.addItem(settingsItem);

        addToDrawer(nav);
    }
}

