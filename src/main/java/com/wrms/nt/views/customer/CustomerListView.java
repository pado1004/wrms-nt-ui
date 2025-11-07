package com.wrms.nt.views.customer;

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
import com.wrms.nt.domain.customer.entity.Customer;
import com.wrms.nt.domain.customer.service.CustomerService;
import com.wrms.nt.views.MainLayout;

import java.time.format.DateTimeFormatter;

/**
 * 고객 목록 뷰
 *
 * 고객 목록을 조회하고 관리하는 화면입니다.
 */
@Route(value = "customer", layout = MainLayout.class)
@PageTitle("고객 관리 | WRMS NT")
public class CustomerListView extends VerticalLayout {

    private final CustomerService customerService;
    private final Grid<Customer> grid = new Grid<>(Customer.class, false);
    private final TextField searchField = new TextField();

    public CustomerListView(CustomerService customerService) {
        this.customerService = customerService;

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
        grid.addColumn(Customer::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Customer::getName).setHeader("고객명").setAutoWidth(true);
        grid.addColumn(Customer::getEmail).setHeader("이메일").setAutoWidth(true);
        grid.addColumn(Customer::getPhone).setHeader("전화번호").setAutoWidth(true);
        grid.addColumn(Customer::getAddress).setHeader("주소").setAutoWidth(true);
        grid.addColumn(customer -> {
            if (customer.getCreatedAt() != null) {
                return customer.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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
        H2 title = new H2("고객 목록");

        Button addButton = new Button("고객 등록", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
            // TODO: 고객 등록 다이얼로그 열기
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
            grid.setItems(customerService.findAll());
        } else {
            grid.setItems(customerService.searchByName(searchTerm));
        }
    }
}
