package ru.kuznetsov.shop.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.BucketItemService;
import ru.kuznetsov.shop.represent.dto.order.BucketItemDto;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/order/bucket")
@RequiredArgsConstructor
public class BucketItemController {

    private final BucketItemService bucketItemService;

    @GetMapping("/{id}")
    public ResponseEntity<BucketItemDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bucketItemService.findById(id));
    }

    @GetMapping()
    public ResponseEntity<Collection<BucketItemDto>> getAll(
            @RequestParam(value = "customerId", required = false) UUID customerId,
            @RequestParam(value = "orderId", required = false) Long orderId
    ) {
        if (customerId != null) {
            return ResponseEntity.ok(bucketItemService.getAllByCustomerId(customerId));
        } else if (orderId != null) {
            return ResponseEntity.ok(bucketItemService.getAllByOrderId(orderId));
        } else return ResponseEntity.ok(bucketItemService.findAll());
    }

    @PostMapping
    public ResponseEntity<BucketItemDto> create(@RequestBody BucketItemDto storeDto) {
        return ResponseEntity.ok(bucketItemService.add(storeDto));
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
