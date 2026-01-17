package com.tlu.hrm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tlu.hrm.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
	
	boolean existsByUsername(String username);
	
	Optional<User> findByRefreshToken(String refreshToken);
	
	@Query("""
	    SELECT u FROM User u
	    ORDER BY
	      CASE
	        WHEN u.status = com.tlu.hrm.enums.UserStatus.ACTIVE THEN 1
	        WHEN u.status = com.tlu.hrm.enums.UserStatus.INACTIVE THEN 2
	        WHEN u.status = com.tlu.hrm.enums.UserStatus.LOCKED THEN 3
	      END,
	      CASE
	        WHEN u.status = com.tlu.hrm.enums.UserStatus.ACTIVE THEN u.createdAt
	        ELSE u.updatedAt
	      END DESC
	""")
	Page<User> findAllSortedByStatusAndTime(Pageable pageable);
	
	Optional<User> findByEmployee_Id(Long employeeId);
}
