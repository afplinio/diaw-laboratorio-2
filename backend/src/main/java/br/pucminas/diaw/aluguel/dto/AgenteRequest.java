package br.pucminas.diaw.aluguel.dto;

import br.pucminas.diaw.aluguel.model.TipoAgente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

public record AgenteRequest(
        @NotBlank(message = "Informe o usuário")
        @Pattern(regexp = "[A-Za-z0-9._-]{3,60}", message = "Use de 3 a 60 letras, números, ponto, hífen ou sublinhado")
        String login,

        @NotBlank(message = "Informe a senha")
        @Size(min = 8, max = 72, message = "A senha deve ter de 8 a 72 caracteres")
        String senha,

        @NotBlank(message = "Informe o nome")
        @Size(max = 120, message = "Use no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "Informe o CNPJ")
        @CNPJ(message = "CNPJ inválido")
        String cnpj,

        @NotNull(message = "Informe o tipo do agente")
        TipoAgente tipo) {
}
