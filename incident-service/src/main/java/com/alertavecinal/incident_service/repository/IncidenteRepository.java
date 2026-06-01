package com.alertavecinal.incident_service.repository;

import com.alertavecinal.incident_service.entity.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

}
