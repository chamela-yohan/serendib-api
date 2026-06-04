package com.serendib.api.repository;

import com.serendib.api.entity.SosAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SosAlertRepository extends JpaRepository<SosAlert, UUID> {
}
