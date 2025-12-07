package ru.home.bankapp.service;

import org.jooq.DSLContext;
import ru.home.bankapp.dao.BankDao;
import ru.home.bankapp.dao.TransactionDao;
import ru.home.bankapp.domain.Bank;
import ru.home.bankapp.domain.Status;
import ru.home.bankapp.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

public class PaymentTransactionService {

  private final DSLContext dsl;
  private final BankDao bankDao;
  private final TransactionDao transactionDao;

  private static final Logger log = LoggerFactory.getLogger(PaymentTransactionService.class);

  public PaymentTransactionService(
      DSLContext dsl,
      BankDao bankDao,
      TransactionDao transactionDao
  ) {
    this.dsl = dsl;
    this.bankDao = bankDao;
    this.transactionDao = transactionDao;
  }

  public void execute(Transaction tx) {

    log.info("Starting transaction id={} amount={} issuer={} receiver={}",
        tx.getId(), tx.getAmount(), tx.getIssuerBic(), tx.getReceiverBic());

    dsl.transaction(conf -> {

      Bank issuer = bankDao.find(tx.getIssuerBic())
          .orElseThrow(() -> {
            log.error("Issuer bank not found: {}", tx.getIssuerBic());
            return new IllegalStateException("Issuer not found");
          });

      Bank receiver = bankDao.find(tx.getReceiverBic())
          .orElseThrow(() -> {
            log.error("Receiver bank not found: {}", tx.getReceiverBic());
            return new IllegalStateException("Receiver not found");
          });

      BigDecimal balance = issuer.getBalance();
      BigDecimal amount = tx.getAmount();

      if (balance.compareTo(amount) < 0) {
        log.warn("Insufficient funds: issuer={} balance={} required={}",
            issuer.getBic(), balance, amount);
        throw new IllegalStateException("INSUFFICIENT_FUNDS");
      }

      log.debug("Debiting issuer={} oldBalance={} newBalance={}",
          issuer.getBic(), balance, balance.subtract(amount));

      issuer.setBalance(balance.subtract(amount));
      bankDao.update(issuer);

      log.debug("Crediting receiver={} oldBalance={} newBalance={}",
          receiver.getBic(), receiver.getBalance(),
          receiver.getBalance().add(amount));

      receiver.setBalance(receiver.getBalance().add(amount));
      bankDao.update(receiver);

      tx.setStatus(Status.COMMITTED);
      transactionDao.insert(tx);

      log.info("Transaction committed: id={}", tx.getId());
    });
  }

  public Optional<Transaction> find(UUID id) {
    log.debug("Searching for transaction id={}", id);
    return transactionDao.find(id);
  }

  public Transaction rollback(UUID id) {

    log.info("Starting rollback for transaction id={}", id);

    Transaction tx = transactionDao.find(id)
        .orElseThrow(() -> {
          log.error("Transaction not found id={}", id);
          return new IllegalStateException("NOT_FOUND");
        });

    if (tx.getStatus() == Status.ROLLED_BACK) {
      log.warn("Transaction already rolled back id={}", id);
      throw new IllegalStateException("ALREADY_ROLLED_BACK");
    }

    if (tx.getStatus() == Status.ACTIVE) {
      log.error("Attempt to rollback ACTIVE transaction id={}", id);
      throw new IllegalStateException("ACTIVE_TRANSACTION_CANNOT_BE_ROLLED_BACK");
    }

    dsl.transaction(conf -> {

      Bank issuer = bankDao.find(tx.getIssuerBic())
          .orElseThrow(() -> {
            log.error("Issuer bank not found for rollback: {}", tx.getIssuerBic());
            return new IllegalStateException("Issuer not found");
          });

      Bank receiver = bankDao.find(tx.getReceiverBic())
          .orElseThrow(() -> {
            log.error("Receiver bank not found for rollback: {}", tx.getReceiverBic());
            return new IllegalStateException("Receiver not found");
          });

      BigDecimal amount = tx.getAmount();

      log.debug("Rollback: returning amount={} to issuer={} and removing from receiver={}",
          amount, issuer.getBic(), receiver.getBic());

      if (receiver.getBalance().compareTo(amount) < 0) {
        log.error("Receiver has insufficient balance for rollback id={}, receiver={}, balance={}, required={}",
            tx.getId(), receiver.getBic(), receiver.getBalance(), amount);
        throw new IllegalStateException("ROLLBACK_FAILED_INTEGRITY");
      }

      receiver.setBalance(receiver.getBalance().subtract(amount));
      bankDao.update(receiver);

      issuer.setBalance(issuer.getBalance().add(amount));
      bankDao.update(issuer);

      tx.setStatus(Status.ROLLED_BACK);
      transactionDao.update(tx);

      log.info("Rollback completed for transaction id={}", id);
    });

    return tx;
  }
}

