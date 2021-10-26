package com.kostapo.bot.model;

import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "Users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id"}))
public class User {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "chat_id")
    private Integer chat_id;

    @Column(name = "user_name")
    private String user_name;

    @Column(name = "referral")
    private String referral;

    @Column(name = "lvl")
    private Integer level = 0;

    @Column(name = "balance")
    private Double balance = 0.0;

    @Column(name = "pay")
    private BigDecimal payment;

    @Column(name = "Purse")
    private String purse;

    @Column(name = "Ban")
    private Boolean Ban = false;

    public Boolean getBan() {
        return Ban;
    }

    public void setBan(Boolean ban) {
        Ban = ban;
    }

    public String getPurse() {
        return purse;
    }

    public void setPurse(String purse) {
        this.purse = purse;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getId() {
        return id;
    }

    public void setReferral(String referral){
        this.referral = referral;
    }

    public String getReferral(){
        return referral;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChat_id() {
        return chat_id;
    }

    public void setChat_id(Integer chat_id) {
        this.chat_id = chat_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
