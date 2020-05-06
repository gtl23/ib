package com.truecaller.ib.repository;

import com.truecaller.ib.entity.Contacts;
import com.truecaller.ib.model.SearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactsRepository extends CrudRepository<Contacts, Long> {

    @Query(nativeQuery = true, value = "SELECT name, phone FROM  contacts c where c.phone  = :key",
    countQuery = "SELECT COUNT(*) FROM  contacts c where c.phone  = :key")
    Page<SearchProjection> findByPhone(String key, Pageable pageable);
}
