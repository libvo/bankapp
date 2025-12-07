package ru.home.bankapp.web.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.ext.web.RoutingContext;
import ru.home.bankapp.domain.Bank;
import ru.home.bankapp.service.BankService;
import ru.home.bankapp.web.ErrorResponse;
import ru.home.bankapp.web.dto.BankResponse;
import ru.home.bankapp.web.dto.CreateBankRequest;
import ru.home.bankapp.web.dto.UpdateBankRequest;

import java.util.stream.Collectors;

public class BankHandler {

  private final BankService bankService;
  private final ObjectMapper mapper;

  public BankHandler(BankService bankService, ObjectMapper mapper) {
    this.bankService = bankService;
    this.mapper = mapper;
  }

  public void list(RoutingContext ctx) {
    try {
      var result = bankService.listAll().stream()
          .map(b -> new BankResponse(b.getBic(), b.getName(), b.getBalance()))
          .collect(Collectors.toList());

      ctx.response()
          .putHeader("Content-Type", "application/json")
          .end(mapper.writeValueAsString(result));

    } catch (Exception e) {
      ctx.fail(500, e);
    }
  }

  public void create(RoutingContext ctx) {
    System.out.println("RAW BODY = " + ctx.body().asString());

    try {
      CreateBankRequest req =
          mapper.readValue(ctx.body().asString(), CreateBankRequest.class);

      Bank created = bankService.create(req.bic(), req.name(), req.balance());

      ctx.response()
          .setStatusCode(201)
          .putHeader("Content-Type", "application/json")
          .end("{\"bic\":\"" + created.getBic() + "\"}");

    } catch (IllegalStateException e) {
      if ("BANK_EXISTS".equals(e.getMessage())) {
        ctx.response().setStatusCode(409)
            .end(ErrorResponse.json("BANK_EXISTS"));
      } else {
        ctx.response().setStatusCode(400)
            .end(ErrorResponse.json("BAD_REQUEST"));
      }
    } catch (Exception e) {
      ctx.response().setStatusCode(400)
          .end(ErrorResponse.json("BAD_REQUEST"));
    }
  }

  public void get(RoutingContext ctx) {
    String bic = ctx.pathParam("bic");

    var opt = bankService.find(bic);
    if (opt.isEmpty()) {
      ctx.response().setStatusCode(404)
          .end(ErrorResponse.json("NOT_FOUND"));
      return;
    }

    var bank = opt.get();

    try {
      ctx.response()
          .putHeader("Content-Type", "application/json")
          .end(mapper.writeValueAsString(
              new BankResponse(bank.getBic(), bank.getName(), bank.getBalance())
          ));

    } catch (Exception e) {
      ctx.fail(500, e);
    }
  }

  public void update(RoutingContext ctx) {
    String bic = ctx.pathParam("bic");

    try {
      UpdateBankRequest req =
          mapper.readValue(ctx.body().asString(), UpdateBankRequest.class);

      bankService.update(bic, req.name(), req.balance());

      ctx.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end("{\"bic\":\"" + bic + "\"}");

    } catch (IllegalStateException e) {
      if ("NOT_FOUND".equals(e.getMessage())) {
        ctx.response().setStatusCode(404)
            .end(ErrorResponse.json("NOT_FOUND"));
      } else {
        ctx.response().setStatusCode(400)
            .end(ErrorResponse.json("BAD_REQUEST"));
      }
    } catch (Exception e) {
      ctx.response().setStatusCode(400)
          .end(ErrorResponse.json("BAD_REQUEST"));
    }
  }

  public void delete(RoutingContext ctx) {
    String bic = ctx.pathParam("bic");

    try {
      bankService.delete(bic);
      ctx.response().setStatusCode(204).end();

    } catch (IllegalStateException e) {
      if ("NOT_FOUND".equals(e.getMessage())) {
        ctx.response().setStatusCode(404)
            .end(ErrorResponse.json("NOT_FOUND"));
      } else {
        ctx.response().setStatusCode(400)
            .end(ErrorResponse.json("BAD_REQUEST"));
      }
    }
  }
}
