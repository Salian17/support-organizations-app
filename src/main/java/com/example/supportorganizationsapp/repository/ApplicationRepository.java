package com.example.supportorganizationsapp.repository;

import com.example.supportorganizationsapp.enums.StatusEnum;
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

    // 1. Поиск заявок по статусу
    @Query("SELECT a FROM application a WHERE a.status = :status ORDER BY a.date DESC, a.time DESC")
    List<Application> findByStatus(@Param("status") StatusEnum status);

    // 2. Поиск заявок по конкретной дате
    @Query("SELECT a FROM application a WHERE a.date = :date ORDER BY a.time ASC")
    List<Application> findByDate(@Param("date") String date);

    // 3. Поиск заявок по диапазону дат
    @Query("SELECT a FROM application a WHERE a.date BETWEEN :startDate AND :endDate ORDER BY a.date ASC, a.time ASC")
    List<Application> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // 4. Поиск заявок по станции отправления
    @Query("SELECT a FROM application a WHERE LOWER(a.departureStation) LIKE LOWER(CONCAT('%', :station, '%')) ORDER BY a.date DESC")
    List<Application> findByDepartureStation(@Param("station") String station);

    // 5. Поиск заявок по станции назначения
    @Query("SELECT a FROM application a WHERE LOWER(a.destinationStation) LIKE LOWER(CONCAT('%', :station, '%')) ORDER BY a.date DESC")
    List<Application> findByDestinationStation(@Param("station") String station);

    // 6. Поиск заявок по любой из станций (отправления ИЛИ назначения)
    @Query("""
        SELECT a FROM application a 
        WHERE LOWER(a.departureStation) LIKE LOWER(CONCAT('%', :station, '%')) 
           OR LOWER(a.destinationStation) LIKE LOWER(CONCAT('%', :station, '%'))
        ORDER BY a.date DESC
        """)
    List<Application> findByAnyStation(@Param("station") String station);

    // 7. Поиск заявок по конкретному маршруту (от А до Б)
    @Query("""
        SELECT a FROM application a 
        WHERE LOWER(a.departureStation) LIKE LOWER(CONCAT('%', :departureStation, '%'))
          AND LOWER(a.destinationStation) LIKE LOWER(CONCAT('%', :destinationStation, '%'))
        ORDER BY a.date DESC, a.time DESC
        """)
    List<Application> findByRoute(@Param("departureStation") String departureStation,
                                  @Param("destinationStation") String destinationStation);

    // 8. Получение активных заявок (ACCEPTED, INPROGRESS, OVERDUE)
    @Query("""
        SELECT a FROM application a 
        WHERE a.status IN (
            com.example.supportorganizationsapp.enums.StatusEnum.ACCEPTED,
            com.example.supportorganizationsapp.enums.StatusEnum.INPROGRESS,
            com.example.supportorganizationsapp.enums.StatusEnum.OVERDUE
        )
        ORDER BY a.date ASC, a.time ASC
        """)
    List<Application> findActiveApplications();

    // 9. Поиск заявок без сопровождающего по станции отправления
    @Query("""
        SELECT a FROM application a 
        WHERE a.companion IS NULL 
          AND a.status = com.example.supportorganizationsapp.enums.StatusEnum.NEW
          AND LOWER(a.departureStation) LIKE LOWER(CONCAT('%', :station, '%'))
        ORDER BY a.date ASC, a.time ASC
        """)
    List<Application> findWithoutCompanionByDepartureStation(@Param("station") String station);

    // 10. Поиск заявок без сопровождающего по станции назначения
    @Query("""
        SELECT a FROM application a 
        WHERE a.companion IS NULL 
          AND a.status = com.example.supportorganizationsapp.enums.StatusEnum.NEW
          AND LOWER(a.destinationStation) LIKE LOWER(CONCAT('%', :station, '%'))
        ORDER BY a.date ASC, a.time ASC
        """)
    List<Application> findWithoutCompanionByDestinationStation(@Param("station") String station);

    // 11. Поиск заявок без сопровождающего по любой станции
    @Query("""
        SELECT a FROM application a 
        WHERE a.companion IS NULL 
          AND a.status = com.example.supportorganizationsapp.enums.StatusEnum.NEW
          AND (LOWER(a.departureStation) LIKE LOWER(CONCAT('%', :station, '%'))
               OR LOWER(a.destinationStation) LIKE LOWER(CONCAT('%', :station, '%')))
        ORDER BY a.date ASC, a.time ASC
        """)
    List<Application> findWithoutCompanionByAnyStation(@Param("station") String station);

    // 12. Поиск заявок без сопровождающего по конкретному маршруту
    @Query("""
        SELECT a FROM application a 
        WHERE a.companion IS NULL 
          AND a.status = com.example.supportorganizationsapp.enums.StatusEnum.NEW
          AND LOWER(a.departureStation) LIKE LOWER(CONCAT('%', :departureStation, '%'))
          AND LOWER(a.destinationStation) LIKE LOWER(CONCAT('%', :destinationStation, '%'))
        ORDER BY a.date ASC, a.time ASC
        """)
    List<Application> findWithoutCompanionByRoute(@Param("departureStation") String departureStation,
                                                  @Param("destinationStation") String destinationStation);

    // 13. Комбинированный поиск: статус + дата
    @Query("""
        SELECT a FROM application a 
        WHERE a.status = :status 
          AND a.date = :date
        ORDER BY a.time ASC
        """)
    List<Application> findByStatusAndDate(@Param("status") StatusEnum status, @Param("date") String date);

    // 14. Комбинированный поиск: статус + маршрут
    @Query("""
        SELECT a FROM application a 
        WHERE a.status = :status
          AND LOWER(a.departureStation) LIKE LOWER(CONCAT('%', :departureStation, '%'))
          AND LOWER(a.destinationStation) LIKE LOWER(CONCAT('%', :destinationStation, '%'))
        ORDER BY a.date DESC, a.time DESC
        """)
    List<Application> findByStatusAndRoute(@Param("status") StatusEnum status,
                                           @Param("departureStation") String departureStation,
                                           @Param("destinationStation") String destinationStation);

    // 15. Получение активных заявок для конкретного сопровождающего
    @Query("""
        SELECT a FROM application a 
        WHERE a.companion.id = :companionId
          AND a.status IN (
              com.example.supportorganizationsapp.enums.StatusEnum.ACCEPTED,
              com.example.supportorganizationsapp.enums.StatusEnum.INPROGRESS,
              com.example.supportorganizationsapp.enums.StatusEnum.OVERDUE
          )
        ORDER BY a.date ASC, a.time ASC
        """)
    List<Application> findActiveApplicationsByCompanion(@Param("companionId") Long companionId);
}

