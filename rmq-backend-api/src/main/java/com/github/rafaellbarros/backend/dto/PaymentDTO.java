package com.github.rafaellbarros.backend.dto;

import java.math.BigDecimal;


public record PaymentDTO(String orderNumber, BigDecimal value, String product) { }
