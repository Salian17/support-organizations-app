package com.example.supportorganizationsapp.dto.request.application;

import com.example.supportorganizationsapp.enums.StatusEnum;

public class UpdateApplicationRequest {
    public String date;
    public String time;
    public String departureStation;
    public String destinationStation;
    public String comment;
    public StatusEnum status;
    public Long passengerId;
    public Long companionId;

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

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getCompanionId() {
        return companionId;
    }

    public void setCompanionId(Long companionId) {
        this.companionId = companionId;
    }
}
