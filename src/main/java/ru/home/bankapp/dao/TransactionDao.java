package ru.home.bankapp.dao;

import ru.home.bankapp.domain.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionDao {

  void insert(Transaction tx);

  Optional<Transaction> find(UUID id);

  void update(Transaction tx);
}
