package com.example.supportorganizationsapp.repository;

import com.example.supportorganizationsapp.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT a FROM application a WHERE a.passenger.id = :userId ORDER BY a.id DESC")
    List<Application> findByUser(@Param("userId") Long userId);

    @Query("SELECT a FROM application a WHERE a.companion.id = :userId ORDER BY a.id DESC")
    List<Application> findByCompanion(@Param("userId") Long userId);

    @Query("""
        SELECT a 
          FROM application a 
         WHERE a.status = com.example.supportorganizationsapp.enums.StatusEnum.NEW
           AND a.companion IS NULL
        """)
    List<Application> findAllNewWithoutCompanion();

}

