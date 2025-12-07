package ru.home.bankapp.web.dto;

import java.math.BigDecimal;

public record BankResponse(
    String bic,
    String name,
    BigDecimal balance
) {}
