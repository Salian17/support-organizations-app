package com.example.supportorganizationsapp.dto.request;

import java.util.Objects;

public class SearchMessagesRequestDTO {
    private final String searchText;

    public SearchMessagesRequestDTO(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SearchMessagesRequestDTO that = (SearchMessagesRequestDTO) obj;
        return Objects.equals(searchText, that.searchText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchText);
    }

    @Override
    public String toString() {
        return "SearchMessagesRequestDTO{" +
                "searchText='" + searchText + '\'' +
                '}';
    }
}