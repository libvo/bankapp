package ru.home.bankapp.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import ru.home.bankapp.util.BankValidation;

public class Bank {

  private final String bic;
  private final String name;
  private BigDecimal balance;
  private OffsetDateTime updatedAt;

  public Bank(String bic, String name, BigDecimal balance) {
    this(bic, name, balance, OffsetDateTime.now());
  }

  public Bank(String bic, String name, BigDecimal balance, OffsetDateTime updatedAt) {
    this.bic = BankValidation.validateBic(bic);
    this.name = BankValidation.validateName(name);
    this.balance = BankValidation.validateBalance(balance);
    this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
  }

  public String getBic() {
    return bic;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = BankValidation.validateBalance(balance);
    this.updatedAt = OffsetDateTime.now();
  }

}
