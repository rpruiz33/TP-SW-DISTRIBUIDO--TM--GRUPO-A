package com.empuje.web_service.services.graphql.impl;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.FilterType;
import com.empuje.web_service.entities.web_service.UserFilter;
import com.empuje.web_service.repositories.UserFilterRepository;
import com.empuje.web_service.services.graphql.UserFilterService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFilterServiceImpl implements UserFilterService{

    private final UserFilterRepository repository;

    public void saveDonationFilter(DonationFilterDTO dto, User user) {
        
        UserFilter filter = UserFilter.builder()
                .filterName(dto.getFilterName())
                .filterType(FilterType.DONATION_REPORT)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .activate(dto.getActivate())
                .category(dto.getCategory())
                .user(user)
                .build();

        repository.save(filter);
    }
}

