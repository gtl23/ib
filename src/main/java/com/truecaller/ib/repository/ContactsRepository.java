package com.truecaller.ib.repository;

import com.truecaller.ib.entity.Contacts;
import com.truecaller.ib.model.SearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactsRepository extends JpaRepository<Contacts, Long> {

    @Query(nativeQuery = true, value = "SELECT name, phone FROM  contacts c where c.phone  = :key",
    countQuery = "SELECT COUNT(*) FROM  contacts c where c.phone  = :key")
    Page<SearchProjection> findByPhone(String key, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from contacts where uploaded_by = :id and phone = :phone")
    Optional<Contacts> checkUsersContacts(Long id, String phone);

    @Query(nativeQuery = true, value = "SELECT name, phone FROM  contacts c where c.phone = :phone")
    List<SearchProjection> findByPhone(String phone);
}
