package com.revolut.challenge.service;

import com.revolut.challenge.model.Account;
import com.revolut.challenge.model.MoneyTransfer;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.LongStream;

public class AccountServiceLoadTest {

    private static final int MAX_AMOUNT = 30_000_000;

    private static final int NUM_OF_TRANSFERS = 30_000;

    private static final int NUM_OF_ACCOUNTS = 10;

    private AccountService accountService = new AccountService();

    private static List<Account> accounts = new ArrayList<>();

    private static Random random = new Random();

    @Test
    public void loadTest() {
        LongStream.range(0, NUM_OF_ACCOUNTS).forEach(i -> {
            Account acc = createAccount(i);
            accountService.addAccount(acc);
            accounts.add(acc);
        });

        runAsync(accounts);

        BigDecimal actualAmount = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assert.assertEquals(new BigDecimal(MAX_AMOUNT).multiply(new BigDecimal(NUM_OF_ACCOUNTS)),
                actualAmount);
    }

    private void runAsync(List<Account> accounts) {
        List<CompletableFuture> futures = new ArrayList<>();

        for (int i = 0; i < NUM_OF_TRANSFERS; i++) {
            for (int j = 0; j < NUM_OF_ACCOUNTS; j++) {
                int from = j;
                int to = (j == NUM_OF_ACCOUNTS - 1) ? 0 : (from + 1);
                futures.add(CompletableFuture.runAsync(() ->
                        accountService.transfer(getMoneyTransferInstruction(from, to))));
            }
        }
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private MoneyTransfer getMoneyTransferInstruction(int fromIndex, int toIndex) {
        MoneyTransfer m = new MoneyTransfer();
        m.setAmount(new BigDecimal(random.nextInt(MAX_AMOUNT)));
        m.setFromAccount(accounts.get(fromIndex).getAccountNo());
        m.setToAccount(accounts.get(toIndex).getAccountNo());
        return m;
    }

    private Account createAccount(long no) {
        Account a = new Account();
        a.setAccountNo(no);
        a.setBalance(new BigDecimal(MAX_AMOUNT));
        return a;
    }
}