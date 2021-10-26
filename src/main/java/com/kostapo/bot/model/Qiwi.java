package com.kostapo.bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Entity
@Table(name = "qiwiPayment")
public class Qiwi {

    @Id
    @Column(name = "id_payment")
    private String idPay;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "status")
    private String status;

    @Column(name = "data")
    private ZonedDateTime data;

    @Column(name = "user_id")
    private String user_id;

    public String getIdPay() {
        return idPay;
    }

    public void setIdPay(String idPay) {
        this.idPay = idPay;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getData() {
        return data;
    }

    public void setData(ZonedDateTime data) {
        this.data = data;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
