package ru.home.bankapp.web.handlers;

import io.vertx.ext.web.RoutingContext;
import java.io.InputStream;
import javax.xml.validation.Schema;
import ru.home.bankapp.config.ObjectMapperConfig;
import ru.home.bankapp.domain.Status;
import ru.home.bankapp.domain.Transaction;
import ru.home.bankapp.service.PaymentTransactionService;
import ru.home.bankapp.web.ErrorResponse;
import ru.home.bankapp.xml.PaymentRequest;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.UnmarshalException;
import java.io.StringReader;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TransactionHandler {

  private final PaymentTransactionService service;
  private final Unmarshaller unmarshaller;

  public TransactionHandler(PaymentTransactionService service, String xsdPath) {
    this.service = service;

    try {
      JAXBContext context = JAXBContext.newInstance(PaymentRequest.class);
      Unmarshaller um = context.createUnmarshaller();

      SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      InputStream xsdStream = getClass().getResourceAsStream(xsdPath);
      if (xsdStream == null) {
        throw new IllegalStateException("XSD not found: " + xsdPath);
      }

      Schema schema = sf.newSchema(new javax.xml.transform.stream.StreamSource(xsdStream));
      um.setSchema(schema);

      this.unmarshaller = um;

    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize TransactionHandler: " + e.getMessage(), e);
    }
  }

  public void create(RoutingContext ctx) {
    try {
      String xml = ctx.body().asString();
      PaymentRequest req = (PaymentRequest) unmarshaller.unmarshal(new StringReader(xml));

      Transaction tx = new Transaction(
          UUID.randomUUID(),
          req.getSenderCard(),
          req.getReceiverCard(),
          req.getSenderAccount(),
          req.getReceiverAccount(),
          req.getAmount(),
          req.getAcquirerBic(),
          req.getIssuerBic(),
          req.getReceiverBic(),
          Status.ACTIVE,
          OffsetDateTime.now(),
          null
      );

      service.execute(tx);

      ctx.response().setStatusCode(201)
          .putHeader("Content-Type", "application/json")
          .end("""
                         {"transactionId":"%s","status":"COMMITTED"}
                         """.formatted(tx.getId()));

    } catch (UnmarshalException e) {
      ctx.response().setStatusCode(400)
          .end(ErrorResponse.json("VALIDATION_ERROR"));

    } catch (IllegalStateException e) {
      if (e.getMessage().equals("INSUFFICIENT_FUNDS")) {
        ctx.response().setStatusCode(409)
            .end(ErrorResponse.json("INSUFFICIENT_FUNDS"));
      } else {
        ctx.response().setStatusCode(400)
            .end(ErrorResponse.json(e.getMessage()));
      }

    } catch (Exception e) {
      ctx.response().setStatusCode(500)
          .end(ErrorResponse.json("INTERNAL_ERROR"));
    }
  }

  public void get(RoutingContext ctx) {
    try {
      var mapper = ObjectMapperConfig.get();
      UUID id = UUID.fromString(ctx.pathParam("id"));

      var txOpt = service.find(id);
      if (txOpt.isEmpty()) {
        ctx.response().setStatusCode(404)
            .end(ErrorResponse.json("NOT_FOUND"));
        return;
      }

      ctx.response().putHeader("Content-Type", "application/json")
          .end(mapper.writeValueAsString(txOpt.get()));

    } catch (Exception e) {
      ctx.response().setStatusCode(404)
          .end(ErrorResponse.json("NOT_FOUND"));
    }
  }

  public void rollback(RoutingContext ctx) {
    try {
      UUID id = UUID.fromString(ctx.pathParam("id"));
      Transaction tx = service.rollback(id);

      ctx.response().setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end("""
                        {"id":"%s","status":"ROLLED_BACK"}
                        """.formatted(tx.getId()));

    } catch (IllegalStateException e) {

      if (e.getMessage().equals("NOT_FOUND")) {
        ctx.response().setStatusCode(404).end(ErrorResponse.json("NOT_FOUND"));
        return;
      }

      if (e.getMessage().equals("ALREADY_ROLLED_BACK")) {
        ctx.response().setStatusCode(400)
            .end(ErrorResponse.json("ALREADY_ROLLED_BACK"));
        return;
      }

      if (e.getMessage().equals("ACTIVE_TRANSACTION_CANNOT_BE_ROLLED_BACK")) {
        ctx.response().setStatusCode(400)
            .end(ErrorResponse.json("ACTIVE_TRANSACTION_CANNOT_BE_ROLLED_BACK"));
        return;
      }

      ctx.response().setStatusCode(400)
          .end(ErrorResponse.json("BAD_REQUEST"));

    } catch (Exception e) {
      ctx.response().setStatusCode(500)
          .end(ErrorResponse.json("INTERNAL_ERROR"));
    }
  }
}
