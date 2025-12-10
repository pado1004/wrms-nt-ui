package com.wrms.newtype.ui.customer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wrms.newtype.customer.api.dto.response.CustomerResponse;
import com.wrms.newtype.customer.api.service.CustomerQueryService;
import com.wrms.newtype.ui.layout.MainLayout;

import java.time.format.DateTimeFormatter;

/**
 * 고객 목록 뷰
 *
 * 고객 목록을 조회하고 관리하는 화면입니다.
 */
@Route(value = "customer-list", layout = MainLayout.class)
@PageTitle("고객 관리 | WRMS NT")
public class CustomerListView extends VerticalLayout {

    private final CustomerQueryService customerQueryService;
    private final Grid<CustomerResponse> grid = new Grid<>(CustomerResponse.class, false);
    private final TextField searchField = new TextField();

    public CustomerListView(CustomerQueryService customerQueryService) {
        this.customerQueryService = customerQueryService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

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

        grid.addColumn(CustomerResponse::id)
            .setHeader("ID")
            .setAutoWidth(true)
            .setFlexGrow(0);

        grid.addColumn(CustomerResponse::name)
            .setHeader("이름")
            .setAutoWidth(true);

        grid.addColumn(CustomerResponse::email)
            .setHeader("이메일")
            .setAutoWidth(true);

        grid.addColumn(CustomerResponse::phone)
            .setHeader("전화번호")
            .setAutoWidth(true);

        grid.addColumn(CustomerResponse::address)
            .setHeader("주소")
            .setAutoWidth(true);

        grid.addColumn(customer -> {
            if (customer.createdAt() != null) {
                return customer.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            return "";
        }).setHeader("등록일").setAutoWidth(true);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    /**
     * 툴바 설정
     */
    private void configureToolbar() {
        searchField.setPlaceholder("이름으로 검색...");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
    }

    /**
     * 툴바 레이아웃
     */
    private HorizontalLayout getToolbar() {
        H2 title = new H2("고객 목록");

        Button refreshButton = new Button("새로고침", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(title, searchField, refreshButton);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        return toolbar;
    }

    /**
     * 목록 업데이트
     */
    private void updateList() {
        String searchTerm = searchField.getValue();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            grid.setItems(customerQueryService.searchByName(searchTerm));
        } else {
            grid.setItems(customerQueryService.findAll());
        }
    }
}

