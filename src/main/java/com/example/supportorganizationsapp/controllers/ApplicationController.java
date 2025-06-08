package com.example.supportorganizationsapp.controllers;

import com.example.supportorganizationsapp.dto.request.application.CreateApplicationRequest;
import com.example.supportorganizationsapp.dto.request.application.UpdateApplicationRequest;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;
import com.example.supportorganizationsapp.enums.StatusEnum;
import com.example.supportorganizationsapp.service.ApplicationService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(
            summary = "Создание новой заявки",
            description = "Создаёт новую заявку со статусом NEW",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания заявки",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateApplicationRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Заявка на поездку",
                                            summary = "Стандартная заявка на сопровождение",
                                            value = """
                                                    {
                                                      "date": "2025-07-15",
                                                      "time": "09:30",
                                                      "departureStation": "Новослабодская",
                                                      "destinationStation": "Менделеевкая",
                                                      "comment": "Комментарий"
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Заявка успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            }
    )
    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            @RequestBody CreateApplicationRequest applicationRequest) {
        ApplicationResponse response = applicationService.createApplication(applicationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Обновление заявки",
            description = "Обновляет существующую заявку по её ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления заявки",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateApplicationRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Обновление времени и комментария",
                                            summary = "Изменение времени отправления и добавление комментария",
                                            value = """
                                                    {
                                                      "date": "2025-07-15",
                                                      "time": "10:00",
                                                      "departureStation": "Москва Ленинградская",
                                                      "destinationStation": "Санкт-Петербург Московский",
                                                      "comment": "Изменено время отправления, требуется помощь с инвалидной коляской",
                                                      "status": "NEW",
                                                      "passengerId": 1,
                                                      "companionId": null
                                                    }
                                                    """
                                    ),
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка успешно обновлена"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id,
            @RequestBody UpdateApplicationRequest applicationRequest) {
        ApplicationResponse response = applicationService.updateApplication(id, applicationRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявки по ID",
            description = "Возвращает заявку по её ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка найдена"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.getApplicationById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение всех заявок",
            description = "Возвращает список всех заявок",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок")
            }
    )
    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        List<ApplicationResponse> responses = applicationService.getAllApplications();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Удаление заявки",
            description = "Удаляет заявку по её ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Заявка успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id) {
        applicationService.deleteApplication(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Отмена заявки",
            description = "Меняет статус заявки на CANCELED",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка отменена"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApplicationResponse> cancelApplication(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.cancelApplication(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Принятие заявки",
            description = "Назначает сопровождающего и меняет статус на ACCEPTED",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка принята"),
                    @ApiResponse(responseCode = "404", description = "Заявка или сопровождающий не найдены")
            }
    )
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApplicationResponse> acceptApplication(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id,
            @Parameter(description = "ID сопровождающего", required = true, example = "3")
            @RequestParam Long companionId) {
        ApplicationResponse response = applicationService.acceptApplication(id, companionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Отклонение заявки",
            description = "Меняет статус заявки на REJECTED",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка отклонена"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApplicationResponse> rejectApplication(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.rejectApplication(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Начало выполнения заявки",
            description = "Меняет статус заявки на INPROGRESS",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка в процессе выполнения"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @PostMapping("/{id}/start")
    public ResponseEntity<ApplicationResponse> startProgress(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.startProgress(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Завершение заявки",
            description = "Меняет статус заявки на COMPLETED",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка завершена"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApplicationResponse> completeApplication(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.completeApplication(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Назначение заявки",
            description = "Назначает сопровождающего и меняет статус на OVERDUE",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка назначена"),
                    @ApiResponse(responseCode = "404", description = "Заявка или сопровождающий не найдены")
            }
    )
    @PutMapping("/{id}/assigned")
    public ResponseEntity<ApplicationResponse> addCompanion(
            @Parameter(description = "ID заявки", required = true, example = "3")
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.addCompanion(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение всех заявок конкретного пассажира",
            description = "Возвращает список всех заявок конкретного пассажира",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок")
            }
    )
    @GetMapping("/passenger")
    public ResponseEntity<List<ApplicationResponse>> getAllByUser() {
        List<ApplicationResponse> responses = applicationService.getByUser();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение всех заявок конкретного сопроваждающего",
            description = "Возвращает список всех заявок конкретного сопроваждающего",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок")
            }
    )
    @GetMapping("/companion")
    public ResponseEntity<List<ApplicationResponse>> getAllByCompanion() {
        List<ApplicationResponse> responses = applicationService.getByCompanion();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по статусу",
            description = "Возвращает список заявок с указанным статусом",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по статусу"),
                    @ApiResponse(responseCode = "400", description = "Некорректный статус")
            }
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStatus(
            @Parameter(description = "Статус заявки", required = true,
                    example = "NEW",
                    schema = @Schema(allowableValues = {"NEW", "ACCEPTED", "REJECTED", "CANCELED", "INPROGRESS", "COMPLETED", "OVERDUE"}))
            @PathVariable StatusEnum status) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByStatus(status);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по дате",
            description = "Возвращает список заявок на указанную дату",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по дате"),
                    @ApiResponse(responseCode = "400", description = "Некорректная дата")
            }
    )
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByDate(
            @Parameter(description = "Дата в формате YYYY-MM-DD", required = true, example = "2025-07-15")
            @PathVariable String date) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByDate(date);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по диапазону дат",
            description = "Возвращает список заявок в указанном диапазоне дат",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по диапазону дат"),
                    @ApiResponse(responseCode = "400", description = "Некорректный диапазон дат")
            }
    )
    @GetMapping("/date-range")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByDateRange(
            @Parameter(description = "Начальная дата в формате YYYY-MM-DD", required = true, example = "2025-07-01")
            @RequestParam String startDate,
            @Parameter(description = "Конечная дата в формате YYYY-MM-DD", required = true, example = "2025-07-31")
            @RequestParam String endDate) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByDateRange(startDate, endDate);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по станции отправления",
            description = "Возвращает список заявок с указанной станцией отправления",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по станции отправления"),
                    @ApiResponse(responseCode = "400", description = "Некорректное название станции")
            }
    )
    @GetMapping("/departure/{station}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByDepartureStation(
            @Parameter(description = "Название станции отправления", required = true, example = "Новослабодская")
            @PathVariable String station) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByDepartureStation(station);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по станции назначения",
            description = "Возвращает список заявок с указанной станцией назначения",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по станции назначения"),
                    @ApiResponse(responseCode = "400", description = "Некорректное название станции")
            }
    )
    @GetMapping("/destination/{station}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByDestinationStation(
            @Parameter(description = "Название станции назначения", required = true, example = "Менделеевская")
            @PathVariable String station) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByDestinationStation(station);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по любой станции",
            description = "Возвращает список заявок, где указанная станция является либо отправления, либо назначения",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по станции"),
                    @ApiResponse(responseCode = "400", description = "Некорректное название станции")
            }
    )
    @GetMapping("/station/{station}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByAnyStation(
            @Parameter(description = "Название станции", required = true, example = "Москва Ленинградская")
            @PathVariable String station) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByAnyStation(station);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по маршруту",
            description = "Возвращает список заявок с указанным маршрутом (станция отправления и назначения)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по маршруту"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры маршрута")
            }
    )
    @GetMapping("/route")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByRoute(
            @Parameter(description = "Станция отправления", required = true, example = "Новослабодская")
            @RequestParam String departureStation,
            @Parameter(description = "Станция назначения", required = true, example = "Менделеевская")
            @RequestParam String destinationStation) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByRoute(departureStation, destinationStation);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение активных заявок",
            description = "Возвращает список активных заявок (NEW, ACCEPTED, INPROGRESS)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список активных заявок")
            }
    )
    @GetMapping("/active")
    public ResponseEntity<List<ApplicationResponse>> getActiveApplications() {
        List<ApplicationResponse> responses = applicationService.getActiveApplications();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение активных заявок сопровождающего",
            description = "Возвращает список активных заявок конкретного сопровождающего",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список активных заявок сопровождающего"),
                    @ApiResponse(responseCode = "404", description = "Сопровождающий не найден")
            }
    )
    @GetMapping("/active/companion/{companionId}")
    public ResponseEntity<List<ApplicationResponse>> getActiveApplicationsByCompanion(
            @Parameter(description = "ID сопровождающего", required = true, example = "3")
            @PathVariable Long companionId) {
        List<ApplicationResponse> responses = applicationService.getActiveApplicationsByCompanion(companionId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок без сопровождающего по станции отправления",
            description = "Возвращает список заявок без назначенного сопровождающего с указанной станцией отправления",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок без сопровождающего"),
                    @ApiResponse(responseCode = "400", description = "Некорректное название станции")
            }
    )
    @GetMapping("/no-companion/departure/{station}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsWithoutCompanionByDepartureStation(
            @Parameter(description = "Название станции отправления", required = true, example = "Новослабодская")
            @PathVariable String station) {
        List<ApplicationResponse> responses = applicationService.getApplicationsWithoutCompanionByDepartureStation(station);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок без сопровождающего по станции назначения",
            description = "Возвращает список заявок без назначенного сопровождающего с указанной станцией назначения",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок без сопровождающего"),
                    @ApiResponse(responseCode = "400", description = "Некорректное название станции")
            }
    )
    @GetMapping("/no-companion/destination/{station}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsWithoutCompanionByDestinationStation(
            @Parameter(description = "Название станции назначения", required = true, example = "Менделеевская")
            @PathVariable String station) {
        List<ApplicationResponse> responses = applicationService.getApplicationsWithoutCompanionByDestinationStation(station);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок без сопровождающего по любой станции",
            description = "Возвращает список заявок без назначенного сопровождающего, где указанная станция является либо отправления, либо назначения",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок без сопровождающего"),
                    @ApiResponse(responseCode = "400", description = "Некорректное название станции")
            }
    )
    @GetMapping("/no-companion/station/{station}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsWithoutCompanionByAnyStation(
            @Parameter(description = "Название станции", required = true, example = "Новослабодская")
            @PathVariable String station) {
        List<ApplicationResponse> responses = applicationService.getApplicationsWithoutCompanionByAnyStation(station);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок без сопровождающего по маршруту",
            description = "Возвращает список заявок без назначенного сопровождающего с указанным маршрутом",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок без сопровождающего"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры маршрута")
            }
    )
    @GetMapping("/no-companion/route")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsWithoutCompanionByRoute(
            @Parameter(description = "Станция отправления", required = true, example = "Новослабодская")
            @RequestParam String departureStation,
            @Parameter(description = "Станция назначения", required = true, example = "Менделеевская")
            @RequestParam String destinationStation) {
        List<ApplicationResponse> responses = applicationService.getApplicationsWithoutCompanionByRoute(departureStation, destinationStation);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по статусу и дате",
            description = "Возвращает список заявок с указанным статусом на конкретную дату",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по статусу и дате"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры")
            }
    )
    @GetMapping("/status/{status}/date/{date}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStatusAndDate(
            @Parameter(description = "Статус заявки", required = true,
                    example = "NEW",
                    schema = @Schema(allowableValues = {"NEW", "ACCEPTED", "REJECTED", "CANCELED", "INPROGRESS", "COMPLETED", "OVERDUE"}))
            @PathVariable StatusEnum status,
            @Parameter(description = "Дата в формате YYYY-MM-DD", required = true, example = "2025-07-15")
            @PathVariable String date) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByStatusAndDate(status, date);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение заявок по статусу и маршруту",
            description = "Возвращает список заявок с указанным статусом и маршрутом",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок по статусу и маршруту"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры")
            }
    )
    @GetMapping("/status/{status}/route")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStatusAndRoute(
            @Parameter(description = "Статус заявки", required = true,
                    example = "NEW",
                    schema = @Schema(allowableValues = {"NEW", "ACCEPTED", "REJECTED", "CANCELED", "INPROGRESS", "COMPLETED", "OVERDUE"}))
            @PathVariable StatusEnum status,
            @Parameter(description = "Станция отправления", required = true, example = "Москва Ленинградская")
            @RequestParam String departureStation,
            @Parameter(description = "Станция назначения", required = true, example = "Санкт-Петербург Московский")
            @RequestParam String destinationStation) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByStatusAndRoute(status, departureStation, destinationStation);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
