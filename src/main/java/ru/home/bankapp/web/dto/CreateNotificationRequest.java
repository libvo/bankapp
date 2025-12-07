package ru.home.bankapp.web.dto;

public record CreateNotificationRequest(String bankBic, String txId, String type, String filePath) {

}
