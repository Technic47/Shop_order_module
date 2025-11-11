package ru.kuznetsov.shop.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.OrderStatusService;
import ru.kuznetsov.shop.represent.dto.order.OrderStatusDto;

import java.util.Collection;

@RestController
@RequestMapping("/order/status")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderStatusService orderStatusService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderStatusService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<OrderStatusDto>> getAll(
            @RequestParam(value = "orderId", required = false) Long orderId
    ) {
        if (orderId != null) {
            return ResponseEntity.ok(orderStatusService.getAllByOrderId(orderId));
        } else return ResponseEntity.ok(orderStatusService.findAll());
    }

    @GetMapping("/last")
    public ResponseEntity<OrderStatusDto> getLast(@RequestParam("orderId") Long orderId) {
        return ResponseEntity.ok(orderStatusService.getLastByOrderId(orderId));
    }

    @PostMapping
    public ResponseEntity<OrderStatusDto> create(@RequestBody OrderStatusDto orderStatusDto) {
        return ResponseEntity.ok(orderStatusService.add(orderStatusDto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderStatusService.deleteById(id);
    }
}
