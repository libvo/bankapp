package ru.home.bankapp.dao;

import org.jooq.DSLContext;
import ru.home.bankapp.domain.Status;
import ru.home.bankapp.domain.Transaction;
import ru.home.bankapp.jooq.tables.Transactions;
import ru.home.bankapp.jooq.tables.records.TransactionsRecord;

import java.util.Optional;
import java.util.UUID;

public class JooqTransactionDao implements TransactionDao {

  private final DSLContext dsl;

  public JooqTransactionDao(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public void insert(Transaction tx) {
    dsl.insertInto(Transactions.TRANSACTIONS)
        .set(Transactions.TRANSACTIONS.ID, tx.getId())
        .set(Transactions.TRANSACTIONS.SENDER_CARD, tx.getSenderCardNumber())
        .set(Transactions.TRANSACTIONS.RECEIVER_CARD, tx.getReceiverCardNumber())
        .set(Transactions.TRANSACTIONS.SENDER_ACCOUNT, tx.getSenderAccountNumber())
        .set(Transactions.TRANSACTIONS.RECEIVER_ACCOUNT, tx.getReceiverAccountNumber())
        .set(Transactions.TRANSACTIONS.AMOUNT, tx.getAmount())
        .set(Transactions.TRANSACTIONS.ACQUIRER_BIC, tx.getAcquirerBic())
        .set(Transactions.TRANSACTIONS.ISSUER_BIC, tx.getIssuerBic())
        .set(Transactions.TRANSACTIONS.RECEIVER_BIC, tx.getReceiverBic())
        .set(Transactions.TRANSACTIONS.STATUS, tx.getStatus().name())
        .set(Transactions.TRANSACTIONS.CREATED_AT, tx.getCreatedAt())
        .set(Transactions.TRANSACTIONS.ROLLED_BACK_AT, tx.getRolledBackAt())
        .execute();
  }

  @Override
  public Optional<Transaction> find(UUID id) {
    TransactionsRecord r = dsl.selectFrom(Transactions.TRANSACTIONS)
        .where(Transactions.TRANSACTIONS.ID.eq(id))
        .fetchOne();

    if (r == null) {
      return Optional.empty();
    }

    return Optional.of(new Transaction(
        r.getId(),
        r.getSenderCard(),
        r.getReceiverCard(),
        r.getSenderAccount(),
        r.getReceiverAccount(),
        r.getAmount(),
        r.getAcquirerBic(),
        r.getIssuerBic(),
        r.getReceiverBic(),
        Status.valueOf(r.getStatus()),
        r.getCreatedAt(),
        r.getRolledBackAt()
    ));
  }

  @Override
  public void update(Transaction tx) {
    dsl.update(Transactions.TRANSACTIONS)
        .set(Transactions.TRANSACTIONS.STATUS, tx.getStatus().name())
        .set(Transactions.TRANSACTIONS.ROLLED_BACK_AT, tx.getRolledBackAt())
        .where(Transactions.TRANSACTIONS.ID.eq(tx.getId()))
        .execute();
  }
}
