package com.serendib.api.repository;

import com.serendib.api.entity.EmergencyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface EmergencyServiceRepository extends JpaRepository<EmergencyService, UUID> {

    // Find nearest services using Haversine distance formula
    // 6371 = Earth radius in km
    @Query(value = """
            SELECT *,
                (6371 * acos(
                    cos(radians(:lat)) * cos(radians(latitude)) *
                    cos(radians(longitude) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(latitude))
                )) AS distance
            FROM emergency_services
            WHERE (:type IS NULL OR type = :type)
            ORDER BY distance
            LIMIT :limit
            """, nativeQuery = true)
    List<EmergencyService> findNearest(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("type") String type,
            @Param("limit") int limit
    );

    List<EmergencyService> findByProvinceIgnoreCaseAndType(String province, String type);

    List<EmergencyService> findByProvinceIgnoreCase(String province);

    long countByType(String type);

}