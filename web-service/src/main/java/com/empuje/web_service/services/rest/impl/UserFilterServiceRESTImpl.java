package com.empuje.web_service.services.rest.impl;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.dto.EventFilterDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.FilterType;
import com.empuje.web_service.entities.web_service.UserFilter;
import com.empuje.web_service.repositories.UserFilterRepository;
import com.empuje.web_service.services.graphql.UserFilterService;
import com.empuje.web_service.services.rest.UserFilterServiceREST;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFilterServiceRESTImpl implements UserFilterServiceREST{

    private final UserFilterRepository repository;

    public void saveEventFilter(EventFilterDTO dto, User user) {
        UserFilter filter = UserFilter.builder()
                .filterName(dto.getFilterName())
                .filterType(FilterType.EVENT_REPORT)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .filterUserId(dto.getFilterUserId())
                .distributionDonations(dto.getDistributionDonations())
                .user(user)
                .build();

        repository.save(filter);
    }
}