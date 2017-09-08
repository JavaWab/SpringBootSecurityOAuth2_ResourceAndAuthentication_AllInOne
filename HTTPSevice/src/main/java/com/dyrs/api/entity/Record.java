package com.dyrs.api.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "t_record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, length = 50)
    private String phone;
    @Column(nullable = false, length = 500)
    private String content;
    @Column(nullable = false)
    private String jiaJuID;
    @Column(nullable = false)
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getJiaJuID() {
        return jiaJuID;
    }

    public void setJiaJuID(String jiaJuID) {
        this.jiaJuID = jiaJuID;
    }
}
