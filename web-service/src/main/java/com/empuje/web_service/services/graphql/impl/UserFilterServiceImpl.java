package com.empuje.web_service.services.graphql.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.FilterType;
import com.empuje.web_service.entities.web_service.UserFilter;
import com.empuje.web_service.repositories.UserFilterRepository;
import com.empuje.web_service.services.graphql.UserFilterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFilterServiceImpl implements UserFilterService{

    private static final Logger logger = LoggerFactory.getLogger(UserFilterServiceImpl.class);

    private final UserFilterRepository repository;

    @Override
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

        UserFilter saved = repository.save(filter);
        logger.debug("Saved donation filter id={} for userId={} name={}", saved.getIdFilter(),
                user != null ? user.getIdUser() : null, saved.getFilterName());
    }

    @Override
    @Transactional
    public boolean deleteDonationFilter(Integer idFilter, User user) {
        try {
            return repository.findById(idFilter).map(f -> {
                // comprobar que el filtro pertenece al usuario
                if (f.getUser() != null && f.getUser().getIdUser() != null
                        && f.getUser().getIdUser().equals(user.getIdUser())) {
                    repository.delete(f);
                    logger.debug("Deleted filter id={} by userId={}", idFilter, user.getIdUser());
                    return true;
                }
                logger.warn("Delete denied: filter id={} ownerId={} requestUserId={}", idFilter,
                        f.getUser() != null ? f.getUser().getIdUser() : null, user.getIdUser());
                return false;
            }).orElseGet(() -> {
                logger.warn("Delete failed: filter id={} not found", idFilter);
                return false;
            });
        } catch (Exception e) {
            logger.error("Exception while deleting filter id={}", idFilter, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateDonationFilter(Integer idFilter, DonationFilterDTO dto, User user) {
        try {
            return repository.findById(idFilter).map(f -> {
                // comprobar que el filtro pertenece al usuario
                if (f.getUser() == null || f.getUser().getIdUser() == null) {
                    logger.warn("Update denied: filter id={} has no owner", idFilter);
                    return false;
                }
                if (!f.getUser().getIdUser().equals(user.getIdUser())) {
                    logger.warn("Update denied: filter id={} ownerId={} requestUserId={}", idFilter,
                            f.getUser().getIdUser(), user.getIdUser());
                    return false;
                }

                // actualizar campos permitidos
                if (dto.getFilterName() != null) f.setFilterName(dto.getFilterName());
                if (dto.getStartDate() != null) f.setStartDate(dto.getStartDate());
                if (dto.getEndDate() != null) f.setEndDate(dto.getEndDate());
                if (dto.getActivate() != null) f.setActivate(dto.getActivate());
                if (dto.getCategory() != null) f.setCategory(dto.getCategory());

                UserFilter saved = repository.save(f);
                logger.debug("Updated filter id={} by userId={}; fields set: name={}, start={}, end={}, activate={}, category={}",
                        saved.getIdFilter(), user.getIdUser(), saved.getFilterName(), saved.getStartDate(), saved.getEndDate(), saved.getActivate(), saved.getCategory());
                return true;
            }).orElseGet(() -> {
                // Si no existe el filtro, crearlo (comportamiento upsert)
                try {
                    UserFilter newFilter = UserFilter.builder()
                            .filterName(dto.getFilterName())
                            .filterType(FilterType.DONATION_REPORT)
                            .startDate(dto.getStartDate())
                            .endDate(dto.getEndDate())
                            .activate(dto.getActivate())
                            .category(dto.getCategory())
                            .user(user)
                            .build();
                    UserFilter saved = repository.save(newFilter);
                    logger.debug("Created new filter id={} for userId={} via update upsert", saved.getIdFilter(), user.getIdUser());
                    return true;
                } catch (Exception ex) {
                    logger.error("Exception while creating new filter in upsert for userId={}", user.getIdUser(), ex);
                    return false;
                }
            });
        } catch (Exception e) {
            logger.error("Exception while updating filter id={}", idFilter, e);
            return false;
        }
    }

}

