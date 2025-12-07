package ru.home.bankapp.domain;

import ru.home.bankapp.util.BankNotificationValidation;

import java.time.OffsetDateTime;
import java.util.UUID;

public class BankNotification {

  private final UUID id;
  private final String bankBic;
  private final UUID transactionId;
  private final Type type;
  private final String filePath;
  private final OffsetDateTime createdAt;

  public BankNotification(
      UUID id,
      String bankBic,
      UUID transactionId,
      Type type,
      String filePath,
      OffsetDateTime createdAt
  ) {
    this.id = id != null ? id : UUID.randomUUID();
    this.bankBic = BankNotificationValidation.validateBic(bankBic);
    this.transactionId = BankNotificationValidation.validateTxId(transactionId);
    this.type = BankNotificationValidation.validateType(type);
    this.filePath = filePath;
    this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
  }

  public UUID getId() {
    return id;
  }

  public String getBankBic() {
    return bankBic;
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public Type getType() {
    return type;
  }

  public String getFilePath() {
    return filePath;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}
