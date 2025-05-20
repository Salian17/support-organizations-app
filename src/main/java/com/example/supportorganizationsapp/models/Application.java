package com.example.supportorganizationsapp.models;

import com.example.supportorganizationsapp.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Builder;

@Entity(name = "application")
@Builder
public class Application extends Base {

    private String date;
    private String time;
    private String departureStation;
    private String destinationStation;
    private String comment;
    private StatusEnum status;
    private User passenger;
    private User companion;

    protected Application() {
    }

    public Application(String date, String time, String departureStation, String destinationStation, String comment, StatusEnum status, User passenger) {
        this.date = date;
        this.time = time;
        this.departureStation = departureStation;
        this.destinationStation = destinationStation;
        this.comment = comment;
        this.status = status;
        this.passenger = passenger;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Enumerated(EnumType.STRING)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    @ManyToOne
    @JoinColumn(name = "companion_id")
    public User getCompanion() {
        return companion;
    }

    public void setCompanion(User companion) {
        this.companion = companion;
    }
}
