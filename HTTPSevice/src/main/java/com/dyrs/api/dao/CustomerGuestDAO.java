package com.dyrs.api.dao;

import com.dyrs.api.entity.CustomerGuest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerGuestDAO extends Repository<CustomerGuest, Long> {
    CustomerGuest save(CustomerGuest customerGuest);

    @Modifying
    @Query("DELETE FROM CustomerGuest cg WHERE cg.customerID = ?1 AND cg.guestID = ?2")
    @Transactional
    void deleteByCustomerIDAndGuestID(String customerID, String guestID);

    CustomerGuest findFirstByCustomerIDAndGuestID(String customerID, String guestID);

    CustomerGuest findFirstByGuestID(String guestID);

    List<CustomerGuest> findAllByCustomerID(String customerID);

    List<CustomerGuest> findAllByGuestID(String guestID);

    @Modifying
    @Query("DELETE FROM CustomerGuest cg WHERE cg.customerID = ?1")
    @Transactional
    void deleteAllByCustomerID(String customerID);

    @Modifying
    @Query("DELETE FROM CustomerGuest cg WHERE cg.guestID = ?1")
    @Transactional
    void deleteAllByGuestID(String guestID);
}
