package com.pedidos.api_pedidos.controller;

import com.pedidos.api_pedidos.dto.category.CategoryResponse;
import com.pedidos.api_pedidos.dto.order.CreateOrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderResponse;
import com.pedidos.api_pedidos.dto.product.ProductResponse;
import com.pedidos.api_pedidos.dto.tab.StartTabRequest;
import com.pedidos.api_pedidos.dto.tab.TabResponse;
import com.pedidos.api_pedidos.dto.table.TableResponse;
import com.pedidos.api_pedidos.service.CategoryService;
import com.pedidos.api_pedidos.service.OrderService;
import com.pedidos.api_pedidos.service.ProductService;
import com.pedidos.api_pedidos.service.TabService;
import com.pedidos.api_pedidos.service.TableService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/field")
@SecurityRequirements
@Tag(name = "Field", description = "Fluxo público utilizado pelo aplicativo de pedidos")
public class FieldController {

    private final TableService tableService;
    private final TabService tabService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;

    public FieldController(TableService tableService,
                           TabService tabService,
                           CategoryService categoryService,
                           ProductService productService,
                           OrderService orderService) {
        this.tableService = tableService;
        this.tabService = tabService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/tables/code/{code}")
    public TableResponse getTableByCode(@PathVariable String code) {
        return tableService.getByCode(code);
    }

    @PostMapping("/tabs/start")
    public TabResponse startTab(@RequestBody StartTabRequest request) {
        return tabService.startTab(request);
    }

    @GetMapping("/tabs/{id}")
    public TabResponse getTabById(@PathVariable Long id) {
        return tabService.getById(id);
    }

    @PatchMapping("/tabs/{id}/close")
    public TabResponse closeTab(@PathVariable Long id) {
        return tabService.closeTab(id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/categories/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @GetMapping("/products")
    public List<ProductResponse> getProducts(
            @RequestParam(value = "available", required = false) Boolean available) {
        return productService.getAll(available);
    }

    @GetMapping("/products/category/{categoryId}")
    public List<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "available", required = false) Boolean available) {
        return productService.getByCategory(categoryId, available);
    }

    @GetMapping("/products/search")
    public List<ProductResponse> searchProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "available", required = false) Boolean available) {
        return productService.search(keyword, categoryId, available);
    }

    @GetMapping("/products/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping("/orders")
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/orders/tab/{tabId}")
    public List<OrderResponse> getOrdersByTab(@PathVariable Long tabId) {
        return orderService.getTabOrders(tabId);
    }
}
