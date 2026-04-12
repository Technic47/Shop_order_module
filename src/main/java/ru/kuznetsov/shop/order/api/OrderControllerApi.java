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
import ru.kuznetsov.shop.represent.dto.order.OrderDto;
import ru.kuznetsov.shop.represent.dto.order.OrderThinDto;
import ru.kuznetsov.shop.represent.enums.OrderStatusType;

import java.util.Collection;
import java.util.UUID;

public interface OrderControllerApi {

    @Operation(summary = "Поиск по id", description = "Получение сущности по id записи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderDto.class)
                    ),
                    description = "Заказ"
            ),
            @ApiResponse(responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Заказ не найден")
    })
    ResponseEntity<OrderDto> getById(
            @Parameter(description = "Уникальный идентификатор заказа для поиска", required = true,
                    schema = @Schema(
                            description = "Id заказа",
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
                            schema = @Schema(implementation = OrderDto[].class)
                    ),
                    description = "Заказ"
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Заказы не найдены"
            )
    })
    ResponseEntity<Collection<OrderDto>> getAll(
            @Parameter(description = "Уникальный идентификатор пользователя для поиска",
                    schema = @Schema(
                            description = "Id пользователя (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @RequestParam(value = "customerId", required = false) UUID customerId
    );

    @Operation(summary = "Получение всех заказов по статусам", description = "Получение всех заказов имеющих один статус и не имеющих другой.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderDto[].class)
                    ),
                    description = "Заказ"
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Заказы не найдены"
            )
    })
    ResponseEntity<Collection<OrderThinDto>> getAllByStatusAndOptionalParams(
            @Parameter(description = "Уникальный идентификатор пользователя для поиска",
                    schema = @Schema(
                            description = "Id пользователя (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @RequestParam(value = "customerId", required = false) UUID customerId,
            @Parameter(description = "Фильтрация заказов после этой даты",
                    schema = @Schema(
                            description = "Дата",
                            example = "2026-04-04 16:40:00.605926",
                            type = "string",
                            pattern = "yyyy-MM-dd'T'HH:mm:ss"
                    )
            )
            @RequestParam(value = "dateAfter", required = false) String dateAfter,
            @Parameter(description = "Фильтрация заказов до этой даты",
                    schema = @Schema(
                            description = "Дата",
                            example = "2026-04-04 16:40:00.605926",
                            type = "string",
                            pattern = "yyyy-MM-dd'T'HH:mm:ss"
                    )
            )
            @RequestParam(value = "dateBefore", required = false) String dateBefore,
            @Parameter(description = "Статус, который ЕСТЬ у заказа",
                    schema = @Schema(
                            description = "Статус",
                            implementation = OrderStatusType.class
                    )
            )
            @RequestParam(value = "dateBefore") OrderStatusType hasStatus,
            @Parameter(description = "Статус, которого НЕТ у заказа",
                    schema = @Schema(
                            description = "Статус",
                            implementation = OrderStatusType.class
                    )
            )
            @RequestParam(value = "dateBefore") OrderStatusType hasNotStatus
    );

    @Operation(summary = "Создание заказа", description = "Создание заказа")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = String.class,
                                    description = "Номер операции по сохранению сущности"
                            )
                    ),
                    description = "Сообщение о создании сущности отправлено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Не корректно указаны данные"
            )
    })
    ResponseEntity<String> create(
            @Parameter(description = "Модель заказа для создания", required = true,
                    schema = @Schema(
                            implementation = OrderDto.class,
                            description = "Заказ"
                    ))
            @RequestBody OrderDto orderDto);

    @Operation(summary = "Создание нескольких заказов", description = "Создание нескольких заказов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = String.class,
                                    description = "Номер операции по сохранению нескольких сущностей"
                            )
                    ),
                    description = "Сообщение о создании нескольких сущностей отправлено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Не корректно указаны данные"
            )
    })
    ResponseEntity<String> createBatch(
            @Parameter(description = "Модель заказа для создания", required = true,
                    schema = @Schema(
                            implementation = OrderDto[].class,
                            description = "Заказы"
                    ))
            @RequestBody Collection<OrderDto> orderDtoCollection);

    @Operation(summary = "Удаление по id", description = "Удаление сущности по id записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ удалён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    void delete(
            @Parameter(description = "Уникальный идентификатор заказа для поиска", required = true,
                    schema = @Schema(
                            description = "Id заказа",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @PathVariable Long id);
}
