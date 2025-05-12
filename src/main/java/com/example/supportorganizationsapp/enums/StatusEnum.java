package com.example.supportorganizationsapp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEnum {
    NEW(0, "Создана"),
    CANCELED(1, "Отменена"),
    ACCEPTED(2, "Принята"),
    REJECTED(3, "Отклонена"),
    INPROGRESS(4, "В процессе"),
    COMPLETED(5, "Выполнена");

    private int numStatus;
    private String nameStatus;

    StatusEnum(int numStatus, String nameStatus) {
        this.numStatus = numStatus;
        this.nameStatus = nameStatus;
    }

    public int getNumStatus() {
        return numStatus;
    }

    public void setNumStatus(int numStatus) {
        this.numStatus = numStatus;
    }

    @JsonValue
    public String getNameStatus() {
        return nameStatus;
    }

    public void setNameStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }
}
