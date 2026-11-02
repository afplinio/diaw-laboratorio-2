package br.pucminas.diaw.aluguel.dto;

import br.pucminas.diaw.aluguel.model.PedidoAluguel;
import br.pucminas.diaw.aluguel.model.StatusPedido;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PedidoResponse(Long id, Long clienteId, String clienteNome, BigDecimal rendaTotalCliente,
                             AutomovelResponse automovel, LocalDate dataInicio, LocalDate dataFim,
                             BigDecimal valorProposto, String observacoes, StatusPedido status,
                             String parecerFinanceiro, String avaliadorNome, Instant criadoEm, Instant avaliadoEm) {

    public static PedidoResponse de(PedidoAluguel pedido) {
        return new PedidoResponse(pedido.getId(), pedido.getCliente().getId(), pedido.getCliente().getNome(),
                pedido.getCliente().getRendaTotal(), AutomovelResponse.de(pedido.getAutomovel()),
                pedido.getDataInicio(), pedido.getDataFim(), pedido.getValorProposto(), pedido.getObservacoes(),
                pedido.getStatus(), pedido.getParecerFinanceiro(),
                pedido.getAvaliador() == null ? null : pedido.getAvaliador().getNome(),
                pedido.getCriadoEm(), pedido.getAvaliadoEm());
    }
}
