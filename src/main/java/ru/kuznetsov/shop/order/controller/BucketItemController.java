package ru.kuznetsov.shop.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.BucketItemService;
import ru.kuznetsov.shop.order.api.BucketItemControllerApi;
import ru.kuznetsov.shop.represent.dto.order.BucketItemDto;

import java.util.Collection;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/order/bucket")
@RequiredArgsConstructor
public class BucketItemController implements BucketItemControllerApi {

    private final BucketItemService bucketItemService;

    @GetMapping("/{id}")
    public ResponseEntity<BucketItemDto> getById(@PathVariable Long id) {
        BucketItemDto byId = bucketItemService.findById(id);
        return byId == null ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(byId);
    }

    @GetMapping()
    public ResponseEntity<Collection<BucketItemDto>> getAll(
            @RequestParam(value = "customerId", required = false) UUID customerId,
            @RequestParam(value = "orderId", required = false) Long orderId
    ) {
        Collection<BucketItemDto> result;

        if (customerId != null) {
            result = bucketItemService.getAllByCustomerId(customerId);
        } else if (orderId != null) {
            result = bucketItemService.getAllByOrderId(orderId);
        } else result = bucketItemService.findAll();

        return result.isEmpty() ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<BucketItemDto> create(@RequestBody BucketItemDto bucketItemDto) {
        return ResponseEntity.ok(bucketItemService.add(bucketItemDto));
    }

    @PostMapping("/batch")
    public ResponseEntity<Collection<BucketItemDto>> createBatch(
            @RequestBody Collection<BucketItemDto> BucketItemDtoCollection
    ) {
        return ResponseEntity.ok(
                BucketItemDtoCollection.stream()
                        .map(bucketItemService::add)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bucketItemService.deleteById(id);
    }
}
