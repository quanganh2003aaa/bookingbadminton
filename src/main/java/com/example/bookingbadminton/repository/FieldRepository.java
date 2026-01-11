package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface FieldRepository extends JpaRepository<Field, UUID> {
    Optional<Field> findByIdAndOwner(UUID id, Owner owner);

    @Query("""
            SELECT f FROM Field f
            JOIN f.owner o
            JOIN o.account a
            WHERE (:search IS NULL OR :search = '' OR
                   LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(a.gmail) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Field> findByFilters(@Param("search") String search, Pageable pageable);

    Page<Field> findByOwner_IdAndParentFieldIsNull(UUID ownerId, Pageable pageable);

    List<Field> findByParentField_IdOrderByIndexFieldAsc(UUID parentId);

    @Query("""
            SELECT f FROM Field f
            WHERE (:search IS NULL OR :search = '' OR LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')))
                AND f.parentField IS NULL
              AND (:active IS NULL OR f.active = :active)
            """)
    Page<Field> findByFiltersForUser(@Param("search") String search,
                                     @Param("active") com.example.bookingbadminton.model.Enum.ActiveStatus active,
                                     Pageable pageable);
}
