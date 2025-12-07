package ru.home.bankapp.web.errors;

import io.vertx.ext.web.RoutingContext;

public class ErrorHandlers {

  public static void badRequest(RoutingContext ctx) {
    ctx.response()
        .setStatusCode(400)
        .putHeader("Content-Type", "application/json")
        .end("{\"error\":\"VALIDATION_ERROR\"}");
  }

  public static void notFound(RoutingContext ctx) {
    ctx.response()
        .setStatusCode(404)
        .putHeader("Content-Type", "application/json")
        .end("{\"error\":\"NOT_FOUND\"}");
  }

  public static void conflict(RoutingContext ctx) {
    ctx.response()
        .setStatusCode(409)
        .putHeader("Content-Type", "application/json")
        .end("{\"error\":\"CONFLICT\"}");
  }

  public static void internal(RoutingContext ctx) {
    ctx.response()
        .setStatusCode(500)
        .putHeader("Content-Type", "application/json")
        .end("{\"error\":\"INTERNAL_ERROR\"}");
  }
}
