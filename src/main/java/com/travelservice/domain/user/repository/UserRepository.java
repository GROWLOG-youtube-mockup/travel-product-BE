package com.travelservice.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelservice.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	Optional<User> findByEmail(String email);

	Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);
}
