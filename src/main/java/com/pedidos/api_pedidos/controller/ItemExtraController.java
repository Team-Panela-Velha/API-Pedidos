package com.pedidos.api_pedidos.controller;

import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.item_extra.ItemExtraRequest;
import com.pedidos.api_pedidos.dto.item_extra.ItemExtraResponse;
import com.pedidos.api_pedidos.service.ItemExtraService;

import java.util.List;

@RestController
@RequestMapping("/item-extras")
public class ItemExtraController {

    private final ItemExtraService service;

    public ItemExtraController(ItemExtraService service) {
        this.service = service;
    }

    @PostMapping
    public ItemExtraResponse create(@RequestBody ItemExtraRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ItemExtraResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ItemExtraResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/by-order-item/{orderItemId}")
    public List<ItemExtraResponse> getByOrderItemId(@PathVariable Long orderItemId) {
        return service.getByOrderItemId(orderItemId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
