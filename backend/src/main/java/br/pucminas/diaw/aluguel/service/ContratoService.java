package br.pucminas.diaw.aluguel.service;

import br.pucminas.diaw.aluguel.dto.ContratoRequest;
import br.pucminas.diaw.aluguel.dto.ContratoResponse;
import br.pucminas.diaw.aluguel.dto.CreditoRequest;
import br.pucminas.diaw.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.diaw.aluguel.exception.RegraNegocioException;
import br.pucminas.diaw.aluguel.model.Agente;
import br.pucminas.diaw.aluguel.model.Automovel;
import br.pucminas.diaw.aluguel.model.ContratoAluguel;
import br.pucminas.diaw.aluguel.model.ContratoCredito;
import br.pucminas.diaw.aluguel.model.PedidoAluguel;
import br.pucminas.diaw.aluguel.model.StatusPedido;
import br.pucminas.diaw.aluguel.model.TipoProprietario;
import br.pucminas.diaw.aluguel.model.Usuario;
import br.pucminas.diaw.aluguel.repository.AgenteRepository;
import br.pucminas.diaw.aluguel.repository.AutomovelRepository;
import br.pucminas.diaw.aluguel.repository.ContratoAluguelRepository;
import br.pucminas.diaw.aluguel.repository.PedidoAluguelRepository;
import br.pucminas.diaw.aluguel.security.UsuarioAutenticado;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContratoService {

    private final ContratoAluguelRepository contratoRepository;
    private final PedidoAluguelRepository pedidoRepository;
    private final AgenteRepository agenteRepository;
    private final AutomovelRepository automovelRepository;

    public ContratoService(ContratoAluguelRepository contratoRepository, PedidoAluguelRepository pedidoRepository,
                           AgenteRepository agenteRepository, AutomovelRepository automovelRepository) {
        this.contratoRepository = contratoRepository;
        this.pedidoRepository = pedidoRepository;
        this.agenteRepository = agenteRepository;
        this.automovelRepository = automovelRepository;
    }

    @Transactional
    public ContratoResponse executar(Long pedidoId, ContratoRequest request, Long agenteId) {
        PedidoAluguel pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));
        if (pedido.getStatus() != StatusPedido.APROVADO) {
            throw new RegraNegocioException("Somente pedidos aprovados podem virar contrato");
        }
        Agente agente = obterAgente(agenteId);

        // O bloqueio impede que dois contratos simultâneos reservem o mesmo automóvel.
        Automovel automovel = automovelRepository.buscarComBloqueio(pedido.getAutomovel().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));
        if (!automovel.isDisponivelParaAluguel()) {
            throw new RegraNegocioException("Este automóvel não está mais disponível para aluguel");
        }
        if (pedidoRepository.existeContratoNoPeriodo(automovel.getId(), pedido.getDataInicio(), pedido.getDataFim(), pedidoId)) {
            throw new RegraNegocioException("Este automóvel já está contratado neste período");
        }

        ContratoCredito credito = criarCredito(request.credito(), pedido);
        Usuario proprietario = definirProprietario(request.tipoProprietario(), pedido, agente, credito);

        automovel.registrarProprietario(proprietario);
        pedido.marcarContratado();
        ContratoAluguel contrato = new ContratoAluguel(pedido, agente, request.tipoProprietario(), proprietario, credito);
        return ContratoResponse.de(contratoRepository.save(contrato));
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> listar(UsuarioAutenticado usuario) {
        List<ContratoAluguel> contratos = usuario.isAgente()
                ? contratoRepository.listarDoAgente(usuario.id())
                : contratoRepository.findByPedidoClienteIdOrderByAssinadoEmDesc(usuario.id());
        return contratos.stream().map(ContratoResponse::de).toList();
    }

    private ContratoCredito criarCredito(CreditoRequest request, PedidoAluguel pedido) {
        if (request == null) {
            return null;
        }
        Agente banco = obterAgente(request.bancoId());
        if (!banco.isBanco()) {
            throw new RegraNegocioException("O crédito deve ser concedido por um banco agente");
        }
        if (request.valor().compareTo(pedido.getValorProposto()) > 0) {
            throw new RegraNegocioException("O crédito não pode superar o valor do pedido");
        }
        return new ContratoCredito(banco, request.valor(), request.taxaJurosMensal(), request.parcelas());
    }

    private Usuario definirProprietario(TipoProprietario tipo, PedidoAluguel pedido, Agente agente,
                                        ContratoCredito credito) {
        return switch (tipo) {
            case CLIENTE -> pedido.getCliente();
            case EMPRESA -> {
                if (agente.isBanco()) {
                    throw new RegraNegocioException("Somente uma empresa agente pode registrar o automóvel como propriedade da empresa");
                }
                yield agente;
            }
            case BANCO -> {
                if (credito == null) {
                    throw new RegraNegocioException("A propriedade do banco exige um contrato de crédito");
                }
                yield credito.getBanco();
            }
        };
    }

    private Agente obterAgente(Long id) {
        return agenteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado"));
    }
}
