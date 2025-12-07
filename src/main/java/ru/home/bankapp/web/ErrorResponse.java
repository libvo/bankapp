package ru.home.bankapp.web;

public class ErrorResponse {

  public static String json(String msg) {
    return "{\"error\":\"" + msg + "\"}";
  }
}
