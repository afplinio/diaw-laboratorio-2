package br.pucminas.diaw.aluguel.dto;

import br.pucminas.diaw.aluguel.model.Agente;
import br.pucminas.diaw.aluguel.model.TipoAgente;

public record AgenteResponse(Long id, String nome, String cnpj, TipoAgente tipo) {

    public static AgenteResponse de(Agente agente) {
        return new AgenteResponse(agente.getId(), agente.getNome(), agente.getCnpj(), agente.getTipo());
    }
}
