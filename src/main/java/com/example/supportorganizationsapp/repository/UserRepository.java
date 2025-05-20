package com.example.supportorganizationsapp.repository;

import com.example.supportorganizationsapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM APP_USER u WHERE u.firstName LIKE %:fullName%")
    List<User> findByFirstName(@Param("firstName") String fullName);

}
