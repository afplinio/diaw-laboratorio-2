package br.pucminas.diaw.aluguel.dto;

import br.pucminas.diaw.aluguel.model.TipoProprietario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ContratoRequest(
        @NotNull(message = "Informe o tipo de proprietário")
        TipoProprietario tipoProprietario,

        @Valid
        CreditoRequest credito) {
}
