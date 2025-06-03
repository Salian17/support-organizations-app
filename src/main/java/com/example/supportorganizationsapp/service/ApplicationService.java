package com.example.supportorganizationsapp.service;

import com.example.supportorganizationsapp.dto.request.application.CreateApplicationRequest;
import com.example.supportorganizationsapp.dto.request.application.UpdateApplicationRequest;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;
import com.example.supportorganizationsapp.enums.StatusEnum;

import java.util.List;

public interface ApplicationService {
    // Существующие методы
    ApplicationResponse createApplication(CreateApplicationRequest application);
    ApplicationResponse updateApplication(Long id, UpdateApplicationRequest application);
    ApplicationResponse getApplicationById(Long id);
    List<ApplicationResponse> getAllApplications();
    void deleteApplication(Long id);
    ApplicationResponse cancelApplication(Long id);
    ApplicationResponse acceptApplication(Long id, Long companionId);
    ApplicationResponse rejectApplication(Long id);
    ApplicationResponse startProgress(Long id);
    ApplicationResponse completeApplication(Long id);
    ApplicationResponse addCompanion(Long id);
    List<ApplicationResponse> getByUser();
    List<ApplicationResponse> getByCompanion();
    List<ApplicationResponse> getApplicationsByStatus(StatusEnum status);
    List<ApplicationResponse> getApplicationsByDate(String date);
    List<ApplicationResponse> getApplicationsByDateRange(String startDate, String endDate);
    List<ApplicationResponse> getApplicationsByDepartureStation(String station);
    List<ApplicationResponse> getApplicationsByDestinationStation(String station);
    List<ApplicationResponse> getApplicationsByAnyStation(String station);
    List<ApplicationResponse> getApplicationsByRoute(String departureStation, String destinationStation);
    List<ApplicationResponse> getActiveApplications();
    List<ApplicationResponse> getActiveApplicationsByCompanion(Long companionId);
    List<ApplicationResponse> getApplicationsWithoutCompanionByDepartureStation(String station);
    List<ApplicationResponse> getApplicationsWithoutCompanionByDestinationStation(String station);
    List<ApplicationResponse> getApplicationsWithoutCompanionByAnyStation(String station);
    List<ApplicationResponse> getApplicationsWithoutCompanionByRoute(String departureStation, String destinationStation);
    List<ApplicationResponse> getApplicationsByStatusAndDate(StatusEnum status, String date);
    List<ApplicationResponse> getApplicationsByStatusAndRoute(StatusEnum status, String departureStation, String destinationStation);
}
