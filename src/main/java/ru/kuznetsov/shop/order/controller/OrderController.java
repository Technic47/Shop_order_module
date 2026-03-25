package ru.kuznetsov.shop.order.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.OrderService;
import ru.kuznetsov.shop.kafka.service.KafkaService;
import ru.kuznetsov.shop.order.api.OrderControllerApi;
import ru.kuznetsov.shop.represent.dto.order.OrderDto;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static ru.kuznetsov.shop.represent.common.KafkaConst.OPERATION_ID_HEADER;
import static ru.kuznetsov.shop.represent.common.KafkaConst.ORDER_SAVE_TOPIC;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController implements OrderControllerApi {

    private final OrderService orderService;
    private final KafkaService kafkaService;

    Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        OrderDto byId = orderService.findById(id);
        return byId == null ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(byId);
    }

    @GetMapping()
    public ResponseEntity<Collection<OrderDto>> getAll(
            @RequestParam(value = "customerId", required = false) UUID customerId
    ) {
        Collection<OrderDto> result;

        if (customerId != null) {
            result = orderService.getAllByCustomerId(customerId);
        } else result = orderService.findAll();

        return result.isEmpty() ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(result);
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
