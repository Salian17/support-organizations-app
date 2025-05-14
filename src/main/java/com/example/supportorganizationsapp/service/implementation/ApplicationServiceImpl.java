package com.example.supportorganizationsapp.service.implementation;

import com.example.supportorganizationsapp.dto.request.application.CreateApplicationRequest;
import com.example.supportorganizationsapp.dto.request.application.UpdateApplicationRequest;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;
import com.example.supportorganizationsapp.enums.StatusEnum;
import com.example.supportorganizationsapp.models.Application;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.repository.ApplicationRepository;
import com.example.supportorganizationsapp.repository.UserRepository;
import com.example.supportorganizationsapp.service.ApplicationService;
import com.example.supportorganizationsapp.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository, UserRepository userRepository, AuthUtil authUtil) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }

    @Override
    public ApplicationResponse createApplication(CreateApplicationRequest applicationRequest) {
        String passengerEmail = authUtil.getPrincipalEmail();
        User passenger = userRepository.findByEmail(passengerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        Application application = new Application(
                applicationRequest.getDate(),
                applicationRequest.getTime(),
                applicationRequest.getDepartureStation(),
                applicationRequest.getDestinationStation(),
                applicationRequest.getComment(),
                StatusEnum.NEW, // Устанавливаем статус NEW при создании
                passenger
        );

        Application savedApplication = applicationRepository.save(application);
        return mapToResponse(savedApplication);
    }

    @Override
    public ApplicationResponse updateApplication(Long id, UpdateApplicationRequest applicationRequest) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (applicationRequest.getDate() != null) {
            application.setDate(applicationRequest.getDate());
        }
        if (applicationRequest.getTime() != null) {
            application.setTime(applicationRequest.getTime());
        }
        if (applicationRequest.getDepartureStation() != null) {
            application.setDepartureStation(applicationRequest.getDepartureStation());
        }
        if (applicationRequest.getDestinationStation() != null) {
            application.setDestinationStation(applicationRequest.getDestinationStation());
        }
        if (applicationRequest.getComment() != null) {
            application.setComment(applicationRequest.getComment());
        }
        if (applicationRequest.getStatus() != null) {
            application.setStatus(applicationRequest.getStatus());
        }
        if (applicationRequest.getPassengerId() != null) {
            User passenger = userRepository.findById(applicationRequest.getPassengerId())
                    .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));
            application.setPassenger(passenger);
        }
        if (applicationRequest.getCompanionId() != null) {
            User companion = userRepository.findById(applicationRequest.getCompanionId())
                    .orElseThrow(() -> new IllegalArgumentException("Companion not found"));
            application.setCompanion(companion);
        }

        Application updatedApplication = applicationRepository.save(application);
        return mapToResponse(updatedApplication);
    }

    @Override
    public ApplicationResponse cancelApplication(Long id) {
        return changeStatus(id, StatusEnum.CANCELED);
    }

    @Override
    public ApplicationResponse acceptApplication(Long id, Long companionId) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        User companion = userRepository.findById(companionId)
                .orElseThrow(() -> new IllegalArgumentException("Companion not found"));
        application.setCompanion(companion);
        application.setStatus(StatusEnum.ACCEPTED);
        Application updatedApplication = applicationRepository.save(application);
        return mapToResponse(updatedApplication);
    }

    @Override
    public ApplicationResponse rejectApplication(Long id) {
        return changeStatus(id, StatusEnum.REJECTED);
    }

    @Override
    public ApplicationResponse startProgress(Long id) {
        return changeStatus(id, StatusEnum.INPROGRESS);
    }

    @Override
    public ApplicationResponse completeApplication(Long id) {
        return changeStatus(id, StatusEnum.COMPLETED);
    }

    private ApplicationResponse changeStatus(Long id, StatusEnum status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.setStatus(status);
        Application updatedApplication = applicationRepository.save(application);
        return mapToResponse(updatedApplication);
    }

    @Override
    public ApplicationResponse getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        return mapToResponse(application);
    }

    @Override
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    private ApplicationResponse mapToResponse(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getDate(),
                application.getTime(),
                application.getDepartureStation(),
                application.getDestinationStation(),
                application.getComment(),
                application.getStatus(),
                application.getPassenger().getId(),
                application.getCompanion() != null ? application.getCompanion().getId() : null
        );
    }

    @Override
    public ApplicationResponse addCompanion(Long id) {
        String companionEmail = authUtil.getPrincipalEmail();
        User companion = userRepository.findByEmail(companionEmail)
                .orElseThrow(() -> new IllegalArgumentException("Companion not found"));

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        application.setCompanion(companion);
        application.setStatus(StatusEnum.OVERDUE);

        Application updatedApplication = applicationRepository.save(application);
        return mapToResponse(updatedApplication);
    }
}
