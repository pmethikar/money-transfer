package com.revolut.challenge.service;

import com.revolut.challenge.exception.InvalidAccountException;
import com.revolut.challenge.exception.InvalidAmountException;
import com.revolut.challenge.model.Account;
import com.revolut.challenge.model.MoneyTransfer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AccountServiceTest {

    private AccountService accountService;

    @Before
    public void setUp(){
        accountService = new AccountService();
    }

    @Test
    public void testAddAndGetAccount(){
        Account a = createAccount(1l, new BigDecimal("10.0"));
        accountService.addAccount(a);

        Account actualAccount =  accountService.getAccount(1L);
        assertEquals(a, actualAccount);
    }

    @Test(expected = InvalidAmountException.class)
    public void testAddAccountWithInvalidAmount(){
        Account a = createAccount(1l, new BigDecimal("-9"));
        accountService.addAccount(a);
    }

    @Test(expected = InvalidAccountException.class)
    public void testAddAccountWithDuplicateAccount(){
        Account a = createAccount(1L, new BigDecimal(10));
        accountService.addAccount(a);

        Account a2 = createAccount(1L, new BigDecimal(100));
        accountService.addAccount(a2);
    }

    @Test(expected = InvalidAmountException.class)
    public void testTRansferWithInsufficientBalance(){
        Account a1 = createAccount(1L, new BigDecimal(10));
        accountService.addAccount(a1);

        Account a2 = createAccount(2L, new BigDecimal(10));
        accountService.addAccount(a2);

        accountService.transfer(getMoneyTransferInstruction(a1, a2, new BigDecimal(12)));
    }


    @Test(expected = InvalidAccountException.class)
    public void testTransferWithInvalidFromAccount(){
        Account a1 = createAccount(1L, new BigDecimal(10));

        Account a2 = createAccount(2L, new BigDecimal(10));
        accountService.addAccount(a2);

        accountService.transfer(getMoneyTransferInstruction(a1, a2, new BigDecimal(12)));
    }

    @Test(expected = InvalidAccountException.class)
    public void testTransferWithInvalidToAccount(){
        Account a1 = createAccount(1L, new BigDecimal(10));
        accountService.addAccount(a1);

        Account a2 = createAccount(2L, new BigDecimal(10));

        accountService.transfer(getMoneyTransferInstruction(a1, a2, new BigDecimal(12)));
    }


    @Test(expected = InvalidAccountException.class)
    public void testTransferWithSameAccounts(){
        Account a1 = createAccount(1L, new BigDecimal(10));
        accountService.addAccount(a1);

        accountService.transfer(getMoneyTransferInstruction(a1, a1, new BigDecimal(2)));
    }

    @Test
    public void testTransfer(){
        Account a1 = createAccount(1L, new BigDecimal(100));
        accountService.addAccount(a1);

        Account a2 = createAccount(2L, new BigDecimal(10));
        accountService.addAccount(a2);

        accountService.transfer(getMoneyTransferInstruction(a1, a2, new BigDecimal(10)));

        assertEquals(new BigDecimal(90), a1.getBalance());
        assertEquals(new BigDecimal(20), a2.getBalance());
    }

    private Account createAccount(Long no, BigDecimal amount){
        Account a = new Account();
        a.setAccountNo(no);
        a.setBalance(amount);
        return a;
    }

    private MoneyTransfer getMoneyTransferInstruction(Account from, Account to, BigDecimal amt) {
        MoneyTransfer m = new MoneyTransfer();
        m.setAmount(amt);
        m.setFromAccount(from.getAccountNo());
        m.setToAccount(to.getAccountNo());
        return m;
    }

}
