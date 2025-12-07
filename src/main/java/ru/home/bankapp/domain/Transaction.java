package ru.home.bankapp.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import ru.home.bankapp.util.TransactionValidation;

public class Transaction {

  private final String senderCardNumber;
  private final String receiverCardNumber;
  private final String senderAccountNumber;
  private final String receiverAccountNumber;
  private final BigDecimal amount;
  private final String acquirerBic;
  private final String issuerBic;
  private final String receiverBic;

  private final UUID id;
  private Status status;
  private final OffsetDateTime createdAt;
  private OffsetDateTime rolledBackAt;


  public Transaction(
      UUID id,
      String senderCardNumber,
      String receiverCardNumber,
      String senderAccountNumber,
      String receiverAccountNumber,
      BigDecimal amount,
      String acquirerBic,
      String issuerBic,
      String receiverBic,
      Status status,
      OffsetDateTime createdAt,
      OffsetDateTime rolledBackAt
  ) {
    this.senderCardNumber = TransactionValidation.validateCard(senderCardNumber);
    this.receiverCardNumber = TransactionValidation.validateCard(receiverCardNumber);
    this.senderAccountNumber = TransactionValidation.validateAccountNumber(senderAccountNumber);
    this.receiverAccountNumber = TransactionValidation.validateAccountNumber(receiverAccountNumber);
    this.amount = TransactionValidation.validateAmount(amount);

    this.acquirerBic = TransactionValidation.validateBic(acquirerBic);
    this.issuerBic = TransactionValidation.validateBic(issuerBic);
    this.receiverBic = TransactionValidation.validateBic(receiverBic);

    this.id = id != null ? id : UUID.randomUUID();
    this.status = status != null ? status : Status.ACTIVE;
    this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
    this.rolledBackAt = TransactionValidation.validateRolledBackStatus(rolledBackAt, status);
  }

  public boolean isCompleted() {
    return status == Status.COMMITTED;
  }

  public boolean isRolledBack() {
    return status == Status.ROLLED_BACK;
  }

  public boolean isActive() {
    return status == Status.ACTIVE;
  }

  public UUID getId() {
    return id;
  }

  public String getSenderCardNumber() {
    return senderCardNumber;
  }

  public String getReceiverCardNumber() {
    return receiverCardNumber;
  }

  public String getSenderAccountNumber() {
    return senderAccountNumber;
  }

  public String getReceiverAccountNumber() {
    return receiverAccountNumber;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getAcquirerBic() {
    return acquirerBic;
  }

  public String getIssuerBic() {
    return issuerBic;
  }

  public String getReceiverBic() {
    return receiverBic;
  }

  public Status getStatus() {
    return status;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getRolledBackAt() {
    return rolledBackAt;
  }

  public void setStatus(Status status) {
    this.status =  status;
    if (status == Status.ROLLED_BACK) {
      this.rolledBackAt = OffsetDateTime.now();
    }
  }
}