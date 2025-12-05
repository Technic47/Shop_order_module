package ru.kuznetsov.shop.order.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(OrderController.class);

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
    public ResponseEntity<String> create(@RequestBody OrderDto orderDto) {
        String uuidString = UUID.randomUUID().toString();

        sendMessageToKafka(orderDto, uuidString);

        return ResponseEntity.ok(uuidString);
    }

    @PostMapping("/batch")
    public ResponseEntity<String> createBatch(@RequestBody Collection<OrderDto> orderDtoCollection) {
        String uuidString = UUID.randomUUID().toString();

        for (OrderDto orderDto : orderDtoCollection) {
            sendMessageToKafka(orderDto, uuidString);
        }

        return ResponseEntity.ok(uuidString);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.deleteById(id);
    }

    private void sendMessageToKafka(OrderDto productDto, String uuidString) {
        boolean sendResult = kafkaService.sendMessageWithEntity(
                productDto,
                ORDER_SAVE_TOPIC,
                Collections.singletonMap(OPERATION_ID_HEADER, uuidString.getBytes()));

        if (!sendResult) {
            logger.warn("Failed to send product to topic. Product: {} operation id {}", productDto, uuidString);
        }
    }
}
