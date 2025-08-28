package org.example.imgq.repo;

import org.example.imgq.domain.AuthLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthLogRepository extends JpaRepository<AuthLog, UUID> { }
