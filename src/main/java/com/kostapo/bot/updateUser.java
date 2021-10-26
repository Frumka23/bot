package com.kostapo.bot;

import java.math.BigDecimal;

public class updateUser {

    private Double balance;
    private Integer lvl;
    private String ref;
    private String purse;
    private BigDecimal pay;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPurse() {
        return purse;
    }

    public void setPurse(String purse) {
        this.purse = purse;
    }

    public BigDecimal getPay() {
        return pay;
    }

    public void setPay(BigDecimal pay) {
        this.pay = pay;
    }
}