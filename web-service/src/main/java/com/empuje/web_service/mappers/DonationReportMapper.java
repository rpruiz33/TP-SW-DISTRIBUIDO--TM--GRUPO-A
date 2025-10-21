package com.empuje.web_service.mappers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DonationReportMapper{

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DonationReportDTO {
        private String category;
        private boolean removed;
        private Long totalQuantity;
    }

}
