package ru.home.bankapp.web.dto;

import java.math.BigDecimal;

public record UpdateBankRequest(
    String name,
    BigDecimal balance
) {}
