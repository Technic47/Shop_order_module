package ru.kuznetsov.shop.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.OrderStatusService;
import ru.kuznetsov.shop.order.KafkaStatusService;
import ru.kuznetsov.shop.order.api.OrderStatusControllerApi;
import ru.kuznetsov.shop.represent.dto.order.OrderStatusDto;
import ru.kuznetsov.shop.represent.enums.OrderStatusType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static ru.kuznetsov.shop.represent.common.KafkaConst.*;


@RestController
@RequestMapping("/order/status")
@RequiredArgsConstructor
public class OrderStatusController implements OrderStatusControllerApi {

    private final OrderStatusService orderStatusService;
    private final KafkaStatusService kafkaService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusDto> getById(@PathVariable Long id) {
        OrderStatusDto byId = orderStatusService.findById(id);
        return byId == null ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(byId);
    }

    @GetMapping
    public ResponseEntity<Collection<OrderStatusDto>> getAll(
            @RequestParam(value = "orderId", required = false) Long orderId
    ) {
        Collection<OrderStatusDto> result;

        if (orderId != null) {
            result = orderStatusService.getAllByOrderId(orderId);
        } else result = orderStatusService.findAll();

        return result.isEmpty() ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(result);
    }

    @GetMapping("/last")
    public ResponseEntity<OrderStatusDto> getLast(@RequestParam("orderId") Long orderId) {
        OrderStatusDto result = orderStatusService.getLastByOrderId(orderId);
        return result == null ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<Collection<OrderStatusDto>> getAllByStatus(
            @RequestParam("status") OrderStatusType status,
            @RequestParam(value = "dateTime", required = false) String dateTime,
            @RequestParam(value = "direction", required = false) String direction) {
        List<OrderStatusDto> result;

        if (dateTime != null && !dateTime.isBlank()) {
            if (direction == null || direction.isBlank()) {
                return ResponseEntity.badRequest().build();
            } else {
                LocalDateTime date;
                try {
                    date = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().build();
                }

                result =
                        switch (direction) {
                            case "after" -> orderStatusService.getAllByStatusAfter(status, date);
                            case "before" -> orderStatusService.getAllByStatusBefore(status, date);
                            default -> throw new IllegalStateException("Unexpected value: " + direction);
                        };
            }
        } else result = orderStatusService.getAllByStatus(status);

        return result.isEmpty() ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<OrderStatusDto> create(@RequestBody OrderStatusDto orderStatusDto) {
        OrderStatusDto savedStatusDto = orderStatusService.add(orderStatusDto);

        switch (savedStatusDto.getStatus()) {
            case FORMED:
                kafkaService.sendNewStatusMessage(savedStatusDto, ORDER_STATUS_FORMED_TOPIC);
                break;
            case READY:
                kafkaService.sendNewStatusMessage(savedStatusDto, ORDER_STATUS_READY_TOPIC);
                break;
            case DELIVERED:
                kafkaService.sendNewStatusMessage(savedStatusDto, ORDER_STATUS_DELIVERED_TOPIC);
                break;
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
