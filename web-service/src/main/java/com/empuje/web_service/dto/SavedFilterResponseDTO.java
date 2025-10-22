package com.empuje.web_service.dto;

import java.time.LocalDate;

import com.empuje.web_service.entities.web_service.DeletedStatus;
import com.empuje.web_service.entities.web_service.SavedFilter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedFilterResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private DeletedStatus deletedStatus;

    public static SavedFilterResponseDTO fromEntity(SavedFilter entity) {
        return SavedFilterResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .category(entity.getCategory())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .deletedStatus(entity.getDeletedStatus())
                .build();
    }
}
