package com.empuje.web_service.entities;

import java.time.LocalDate;

/**
 * Plain Java SavedFilter entity to satisfy compilation where Lombok isn't available.
 */
public class SavedFilter {

    private Long id;
    private Long userId;
    private String name;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private DeletedStatus deletedStatus;

    public SavedFilter() {
    }

    public SavedFilter(Long id, Long userId, String name, String category, LocalDate startDate, LocalDate endDate, DeletedStatus deletedStatus) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deletedStatus = deletedStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public DeletedStatus getDeletedStatus() {
        return deletedStatus;
    }

    public void setDeletedStatus(DeletedStatus deletedStatus) {
        this.deletedStatus = deletedStatus;
    }

    public enum DeletedStatus {
        ACTIVE,
        DELETED
    }
}
