package com.kostapo.bot;

import java.math.BigDecimal;

public class updateUser {

    private Double balance;//
    private Double balanceVkl;//
    private Integer lvl;//
    private String lastMessage;//
    private String password;//
    private String username;//
    private String purse;//
    private BigDecimal pay;//
    private Boolean ban;
    private Boolean block;
    private Double top;//
    private Double unt;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getBalanceVkl() {
        return balanceVkl;
    }

    public void setBalanceVkl(Double balanceVkl) {
        this.balanceVkl = balanceVkl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getBan() {
        return ban;
    }

    public void setBan(Boolean ban) {
        this.ban = ban;
    }

    public Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
    }

    public Double getTop() {
        return top;
    }

    public void setTop(Double top) {
        this.top = top;
    }

    public Double getUnt() {
        return unt;
    }

    public void setUnt(Double unt) {
        this.unt = unt;
    }

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