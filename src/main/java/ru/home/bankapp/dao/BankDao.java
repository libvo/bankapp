package ru.home.bankapp.dao;

import ru.home.bankapp.domain.Bank;
import java.util.List;
import java.util.Optional;

public interface BankDao {

  List<Bank> findAll();

  Optional<Bank> find(String bic);

  void insert(Bank bank);

  void update(Bank bank);

  void delete(String bic);
}
