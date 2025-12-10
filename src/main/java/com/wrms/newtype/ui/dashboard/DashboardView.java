package com.wrms.newtype.ui.dashboard;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wrms.newtype.ui.layout.MainLayout;

/**
 * 대시보드 뷰
 *
 * 애플리케이션의 메인 화면으로, 주요 통계 및 요약 정보를 표시합니다.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("대시보드 | WRMS NT")
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        setSpacing(true);
        setPadding(true);

        H2 title = new H2("대시보드");
        Paragraph description = new Paragraph("상담 관리 시스템에 오신 것을 환영합니다.");

        // TODO: 통계 카드 추가
        Paragraph statsPlaceholder = new Paragraph(
            "이곳에 상담 통계, 최근 상담 내역, 차트 등이 표시됩니다."
        );

        add(title, description, statsPlaceholder);
    }
}

