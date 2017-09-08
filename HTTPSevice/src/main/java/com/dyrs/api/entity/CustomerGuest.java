package com.dyrs.api.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "t_customer_guest")
public class CustomerGuest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, length = 50)
    private String customerID;
    @Column(nullable = false, length = 50)
    private String guestID;
    @Column(nullable = false)
    private Date time;

    public CustomerGuest(String customer_id, String guest_id) {
        this.customerID = customer_id;
        this.guestID = guest_id;
        this.time = new Date(System.currentTimeMillis());
    }

    public CustomerGuest() {
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getGuestID() {
        return guestID;
    }

    public void setGuestID(String guestID) {
        this.guestID = guestID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
