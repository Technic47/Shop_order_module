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
import ru.kuznetsov.shop.represent.dto.order.OrderStatusDto;
import ru.kuznetsov.shop.represent.enums.OrderStatusType;

import java.util.Collection;

public interface OrderStatusControllerApi {

    @Operation(summary = "Поиск по id", description = "Получение сущности по id записи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderStatusDto.class)
                    ),
                    description = "Статус заказа"
            ),
            @ApiResponse(responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Статус заказа не найден")
    })
    ResponseEntity<OrderStatusDto> getById(
            @Parameter(description = "Уникальный идентификатор статуса заказа для поиска", required = true,
                    schema = @Schema(
                            description = "Id статуса заказа",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @PathVariable Long id);

    @Operation(summary = "Получение всех статусов заказа", description = "Получение всех статусов заказа")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderStatusDto[].class)
                    ),
                    description = "Статусы заказа"
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Статусы заказа не найдены"
            )
    })
    ResponseEntity<Collection<OrderStatusDto>> getAll(
            @Parameter(description = "Уникальный идентификатор заказа для поиска", required = true,
                    schema = @Schema(
                            description = "Id заказа",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @RequestParam(value = "orderId", required = false) Long orderId
    );

    @Operation(summary = "Получение последнего статуса заказа", description = "Получение последнего статуса заказа")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderStatusDto.class)
                    ),
                    description = "Последний статус заказа"
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Статусы заказа не найдены"
            )
    })
    ResponseEntity<OrderStatusDto> getLast(
            @Parameter(description = "Уникальный идентификатор заказа для поиска", required = true,
                    schema = @Schema(
                            description = "Id заказа",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            ) @RequestParam("orderId") Long orderId);

    @Operation(summary = "Получение всех стутасов заказов по наименованию статуса", description = "Получение всех стутасов заказов по наименованию статуса")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderStatusDto[].class)
                    ),
                    description = "Статусы заказов"
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Статусы не найдены"
            )
    })
    ResponseEntity<Collection<OrderStatusDto>> getAllByStatus(
            @Parameter(description = "Наименование статуса для поиска",
                    schema = @Schema(
                            description = "Статус",
                            example = "CREATED, FORMED, READY, SHIPPED, DELIVERED, CANCELED, ERROR, AWAIT_PAYMENT, AWAIT_CUSTOMER"
                    )
            )
            @RequestParam("status") OrderStatusType status,
            @Parameter(description = "Дата для поиска",
                    schema = @Schema(
                            description = "Дата",
                            example = "before, after",
                            type = "string",
                            pattern ="yyyy-MM-dd'T'HH:mm:ss"
                    )
            )
            @RequestParam("dateTime") String dateTime,
            @Parameter(description = "Направление поиска по дате",
                    schema = @Schema(
                            description = "До или после указанной даты",
                            example = "before, after"
                    )
            )
            @RequestParam("direction") String direction);

    @Operation(summary = "Создание статуса заказа", description = "Создание статуса заказа")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderStatusDto.class)
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
    ResponseEntity<OrderStatusDto> create(
            @Parameter(description = "Модель статуса заказа для создания", required = true,
                    schema = @Schema(
                            implementation = OrderStatusDto.class,
                            description = "Статус заказа"
                    ))
            @RequestBody OrderStatusDto orderStatusDto);

    @Operation(summary = "Создание нескольких статусов заказа", description = "Единовременное создание нескольких статусов заказа")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderStatusDto[].class)
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
    ResponseEntity<Collection<OrderStatusDto>> createBatch(
            @Parameter(description = "Модель статусов заказа для создания", required = true,
                    schema = @Schema(
                            implementation = OrderStatusDto[].class,
                            description = "Статусы заказа"
                    ))
            @RequestBody Collection<OrderStatusDto> OrderDtoCollection);

    @Operation(summary = "Удаление по id", description = "Удаление сущности по id записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус заказа удалён"),
            @ApiResponse(responseCode = "404", description = "Статус заказа не найден")
    })
    void delete(
            @Parameter(description = "Уникальный идентификатор статуса заказа для удаления", required = true,
                    schema = @Schema(
                            description = "Id статуса заказа",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @PathVariable Long id);
}
