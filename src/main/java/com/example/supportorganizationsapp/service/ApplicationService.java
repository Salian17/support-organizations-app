package com.example.supportorganizationsapp.service;

import com.example.supportorganizationsapp.dto.request.application.CreateApplicationRequest;
import com.example.supportorganizationsapp.dto.request.application.UpdateApplicationRequest;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;

import java.util.List;

public interface ApplicationService {
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
}
