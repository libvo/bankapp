package ru.home.bankapp.web.handlers;

import io.vertx.ext.web.RoutingContext;
import ru.home.bankapp.config.ObjectMapperConfig;
import ru.home.bankapp.dao.NotificationDao;
import ru.home.bankapp.domain.BankNotification;
import ru.home.bankapp.domain.Type;
import ru.home.bankapp.web.dto.CreateNotificationRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public class NotificationHandler {

  private final NotificationDao dao;

  public NotificationHandler(NotificationDao dao) {
    this.dao = dao;
  }

  public void create(RoutingContext ctx) {
    try {
      var mapper = ObjectMapperConfig.get();

      CreateNotificationRequest req =
          mapper.readValue(ctx.body().asString(), CreateNotificationRequest.class);

      BankNotification n = new BankNotification(
          null,
          req.bankBic(),
          UUID.fromString(req.txId()),
          Type.valueOf(req.type()),
          req.filePath(),
          OffsetDateTime.now()
      );

      dao.insert(n);

      ctx.response()
          .putHeader("Content-Type", "application/json")
          .setStatusCode(201)
          .end(mapper.writeValueAsString(n));

    } catch (Exception e) {
      ctx.response()
          .putHeader("Content-Type", "application/json")
          .setStatusCode(400)
          .end("{\"error\":\"" + e.getMessage() + "\"}");
    }
  }
}
