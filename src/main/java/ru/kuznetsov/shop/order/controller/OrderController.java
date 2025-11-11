package ru.kuznetsov.shop.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.KafkaService;
import ru.kuznetsov.shop.data.service.OrderService;
import ru.kuznetsov.shop.represent.dto.order.OrderDto;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static ru.kuznetsov.shop.represent.common.KafkaConst.OPERATION_ID_HEADER;
import static ru.kuznetsov.shop.represent.common.KafkaConst.ORDER_SAVE_TOPIC;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final KafkaService kafkaService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping()
    public ResponseEntity<Collection<OrderDto>> getAll(
            @RequestParam(value = "customerId", required = false) UUID customerId
    ) {
        if (customerId != null) {
            return ResponseEntity.ok(orderService.getAllByCustomerId(customerId));
        } else return ResponseEntity.ok(orderService.findAll());
    }

    @PostMapping
    public ResponseEntity<Boolean> create(@RequestBody OrderDto storeDto) {
        return ResponseEntity.ok(kafkaService.sendMessageWithEntity(
                storeDto,
                ORDER_SAVE_TOPIC,
                Collections.singletonMap(OPERATION_ID_HEADER, UUID.randomUUID().toString().getBytes())));
    }

    @PostMapping("/batch")
    public ResponseEntity<Collection<Boolean>> createBatch(@RequestBody Collection<OrderDto> OrderDtoCollection) {
        byte[] operationId = UUID.randomUUID().toString().getBytes();

        return ResponseEntity.ok(
                OrderDtoCollection.stream()
                        .map(dto -> kafkaService.sendMessageWithEntity(dto,
                                ORDER_SAVE_TOPIC,
                                Collections.singletonMap(OPERATION_ID_HEADER, operationId)))
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.deleteById(id);
    }
}
