package ru.home.bankapp.util;

import java.util.UUID;
import ru.home.bankapp.domain.Type;

public class BankNotificationValidation {

  public static String validateBic(String bic) {
    return BankValidation.validateBic(bic);
  }

  public static UUID validateTxId(UUID txId) {
    if (txId == null) {
      throw new IllegalArgumentException("TransactionId must not be null");
    }
    return txId;
  }

  public static Type validateType(Type type) {
    if (type == null) {
      throw new IllegalArgumentException("notification type must not be null");
    }
    return type;
  }

}
