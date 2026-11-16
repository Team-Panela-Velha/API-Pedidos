package com.pedidos.api_pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.pedidos.api_pedidos.exception.ConflictException;

import com.pedidos.api_pedidos.domain.entity.*;
import com.pedidos.api_pedidos.dto.order.CreateOrderRequest;
import com.pedidos.api_pedidos.dto.order_item.OrderItemRequest;
import com.pedidos.api_pedidos.repository.*;
import com.pedidos.api_pedidos.service.*;

class OrderFlowTests {
    @Test
    void markingDeliveredIsIdempotentAndUsesServerTimestamp() {
        OrderRepository orders = mock(OrderRepository.class);
        TabRepository tabs = mock(TabRepository.class);
        OrderItemRepository items = mock(OrderItemRepository.class);
        TabEntity tab = new TabEntity(BigDecimal.ZERO, new TableEntity("A1"));
        tab.setId(1L);
        OrderEntity order = new OrderEntity(tab);
        order.setId(2L);
        when(orders.findByIdForUpdate(2L)).thenReturn(Optional.of(order));
        when(orders.save(order)).thenReturn(order);
        when(tabs.findByIdForUpdate(1L)).thenReturn(Optional.of(tab));
        when(items.findByOrderId(2L)).thenReturn(List.of());
        OrderService service = new OrderService(orders, tabs, items, mock(ItemExtraRepository.class),
                mock(ProductRepository.class), mock(ExtraRepository.class), mock(ProductExtraRepository.class),
                mock(FcmService.class), mock(TabService.class));

        service.markDelivered(2L);
        Instant firstTimestamp = order.getDeliveredAt();
        service.markDelivered(2L);

        assertEquals(Boolean.TRUE, order.getDelivered());
        assertNotNull(firstTimestamp);
        assertEquals(firstTimestamp, order.getDeliveredAt());
        verify(orders, times(1)).save(order);
        verify(orders, times(2)).findByIdForUpdate(2L);
        verify(tabs, times(2)).findByIdForUpdate(1L);
    }

    @Test
    void closingTabWithPendingOrdersIsRejectedAndUsesTabLock() {
        TabRepository tabs = mock(TabRepository.class);
        OrderRepository orders = mock(OrderRepository.class);
        TabEntity tab = new TabEntity(BigDecimal.ZERO, new TableEntity("A1"));
        tab.setId(1L);
        when(tabs.findByIdForUpdate(1L)).thenReturn(Optional.of(tab));
        when(orders.countByTabIdAndDeliveredFalse(1L)).thenReturn(2L);
        TabService service = new TabService(tabs, mock(TableRepository.class), orders,
                mock(OrderItemRepository.class), mock(ItemExtraRepository.class), mock(UserRepository.class),
                () -> Optional.empty());

        ConflictException error = assertThrows(ConflictException.class, () -> service.closeTab(1L));

        assertEquals("A comanda possui 2 pedido(s) não entregue(s)", error.getMessage());
        verify(tabs).findByIdForUpdate(1L);
        verify(tabs, never()).save(any());
    }

    @Test
    void closingTabWithoutOrdersIsAllowed() {
        TabRepository tabs = mock(TabRepository.class);
        OrderRepository orders = mock(OrderRepository.class);
        TabEntity tab = new TabEntity(BigDecimal.ZERO, new TableEntity("A1"));
        tab.setId(1L);
        when(tabs.findByIdForUpdate(1L)).thenReturn(Optional.of(tab));
        when(orders.countByTabIdAndDeliveredFalse(1L)).thenReturn(0L);
        when(tabs.save(tab)).thenReturn(tab);
        TabService service = new TabService(tabs, mock(TableRepository.class), orders,
                mock(OrderItemRepository.class), mock(ItemExtraRepository.class), mock(UserRepository.class),
                () -> Optional.empty());

        service.closeTab(1L);

        assertEquals(Boolean.TRUE, tab.getClosed());
        verify(tabs).save(tab);
    }

