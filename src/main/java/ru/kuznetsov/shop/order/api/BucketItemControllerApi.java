package ru.kuznetsov.shop.order.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kuznetsov.shop.represent.dto.order.BucketItemDto;

import java.util.Collection;
import java.util.UUID;

public interface BucketItemControllerApi {

    @Operation(summary = "Поиск по id", description = "Получение сущности по id записи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BucketItemDto.class)
                    ),
                    description = "Элемент корзины"
            ),
            @ApiResponse(responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Элемент корзины не найден")
    })
    ResponseEntity<BucketItemDto> getById(
            @Parameter(description = "Уникальный идентификатор элемента корзины для поиска", required = true,
                    schema = @Schema(
                            description = "Id элемента корзины",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @PathVariable Long id);

    @Operation(summary = "Получение всех сущностей", description = "Получение всех сущностей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BucketItemDto[].class)
                    ),
                    description = "Список элементов корзины"
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Элементы корзины не найдены"
            )
    })
    ResponseEntity<Collection<BucketItemDto>> getAll(
            @Parameter(description = "Уникальный идентификатор покупателя для поиска",
                    schema = @Schema(
                            description = "Id покупателя",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @RequestParam(value = "customerId", required = false) UUID customerId,
            @Parameter(description = "Уникальный идентификатор заказа для поиска",
                    schema = @Schema(
                            description = "Id заказа",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @RequestParam(value = "orderId", required = false) Long orderId
    );

    @Operation(summary = "Создание элемента корзины", description = "Создание элемента корзины")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BucketItemDto.class)
                    ),
                    description = "Сущность создана"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Не корректно указаны данные"
            )
    })
    ResponseEntity<BucketItemDto> create(
            @Parameter(description = "Модель элемента корзины для создания", required = true,
                    schema = @Schema(
                            implementation = BucketItemDto.class,
                            description = "Элемент корзины"
                    ))
            @RequestBody BucketItemDto bucketItemDto);

    @Operation(summary = "Создание элементов корзины", description = "Единовременное создание нескольких сущностей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BucketItemDto[].class)
                    ),
                    description = "Сущность создана"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Не корректно указаны данные"
            )
    })
    ResponseEntity<Collection<BucketItemDto>> createBatch(
            @Parameter(description = "Модель элементов корзины для создания", required = true,
                    schema = @Schema(
                            implementation = BucketItemDto[].class,
                            description = "Элементы корзины"
                    ))
            @RequestBody Collection<BucketItemDto> BucketItemDtoCollection
    );

    @Operation(summary = "Удаление по id", description = "Удаление сущности по id записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Элемент корзины удалён"),
            @ApiResponse(responseCode = "404", description = "Элемент корзины не найден")
    })
    void delete(
            @Parameter(description = "Уникальный идентификатор элемента корзины для поиска", required = true,
                    schema = @Schema(
                            description = "Id элемента корзины",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @PathVariable Long id);
}
