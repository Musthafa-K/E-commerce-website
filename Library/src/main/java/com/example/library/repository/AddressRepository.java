package com.example.library.repository;

import com.example.library.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long>{
    Address findById(long id);

    @Query(value = "select * from address where active = true and customer_id = :id", nativeQuery = true)
    Address findByActivatedTrue(@Param("id") long id);

}
