package ru.home.bankapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import ru.home.bankapp.web.handlers.BankHandler;
import ru.home.bankapp.web.handlers.TransactionHandler;

public class Main extends AbstractVerticle {

  @Override
  public void start() {

    System.out.println("Main.start() called — setting up router...");

    Router router = Router.router(vertx);

    BankHandler bankHandler = AppConfig.getBankHandler();
    TransactionHandler transactionHandler = AppConfig.getTransactionHandler();

    router.get("/api/banks").handler(bankHandler::list);
    router.get("/api/banks/:bic").handler(bankHandler::get);
    router.post("/api/banks").handler(bankHandler::create);
    router.put("/api/banks/:bic").handler(bankHandler::update);
    router.delete("/api/banks/:bic").handler(bankHandler::delete);

    router.post("/api/transactions").handler(transactionHandler::create);
    router.get("/api/transactions/:id").handler(transactionHandler::get);
    router.post("/api/transactions/:id/rollback").handler(transactionHandler::rollback);

    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8080)
        .onSuccess(server -> {
          System.out.println("HTTP server started on port 8080");
        })
        .onFailure(err -> {
          System.err.println("FAILED TO START SERVER:");
          err.printStackTrace();
        });
  }

  public static void main(String[] args) {
    System.out.println("Deploying HttpServerVerticle...");
    Vertx.vertx().deployVerticle(new ru.home.bankapp.web.HttpServerVerticle())
        .onSuccess(id -> System.out.println("DEPLOYED: " + id))
        .onFailure(Throwable::printStackTrace);
  }
}

