package com.whenwego.wwg.feature.identity.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whenwego.wwg.feature.identity.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
}
