package br.pucminas.diaw.aluguel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoRequest(
        @NotNull(message = "Selecione um automóvel")
        Long automovelId,

        @NotNull(message = "Informe a data de início")
        @FutureOrPresent(message = "A data de início não pode estar no passado")
        LocalDate dataInicio,

        @NotNull(message = "Informe a data de término")
        LocalDate dataFim,

        @NotNull(message = "Informe o valor proposto")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Valor inválido")
        BigDecimal valorProposto,

        @Size(max = 500, message = "Use no máximo 500 caracteres")
        String observacoes) {
}
