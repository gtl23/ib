package com.truecaller.ib.repository;

import com.truecaller.ib.entity.Spam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpamRepository extends CrudRepository<Spam, Long> {

    @Query(nativeQuery = true, value = "select count(*) from spam where phone = :phone")
    Long getSpamCount(String phone);
}
