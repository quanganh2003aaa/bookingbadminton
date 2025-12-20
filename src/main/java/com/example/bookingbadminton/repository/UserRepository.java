package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByAccount(Account account);

    @Query("""
            SELECT u FROM User u
            WHERE (:locked IS NULL OR (:locked = true AND u.deletedAt IS NOT NULL) OR (:locked = false AND u.deletedAt IS NULL))
              AND (
                  :search IS NULL OR :search = '' OR
                  LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                  LOWER(u.account.gmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
                  u.account.msisdn LIKE CONCAT('%', :search, '%')
              )
            """)
    Page<User> findByFilters(@Param("search") String search,
                             @Param("locked") Boolean locked,
                             Pageable pageable);
}
