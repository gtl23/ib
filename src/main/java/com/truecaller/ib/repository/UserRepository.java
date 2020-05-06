package com.truecaller.ib.repository;

import com.truecaller.ib.entity.User;
import com.truecaller.ib.model.SearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    @Query(nativeQuery = true, value = "SELECT name, phone FROM `user` where name LIKE CONCAT(:key, '%') " +
            "union " +
            "SELECT name, phone FROM contacts where name LIKE CONCAT(:key, '%') " +
            "UNION " +
            "SELECT name, phone FROM `user` u2 where name LIKE CONCAT('_%', :key, '%') " +
            "UNION " +
            "SELECT name, phone FROM contacts where name LIKE CONCAT('_%', :key, '%')",
    countQuery = "SELECT COUNT(*) FROM ((SELECT name, phone FROM `user` where name LIKE CONCAT(:key, '%') " +
            "union " +
            "SELECT name, phone FROM contacts where name LIKE CONCAT(:key, '%') " +
            "UNION " +
            "SELECT name, phone FROM `user` u2 where name LIKE CONCAT('_%', :key, '%') " +
            "UNION " +
            "SELECT name, phone FROM contacts where name LIKE CONCAT('_%', :key, '%')) as temp)")
    Page<SearchProjection> searchByName(String key, Pageable pageable);
}
