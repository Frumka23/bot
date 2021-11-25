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

    public Double getBalance_vkl() {
        return balance_vkl;
    }

    public void setBalance_vkl(Double balance_vkl) {
        this.balance_vkl = balance_vkl;
    }

    @Column(name = "balance_vkl")
    private Double balance_vkl = 0.0;

    @Column(name = "pay")
    private BigDecimal payment = BigDecimal.valueOf(0);

    @Column(name = "Purse")
    private String purse;

    @Column(name = "Ban")
    private Boolean Ban = false;

    @Column(name = "referral_2")
    private String Referral_2;

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Column(name = "lastMessage")
    private String lastMessage = "Главное меню";

    @Column(name = "top")
    private Double top = 0.0;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "password")
    private String password;

    public Double getUnt() {
        return unt;
    }

    public void setUnt(Double unt) {
        this.unt = unt;
    }

    @Column(name = "unt")
    private Double unt = 0.0;

    public Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
    }

    @Column(name = "block")
    private Boolean block = false;

    public Double getTop() {
        return top;
    }

    public void setTop(Double top) {
        this.top = top;
    }

    public Integer getIntScore() {
        return intScore;
    }

    public void setIntScore(Integer intScore) {
        this.intScore = intScore;
    }

    @Column(name = "int_score")
    private Integer intScore;

    public String getReferral_2() {
        return Referral_2;
    }

    public void setReferral_2(String referral_2) {
        Referral_2 = referral_2;
    }

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
