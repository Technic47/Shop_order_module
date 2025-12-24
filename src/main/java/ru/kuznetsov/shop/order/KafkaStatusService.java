package ru.kuznetsov.shop.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kuznetsov.shop.kafka.service.KafkaService;
import ru.kuznetsov.shop.represent.dto.order.OrderStatusDto;
import ru.kuznetsov.shop.represent.dto.order.UpdateOrderDTO;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class KafkaStatusService {

    private final KafkaService kafkaService;

    public void sendNewStatusMessage(OrderStatusDto savedStatusDto, String topic) {
        kafkaService.sendMessage(
                new UpdateOrderDTO(savedStatusDto.getOrderId(), savedStatusDto.getId(), savedStatusDto.getStatus()),
                topic,
                Collections.emptyMap());
    }
}
