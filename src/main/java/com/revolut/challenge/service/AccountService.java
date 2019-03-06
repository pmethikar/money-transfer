package com.revolut.challenge.service;

import com.revolut.challenge.exception.InvalidAccountException;
import com.revolut.challenge.exception.InvalidAmountException;
import com.revolut.challenge.model.Account;
import com.revolut.challenge.model.MoneyTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    /*For simplicity, this will act as in-memory storage for accounts.
     * Taken as ConcurrentHashMap so as to avoid handling synchronization explicitly for adding and
     * getting account, as it is not part of main requirement in the challenge.*/
    private final Map<Long, Account> accountMap = new ConcurrentHashMap<>();

    public void addAccount(Account account) {
        validateAmount(account.getBalance());
        if (!accountMap.containsKey(account.getAccountNo())) {
            accountMap.put(account.getAccountNo(), account);
            LOG.info("Account added successfully");
        } else {
            throw new InvalidAccountException("Account already exists");
        }
    }

    public Account getAccount(Long accountNo) {
        if (accountMap.containsKey(accountNo)) {
            LOG.info("Account found");
            return accountMap.get(accountNo);
        } else {
            throw new InvalidAccountException("Account not found");
        }
    }

    public BigDecimal transfer(MoneyTransfer transfer) {
        Long fromNo = transfer.getFromAccount();
        Long toNo = transfer.getToAccount();
        validateTransferAccounts(transfer);

        Account from = accountMap.get(fromNo);
        Account to = accountMap.get(toNo);

        return transferSynchronously(from, to, transfer.getAmount());
    }

    private BigDecimal transferSynchronously(Account from, Account to, BigDecimal amount) {
        Account firstLock = from.getAccountNo() < to.getAccountNo() ? from : to;
        Account secondLock = from.getAccountNo() < to.getAccountNo() ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                validateTransferAmount(from, amount);

                from.setBalance(from.getBalance().subtract(amount));
                accountMap.put(from.getAccountNo(), from);

                to.setBalance(to.getBalance().add(amount));
                accountMap.put(to.getAccountNo(), to);

                return from.getBalance();
            }
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Amount has to be positive");
        }
    }

    private void validateTransferAccounts(MoneyTransfer transfer) {
        Long from = transfer.getFromAccount();
        Long to = transfer.getToAccount();
        if (!accountMap.containsKey(from)) {
            throw new InvalidAccountException("Invalid sender account");
        } else if (!accountMap.containsKey(to)) {
            throw new InvalidAccountException("Invalid receiver account");
        } else if (from == to) {
            throw new InvalidAccountException("Sender account and Receiver accounts cannot be same");
        }
    }

    private void validateTransferAmount(Account from, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InvalidAmountException("Insufficient Balance. Maximum allowed transfer is " + from.getBalance());
        }
    }
}