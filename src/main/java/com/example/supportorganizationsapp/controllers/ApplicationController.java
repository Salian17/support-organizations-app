package com.example.supportorganizationsapp.controllers;

import com.example.supportorganizationsapp.dto.request.application.CreateApplicationRequest;
import com.example.supportorganizationsapp.dto.request.application.UpdateApplicationRequest;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;
import com.example.supportorganizationsapp.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(
            summary = "Создание новой заявки",
            description = "Создаёт новую заявку со статусом NEW",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Заявка успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            }
    )
    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            @Parameter(description = "Данные для создания заявки", required = true)
            @RequestBody CreateApplicationRequest applicationRequest) {
        ApplicationResponse response = applicationService.createApplication(applicationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Обновление заявки",
            description = "Обновляет существующую заявку по её ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка успешно обновлена"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @Parameter(description = "ID заявки", required = true)
            @PathVariable Long id,
            @Parameter(description = "Данные для обновления заявки", required = true)
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
            @Parameter(description = "ID заявки", required = true)
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
            @Parameter(description = "ID заявки", required = true)
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
            @Parameter(description = "ID заявки", required = true)
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
            @Parameter(description = "ID заявки", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID сопровождающего", required = true)
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
            @Parameter(description = "ID заявки", required = true)
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
            @Parameter(description = "ID заявки", required = true)
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
            @Parameter(description = "ID заявки", required = true)
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
            @Parameter(description = "ID заявки", required = true)
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.addCompanion(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
