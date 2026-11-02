package br.pucminas.diaw.aluguel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreditoRequest(
        @NotNull(message = "Selecione o banco")
        Long bancoId,

        @NotNull(message = "Informe o valor do crédito")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Valor inválido")
        BigDecimal valor,

        @NotNull(message = "Informe a taxa de juros")
        @DecimalMin(value = "0.00", message = "A taxa não pode ser negativa")
        @Digits(integer = 3, fraction = 2, message = "Taxa inválida")
        BigDecimal taxaJurosMensal,

        @NotNull(message = "Informe o número de parcelas")
        @Min(value = 1, message = "Informe ao menos 1 parcela")
        @Max(value = 120, message = "Informe no máximo 120 parcelas")
        Integer parcelas) {
}
