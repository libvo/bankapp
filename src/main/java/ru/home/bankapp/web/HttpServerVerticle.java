package ru.home.bankapp.web;


import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import org.jooq.DSLContext;
import ru.home.bankapp.config.DBConfig;
import ru.home.bankapp.config.ObjectMapperConfig;
import ru.home.bankapp.dao.JooqBankDao;
import ru.home.bankapp.dao.JooqTransactionDao;
import ru.home.bankapp.service.BankService;
import ru.home.bankapp.service.PaymentTransactionService;
import ru.home.bankapp.web.errors.ErrorHandlers;
import ru.home.bankapp.web.handlers.BankHandler;
import ru.home.bankapp.web.handlers.TransactionHandler;

public class HttpServerVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    DSLContext dsl = DBConfig.createDSL();

    var bankDao = new JooqBankDao(dsl);
    var txDao = new JooqTransactionDao(dsl);

    var bankService = new BankService(bankDao);
    var paymentService = new PaymentTransactionService(dsl, bankDao, txDao);

    var mapper = ObjectMapperConfig.get();

    BankHandler bankHandler = new BankHandler(bankService, mapper);
    TransactionHandler transactionHandler =
        new TransactionHandler(paymentService, "/xsd/PaymentRequest.xsd");

    Router router = Router.router(vertx);
    router.route().handler(io.vertx.ext.web.handler.BodyHandler.create());

    router.get("/api/banks").handler(bankHandler::list);
    router.post("/api/banks").handler(bankHandler::create);
    router.get("/api/banks/:bic").handler(bankHandler::get);
    router.put("/api/banks/:bic").handler(bankHandler::update);
    router.delete("/api/banks/:bic").handler(bankHandler::delete);

    router.post("/api/transactions").handler(transactionHandler::create);
    router.get("/api/transactions/:id").handler(transactionHandler::get);
    router.post("/api/transactions/:id/rollback").handler(transactionHandler::rollback);

    router.errorHandler(400, ErrorHandlers::badRequest);
    router.errorHandler(404, ErrorHandlers::notFound);
    router.errorHandler(409, ErrorHandlers::conflict);
    router.errorHandler(500, ErrorHandlers::internal);

    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8080);

  }
}
