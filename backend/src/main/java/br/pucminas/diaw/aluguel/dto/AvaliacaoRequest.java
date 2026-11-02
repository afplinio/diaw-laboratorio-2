package br.pucminas.diaw.aluguel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequest(
        @NotNull(message = "Informe o resultado da avaliação")
        Boolean aprovado,

        @NotBlank(message = "Informe o parecer financeiro")
        @Size(max = 500, message = "Use no máximo 500 caracteres")
        String parecer) {
}
