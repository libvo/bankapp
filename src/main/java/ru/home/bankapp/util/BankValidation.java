package ru.home.bankapp.util;

import java.math.BigDecimal;

public class BankValidation {

  public static String validateBic(String bic) {
    if (bic == null || bic.isEmpty()) {
      throw new IllegalArgumentException("Bic is empty");
    }
    return bic;
  }

  public static String validateName(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Name is empty");
    }
    return name;
  }

  public static BigDecimal validateBalance(BigDecimal balance) {
    if (balance == null) {
      throw new IllegalArgumentException("Balance is empty");
    }

    if (balance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Balance is negative");
    }

    return balance;
  }
}
