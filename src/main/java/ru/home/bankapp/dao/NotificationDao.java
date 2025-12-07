package ru.home.bankapp.dao;

import ru.home.bankapp.domain.BankNotification;

public interface NotificationDao {

  void insert(BankNotification n);
}
