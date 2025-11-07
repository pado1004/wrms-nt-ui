package com.wrms.nt.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.wrms.nt.views.counseling.CounselingListView;
import com.wrms.nt.views.customer.CustomerListView;
import com.wrms.nt.views.dashboard.DashboardView;

/**
 * 메인 레이아웃
 *
 * 애플리케이션의 전체 레이아웃을 담당합니다.
 * 헤더, 사이드 네비게이션, 메인 콘텐츠 영역으로 구성됩니다.
 */
@PageTitle("WRMS NT")
public class MainLayout extends AppLayout {

    public MainLayout() {
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

        // 사용자 정보 표시 (추후 인증 기능 추가시 사용)
        Span userInfo = new Span("관리자");
        userInfo.addClassNames(LumoUtility.Margin.MEDIUM);

        var header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM
        );

        addToNavbar(header);
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
        SideNavItem statsItem = new SideNavItem("통계", null, VaadinIcon.BAR_CHART.create());
        statsItem.setEnabled(false);
        nav.addItem(statsItem);

        // 설정 메뉴 (추후 구현)
        SideNavItem settingsItem = new SideNavItem("설정", null, VaadinIcon.COG.create());
        settingsItem.setEnabled(false);
        nav.addItem(settingsItem);

        addToDrawer(nav);
    }
}
