package br.pucminas.diaw.aluguel.dto;

import br.pucminas.diaw.aluguel.model.Cliente;
import java.math.BigDecimal;
import java.util.List;

public record ClienteResponse(Long id, String login, String rg, String cpf, String nome, String endereco,
                              String profissao, List<RendimentoDto> rendimentos, BigDecimal rendaTotal) {

    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getLogin(), cliente.getRg(), cliente.getCpf(),
                cliente.getNome(), cliente.getEndereco(), cliente.getProfissao(),
                cliente.getRendimentos().stream().map(RendimentoDto::de).toList(), cliente.getRendaTotal());
    }
}
