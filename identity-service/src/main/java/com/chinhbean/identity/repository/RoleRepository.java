package com.chinhbean.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chinhbean.identity.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
