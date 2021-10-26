package com.kostapo.bot.model;


import javax.persistence.*;
import java.sql.Date;
import java.time.ZonedDateTime;

@Entity
@Table(name = "withdraw")
public class Withdraw {

    @Id
    @Column(name = "id_draw")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id_draw;

    @Column(name = "id_user")
    private String id_user;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "status")
    private String status;

    @Column(name = "data")
    private String data;

    @Column(name = "Purse")
    private String purse;

    public String getPurse() {
        return purse;
    }

    public void setPurse(String purse) {
        this.purse = purse;
    }

    public Integer getId_draw() {
        return id_draw;
    }

    public void setId_draw(Integer id_draw) {
        this.id_draw = id_draw;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
