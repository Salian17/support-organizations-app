package com.example.supportorganizationsapp.repository;

import com.example.supportorganizationsapp.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
