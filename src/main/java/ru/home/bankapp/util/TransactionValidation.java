package ru.home.bankapp.util;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import ru.home.bankapp.domain.Status;

public class TransactionValidation {

  public static String validateCard(String card) {
    if (card == null || !card.matches("\\d{16}")) {
      throw new IllegalArgumentException("Card number must be exactly 16 digits");
    }
    return card;
  }

  public static String validateAccountNumber(String number) {
    if (number == null || number.isBlank()) {
      throw new IllegalArgumentException("Account number must not be empty");
    }
    return number.trim();
  }

  public static BigDecimal validateAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    return amount;
  }

  public static String validateBic(String bic) {
    return BankValidation.validateBic(bic);
  }

  public static OffsetDateTime validateRolledBackStatus(OffsetDateTime rolledBackAt,
      Status status) {
    if (status == Status.ROLLED_BACK && rolledBackAt == null) {
      throw new IllegalArgumentException("rolledBackAt required for ROLLED_BACK");
    }
    if (status != Status.ROLLED_BACK && rolledBackAt != null) {
      throw new IllegalArgumentException("rolledBackAt must be null unless status=ROLLED_BACK");
    }

    return rolledBackAt;
  }

}
