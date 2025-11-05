package com.empuje.web_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.FilterType;
import com.empuje.web_service.entities.web_service.UserFilter;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, Integer> {

	java.util.List<UserFilter> findByUser_IdUser(Integer idUser);
	
	@Modifying
	@Query("""
		DELETE FROM UserFilter f
		WHERE f.filterName = :name
		AND f.user = :user
		AND f.filterType = :type
	""")
	int deleteEventFilter(@Param("name") String name,
						@Param("user") User user,
						@Param("type") FilterType type);


	Optional<UserFilter> findByFilterNameAndUserAndFilterType(String filterName, User user, FilterType filterType);


	List<UserFilter> findByUser(User user);

	// Devuelve como máximo los 50 filtros más recientes (por id) para evitar enviar demasiados registros al cliente
	List<UserFilter> findTop50ByUserOrderByIdFilterDesc(User user);

	// Variante que filtra por tipo de filtro (EVENT_REPORT, DONATION_REPORT, ...)
	List<UserFilter> findTop50ByUserAndFilterTypeOrderByIdFilterDesc(User user, FilterType filterType);

}
