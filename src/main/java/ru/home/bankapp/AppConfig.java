package ru.home.bankapp;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import ru.home.bankapp.dao.JooqBankDao;
import ru.home.bankapp.dao.JooqTransactionDao;
import ru.home.bankapp.service.BankService;
import ru.home.bankapp.service.PaymentTransactionService;
import ru.home.bankapp.web.handlers.BankHandler;
import ru.home.bankapp.web.handlers.TransactionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.Connection;
import java.sql.DriverManager;

public class AppConfig {

  private static final DSLContext dsl;
  private static final BankService bankService;
  private static final PaymentTransactionService transactionService;
  private static final ObjectMapper mapper;

  static {
    try {
      Connection conn = DriverManager.getConnection(
          "jdbc:postgresql://localhost:5432/bankapp",
          "postgres",
          "postgres"
      );

      dsl = DSL.using(conn);

      var bankDao = new JooqBankDao(dsl);
      var txDao = new JooqTransactionDao(dsl);

      bankService = new BankService(bankDao);
      transactionService = new PaymentTransactionService(dsl, bankDao, txDao);

      mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static BankHandler getBankHandler() {
    return new BankHandler(bankService, mapper);
  }

  public static TransactionHandler getTransactionHandler() {
    return new TransactionHandler(transactionService, "/xsd/PaymentRequest.xsd");
  }
}
