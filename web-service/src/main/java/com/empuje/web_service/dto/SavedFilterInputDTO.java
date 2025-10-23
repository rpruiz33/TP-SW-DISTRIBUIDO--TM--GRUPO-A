package com.empuje.web_service.dto;

import com.empuje.web_service.entities.web_service.DeletedStatus;
import com.empuje.web_service.entities.web_service.SavedFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedFilterInputDTO {

    private Long userId;
    private String name;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private DeletedStatus deletedStatus;

    public SavedFilter toEntity() {
        SavedFilter entity = new SavedFilter();
        entity.setUserId(this.userId);
        entity.setName(this.name);
        entity.setCategory(this.category);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setDeletedStatus(this.deletedStatus);
        return entity;
    }
}
