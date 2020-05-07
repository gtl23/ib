package com.truecaller.ib.repository;

import com.truecaller.ib.entity.Spam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpamRepository extends JpaRepository<Spam, Long> {

    @Query(nativeQuery = true, value = "select count(*) from spam where phone = :phone")
    Long getSpamCount(String phone);
}
