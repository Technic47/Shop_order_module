package ru.kuznetsov.shop.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.OrderStatusService;
import ru.kuznetsov.shop.order.KafkaStatusService;
import ru.kuznetsov.shop.represent.dto.order.OrderStatusDto;
import ru.kuznetsov.shop.represent.enums.OrderStatusType;

import java.util.Collection;

import static ru.kuznetsov.shop.represent.common.KafkaConst.ORDER_STATUS_FORMED_TOPIC;
import static ru.kuznetsov.shop.represent.common.KafkaConst.ORDER_STATUS_READY_TOPIC;


@RestController
@RequestMapping("/order/status")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderStatusService orderStatusService;
    private final KafkaStatusService kafkaService;

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

    @GetMapping("/status")
    public ResponseEntity<Collection<OrderStatusDto>> getAllByStatus(@RequestParam("orderId") OrderStatusType orderId) {
        return ResponseEntity.ok(orderStatusService.getAllByStatus(orderId));
    }

    @PostMapping
    public ResponseEntity<OrderStatusDto> create(@RequestBody OrderStatusDto orderStatusDto) {
        OrderStatusDto savedStatusDto = orderStatusService.add(orderStatusDto);

        switch (savedStatusDto.getStatus()) {
            case FORMED:
                kafkaService.sendNewStatusMessage(savedStatusDto, ORDER_STATUS_FORMED_TOPIC);
            case READY:
                kafkaService.sendNewStatusMessage(savedStatusDto, ORDER_STATUS_READY_TOPIC);
        }

        return ResponseEntity.ok(savedStatusDto);
    }

    @PostMapping("/batch")
    public ResponseEntity<Collection<OrderStatusDto>> createBatch(@RequestBody Collection<OrderStatusDto> OrderDtoCollection) {
        return ResponseEntity.ok(
                OrderDtoCollection.stream()
                        .map(orderStatusService::add)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderStatusService.deleteById(id);
    }
}
