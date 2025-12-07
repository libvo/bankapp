package ru.home.bankapp.web.dto;

import java.math.BigDecimal;

public record CreateTransactionRequest(String senderCard, String receiverCard, String senderAccount,
                                       String receiverAccount, BigDecimal amount,
                                       String acquirerBic, String issuerBic, String receiverBic) {

}
