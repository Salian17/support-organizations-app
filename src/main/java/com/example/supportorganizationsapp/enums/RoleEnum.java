package com.example.supportorganizationsapp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleEnum {
    PASSENGER(0, "Пассажир"),
    COMPANION(1, "Сопровождающий");

    private int numRole;
    private String nameRole;

    RoleEnum(int numRole, String nameRole) {
        this.numRole = numRole;
        this.nameRole = nameRole;
    }

    public int getNumRole() {
        return numRole;
    }

    public void setNumRole(int numRole) {
        this.numRole = numRole;
    }

    @JsonValue
    public String getNameRole() {
        return nameRole;
    }

    public void setNameRole(String nameRole) {
        this.nameRole = nameRole;
    }
}
