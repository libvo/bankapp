package ru.home.bankapp.dao;

import org.jooq.DSLContext;
import ru.home.bankapp.domain.Bank;
import ru.home.bankapp.jooq.tables.Banks;
import ru.home.bankapp.jooq.tables.records.BanksRecord;

import java.util.List;
import java.util.Optional;

public class JooqBankDao implements BankDao {

  private final DSLContext dsl;

  public JooqBankDao(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public List<Bank> findAll() {
    return dsl.selectFrom(Banks.BANKS)
        .fetch()
        .map(r -> new Bank(
            r.getBic(),
            r.getName(),
            r.getBalance(),
            r.getUpdatedAt()
        ));
  }

  @Override
  public Optional<Bank> find(String bic) {
    BanksRecord r = dsl.selectFrom(Banks.BANKS)
        .where(Banks.BANKS.BIC.eq(bic))
        .fetchOne();

    if (r == null) {
      return Optional.empty();
    }

    return Optional.of(new Bank(
        r.getBic(),
        r.getName(),
        r.getBalance(),
        r.getUpdatedAt()
    ));
  }

  @Override
  public void insert(Bank bank) {
    dsl.insertInto(Banks.BANKS)
        .set(Banks.BANKS.BIC, bank.getBic())
        .set(Banks.BANKS.NAME, bank.getName())
        .set(Banks.BANKS.BALANCE, bank.getBalance())
        .set(Banks.BANKS.UPDATED_AT, bank.getUpdatedAt())
        .execute();
  }

  @Override
  public void update(Bank bank) {
    dsl.update(Banks.BANKS)
        .set(Banks.BANKS.NAME, bank.getName())
        .set(Banks.BANKS.BALANCE, bank.getBalance())
        .set(Banks.BANKS.UPDATED_AT, bank.getUpdatedAt())
        .where(Banks.BANKS.BIC.eq(bank.getBic()))
        .execute();
  }

  @Override
  public void delete(String bic) {
    dsl.deleteFrom(Banks.BANKS)
        .where(Banks.BANKS.BIC.eq(bic))
        .execute();
  }
}
