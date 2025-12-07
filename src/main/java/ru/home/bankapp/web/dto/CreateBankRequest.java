package ru.home.bankapp.web.dto;

import java.math.BigDecimal;

public record CreateBankRequest(String bic, String name, BigDecimal balance) {

}
