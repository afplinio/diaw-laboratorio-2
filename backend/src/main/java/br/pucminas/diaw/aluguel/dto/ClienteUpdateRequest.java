package br.pucminas.diaw.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteUpdateRequest(
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        @Size(max = 72, message = "A senha é muito longa")
        String novaSenha,

        @NotBlank(message = "Informe o RG")
        @Size(max = 20, message = "Use no máximo 20 caracteres")
        String rg,

        @NotBlank(message = "Informe o CPF")
        @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank(message = "Informe o nome")
        @Size(max = 120, message = "Use no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "Informe o endereço")
        @Size(max = 255, message = "Use no máximo 255 caracteres")
        String endereco,

        @NotBlank(message = "Informe a profissão")
        @Size(max = 80, message = "Use no máximo 80 caracteres")
        String profissao,

        @NotNull(message = "Informe os rendimentos")
        @Size(max = 3, message = "Informe no máximo 3 rendimentos")
        List<@Valid RendimentoDto> rendimentos) {
}
