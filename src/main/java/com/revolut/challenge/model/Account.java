package com.revolut.challenge.model;

import java.math.BigDecimal;

public class Account {
    /*Advanced things like currency (and subsequently FX), transfer limit, etc
    * are not considered for simplicity*/
    private Long accountNo;

    private BigDecimal balance;

    public Long getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(Long accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
