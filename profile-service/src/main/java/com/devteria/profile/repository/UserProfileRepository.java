package com.devteria.profile.repository;

import com.devteria.profile.entity.UserProfile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository //good practice for class
public interface UserProfileRepository extends Neo4jRepository<UserProfile, String> {}
