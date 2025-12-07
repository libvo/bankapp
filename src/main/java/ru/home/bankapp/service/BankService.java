package ru.home.bankapp.service;

import ru.home.bankapp.dao.BankDao;
import ru.home.bankapp.domain.Bank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BankService {

  private final BankDao bankDao;

  public BankService(BankDao bankDao) {
    this.bankDao = bankDao;
  }

  public List<Bank> listAll() {
    return bankDao.findAll();
  }

  public Optional<Bank> find(String bic) {
    return bankDao.find(bic);
  }

  public Bank create(String bic, String name, BigDecimal balance) {

    if (bankDao.find(bic).isPresent()) {
      throw new IllegalStateException("BANK_EXISTS");
    }

    Bank bank = new Bank(bic, name, balance);
    bankDao.insert(bank);

    return bank;
  }

  public void update(String bic, String name, BigDecimal balance) {

    Bank existing = bankDao.find(bic)
        .orElseThrow(() -> new IllegalStateException("NOT_FOUND"));

    if (balance != null) {
      existing.setBalance(balance);
    }

    bankDao.update(existing);
  }

  public void delete(String bic) {

    Bank existing = bankDao.find(bic)
        .orElseThrow(() -> new IllegalStateException("NOT_FOUND"));

    bankDao.delete(existing.getBic());
  }
}
