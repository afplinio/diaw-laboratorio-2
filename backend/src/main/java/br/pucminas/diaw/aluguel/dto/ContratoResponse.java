package br.pucminas.diaw.aluguel.dto;

import br.pucminas.diaw.aluguel.model.ContratoAluguel;
import br.pucminas.diaw.aluguel.model.ContratoCredito;
import br.pucminas.diaw.aluguel.model.TipoProprietario;
import java.math.BigDecimal;
import java.time.Instant;

public record ContratoResponse(Long id, Long pedidoId, String clienteNome, AutomovelResponse automovel,
                               String agenteNome, TipoProprietario tipoProprietario, String proprietarioNome,
                               Instant assinadoEm, Credito credito) {

    public record Credito(String bancoNome, BigDecimal valor, BigDecimal taxaJurosMensal, Integer parcelas) {

        static Credito de(ContratoCredito credito) {
            return credito == null ? null : new Credito(credito.getBanco().getNome(), credito.getValor(),
                    credito.getTaxaJurosMensal(), credito.getParcelas());
        }
    }

    public static ContratoResponse de(ContratoAluguel contrato) {
        return new ContratoResponse(contrato.getId(), contrato.getPedido().getId(),
                contrato.getPedido().getCliente().getNome(), AutomovelResponse.de(contrato.getPedido().getAutomovel()),
                contrato.getAgente().getNome(), contrato.getTipoProprietario(), contrato.getProprietario().getNome(),
                contrato.getAssinadoEm(), Credito.de(contrato.getContratoCredito()));
    }
}
