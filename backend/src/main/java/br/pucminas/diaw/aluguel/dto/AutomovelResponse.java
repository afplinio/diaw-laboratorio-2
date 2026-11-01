package br.pucminas.diaw.aluguel.dto;

import br.pucminas.diaw.aluguel.model.Automovel;
import br.pucminas.diaw.aluguel.model.TipoProprietario;

public record AutomovelResponse(Long id, String matricula, Integer ano, String marca, String modelo, String placa,
                                Long proprietarioId, String proprietarioNome, TipoProprietario tipoProprietario,
                                boolean disponivel) {

    public static AutomovelResponse de(Automovel automovel) {
        return new AutomovelResponse(automovel.getId(), automovel.getMatricula(), automovel.getAno(),
                automovel.getMarca(), automovel.getModelo(), automovel.getPlaca(),
                automovel.getProprietario().getId(), automovel.getProprietario().getNome(),
                automovel.getProprietario().getTipoProprietario(), automovel.isDisponivelParaAluguel());
    }
}