    @Test
    void extrasAreChargedForEveryUnit() {
        TabRepository tabs = mock(TabRepository.class);
        OrderRepository orders = mock(OrderRepository.class);
        OrderItemRepository items = mock(OrderItemRepository.class);
        ItemExtraRepository itemExtras = mock(ItemExtraRepository.class);
        TableRepository tables = mock(TableRepository.class);
        TabEntity tab = new TabEntity(BigDecimal.ZERO, new TableEntity("A1"));
        tab.setId(1L);
        OrderEntity order = new OrderEntity(tab);
        order.setId(2L);
        OrderItemEntity item = new OrderItemEntity(null, order, (short) 2, null, new BigDecimal("10.00"));
        item.setId(3L);
        ExtraEntity extra = new ExtraEntity("Abacate", new BigDecimal("3.00"));
        ItemExtraEntity selectedExtra = new ItemExtraEntity(item, extra);
        extra.setPrice(new BigDecimal("9.00"));
        when(tabs.findById(1L)).thenReturn(Optional.of(tab));
        when(orders.findByTabId(1L)).thenReturn(List.of(order));
        when(items.findByOrderId(2L)).thenReturn(List.of(item));
        when(itemExtras.findByOrderItemId(3L)).thenReturn(List.of(selectedExtra));

        new TabService(tabs, tables, orders, items, itemExtras,
                mock(UserRepository.class), mock(org.springframework.data.domain.AuditorAware.class))
                .recalculateTotalValue(1L);

        assertEquals(new BigDecimal("26.00"), tab.getTotalValue());
        verify(tabs).save(tab);
    }

    @Test
    void unavailableProductIsRejectedBeforeOrderIsSaved() {
        OrderRepository orders = mock(OrderRepository.class);
        TabRepository tabs = mock(TabRepository.class);
        OrderItemRepository items = mock(OrderItemRepository.class);
        ItemExtraRepository itemExtras = mock(ItemExtraRepository.class);
        ProductRepository products = mock(ProductRepository.class);
        ExtraRepository extras = mock(ExtraRepository.class);
        ProductExtraRepository productExtras = mock(ProductExtraRepository.class);
        FcmService fcm = mock(FcmService.class);
        TabService tabService = mock(TabService.class);
        TabEntity tab = new TabEntity(BigDecimal.ZERO, new TableEntity("A1"));
        when(tabs.findById(1L)).thenReturn(Optional.of(tab));
        ProductEntity product = new ProductEntity("Poke", BigDecimal.TEN, null, null, null);
        product.setAvailable(false);
        when(products.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(product));
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(2L);
        item.setQuantity((short) 1);
        CreateOrderRequest request = new CreateOrderRequest();
        request.setTabId(1L);
        request.setItems(List.of(item));

        OrderService service = new OrderService(orders, tabs, items, itemExtras, products, extras, productExtras, fcm, tabService);
        assertThrows(ResponseStatusException.class, () -> service.create(request));
        verify(orders, never()).save(any());
    }

    @Test
    void retryWithSameRequestIdReturnsExistingOrder() {
        OrderRepository orders = mock(OrderRepository.class);
        TabRepository tabs = mock(TabRepository.class);
        OrderItemRepository items = mock(OrderItemRepository.class);
        TabEntity tab = new TabEntity(BigDecimal.ZERO, new TableEntity("A1"));
        tab.setId(1L);
        OrderEntity existing = new OrderEntity(tab);
        existing.setId(9L);
        existing.setClientRequestId("request-1");
        when(orders.findByClientRequestId("request-1")).thenReturn(Optional.of(existing));
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(2L);
        item.setQuantity((short) 1);
        CreateOrderRequest request = new CreateOrderRequest();
        request.setTabId(1L);
        request.setClientRequestId("request-1");
        request.setItems(List.of(item));
        OrderService service = new OrderService(orders, tabs, items, mock(ItemExtraRepository.class),
                mock(ProductRepository.class), mock(ExtraRepository.class), mock(ProductExtraRepository.class),
                mock(FcmService.class), mock(TabService.class));

        assertEquals(9L, service.create(request).getId());
        verify(orders, never()).save(any());
    }
}
