package com.wrms.nt.views.counseling;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wrms.nt.domain.counseling.entity.Counseling;
import com.wrms.nt.domain.counseling.service.CounselingService;
import com.wrms.nt.views.MainLayout;

import java.time.format.DateTimeFormatter;

/**
 * 상담 목록 뷰
 *
 * 상담 목록을 조회하고 관리하는 화면입니다.
 */
@Route(value = "counseling", layout = MainLayout.class)
@PageTitle("상담 관리 | WRMS NT")
public class CounselingListView extends VerticalLayout {

    private final CounselingService counselingService;
    private final Grid<Counseling> grid = new Grid<>(Counseling.class, false);
    private final TextField searchField = new TextField();

    public CounselingListView(CounselingService counselingService) {
        this.counselingService = counselingService;

        setSizeFull();
        configureGrid();
        configureToolbar();

        add(getToolbar(), grid);
        updateList();
    }

    /**
     * Grid 설정
     */
    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Counseling::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Counseling::getCustomerName).setHeader("고객명").setAutoWidth(true);
        grid.addColumn(Counseling::getContact).setHeader("연락처").setAutoWidth(true);
        grid.addColumn(Counseling::getCounselingType).setHeader("상담유형").setAutoWidth(true);
        grid.addColumn(Counseling::getStatus).setHeader("상태").setAutoWidth(true);
        grid.addColumn(counseling -> {
            if (counseling.getCreatedAt() != null) {
                return counseling.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            return "";
        }).setHeader("등록일").setAutoWidth(true);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> {
            // TODO: 상세 화면으로 이동 또는 편집 다이얼로그 열기
        });
    }

    /**
     * 툴바 설정
     */
    private void configureToolbar() {
        searchField.setPlaceholder("고객명으로 검색...");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
    }

    /**
     * 툴바 레이아웃
     */
    private HorizontalLayout getToolbar() {
        H2 title = new H2("상담 목록");

        Button addButton = new Button("상담 등록", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
            // TODO: 상담 등록 다이얼로그 열기
        });

        HorizontalLayout toolbar = new HorizontalLayout(title, searchField, addButton);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.expand(searchField);

        return toolbar;
    }

    /**
     * 목록 업데이트
     */
    private void updateList() {
        String searchTerm = searchField.getValue();
        if (searchTerm == null || searchTerm.isEmpty()) {
            grid.setItems(counselingService.findAll());
        } else {
            grid.setItems(counselingService.searchByCustomerName(searchTerm));
        }
    }
}
