package ru.home.bankapp.dao;

import org.jooq.DSLContext;
import ru.home.bankapp.domain.BankNotification;
import ru.home.bankapp.jooq.tables.Notifications;

public class JooqNotificationDao implements NotificationDao {

  private final DSLContext dsl;

  public JooqNotificationDao(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public void insert(BankNotification n) {
    dsl.insertInto(Notifications.NOTIFICATIONS)
        .set(Notifications.NOTIFICATIONS.ID, n.getId())
        .set(Notifications.NOTIFICATIONS.BANK_BIC, n.getBankBic())
        .set(Notifications.NOTIFICATIONS.TX_ID, n.getTransactionId())
        .set(Notifications.NOTIFICATIONS.TYPE, n.getType().name())
        .set(Notifications.NOTIFICATIONS.FILE_PATH, n.getFilePath())
        .set(Notifications.NOTIFICATIONS.CREATED_AT, n.getCreatedAt())
        .execute();
  }
}
