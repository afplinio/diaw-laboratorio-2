package br.pucminas.diaw.aluguel.service;

import br.pucminas.diaw.aluguel.dto.PedidoRequest;
import br.pucminas.diaw.aluguel.dto.PedidoResponse;
import br.pucminas.diaw.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.diaw.aluguel.exception.RegraNegocioException;
import br.pucminas.diaw.aluguel.model.Automovel;
import br.pucminas.diaw.aluguel.model.Cliente;
import br.pucminas.diaw.aluguel.model.PedidoAluguel;
import br.pucminas.diaw.aluguel.model.StatusPedido;
import br.pucminas.diaw.aluguel.repository.AutomovelRepository;
import br.pucminas.diaw.aluguel.repository.ClienteRepository;
import br.pucminas.diaw.aluguel.repository.PedidoAluguelRepository;
import br.pucminas.diaw.aluguel.security.UsuarioAutenticado;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoAluguelRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final AutomovelRepository automovelRepository;

    public PedidoService(PedidoAluguelRepository pedidoRepository, ClienteRepository clienteRepository,
                         AutomovelRepository automovelRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.automovelRepository = automovelRepository;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest request, Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        Automovel automovel = validarAutomovelEPeriodo(request, null);
        PedidoAluguel pedido = new PedidoAluguel(cliente, automovel, request.dataInicio(), request.dataFim(),
                request.valorProposto(), textoOuNulo(request.observacoes()));
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(UsuarioAutenticado usuario, StatusPedido status) {
        List<PedidoAluguel> pedidos;
        if (!usuario.isAgente()) {
            pedidos = pedidoRepository.findByClienteIdOrderByCriadoEmDesc(usuario.id());
        } else if (status != null) {
            pedidos = pedidoRepository.findByStatusOrderByCriadoEmDesc(status);
        } else {
            pedidos = pedidoRepository.findAllByOrderByCriadoEmDesc();
        }
        return pedidos.stream().map(PedidoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id, UsuarioAutenticado usuario) {
        PedidoAluguel pedido = obter(id);
        exigirAcesso(pedido, usuario);
        return PedidoResponse.de(pedido);
    }

    @Transactional
    public PedidoResponse modificar(Long id, PedidoRequest request, UsuarioAutenticado usuario) {
        PedidoAluguel pedido = obter(id);
        exigirAcesso(pedido, usuario);
        boolean permitido = pedido.getStatus() == StatusPedido.EM_ANALISE
                || (usuario.isAgente() && pedido.getStatus() == StatusPedido.APROVADO);
        if (!permitido) {
            throw new RegraNegocioException("Este pedido não pode mais ser modificado");
        }
        Automovel automovel = validarAutomovelEPeriodo(request, id);
        pedido.modificar(automovel, request.dataInicio(), request.dataFim(), request.valorProposto(),
                textoOuNulo(request.observacoes()));
        return PedidoResponse.de(pedido);
    }

    @Transactional
    public PedidoResponse cancelar(Long id, Long clienteId) {
        PedidoAluguel pedido = obter(id);
        if (!pedido.pertenceAoCliente(clienteId)) {
            throw new AccessDeniedException("Acesso negado");
        }
        if (pedido.getStatus() != StatusPedido.EM_ANALISE && pedido.getStatus() != StatusPedido.APROVADO) {
            throw new RegraNegocioException("Este pedido não pode mais ser cancelado");
        }
        pedido.cancelar();
        return PedidoResponse.de(pedido);
    }

    private Automovel validarAutomovelEPeriodo(PedidoRequest request, Long pedidoId) {
        if (request.dataFim().isBefore(request.dataInicio())) {
            throw new RegraNegocioException("A data de término não pode ser anterior à data de início");
        }
        Automovel automovel = automovelRepository.findById(request.automovelId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));
        if (!automovel.isDisponivelParaAluguel()) {
            throw new RegraNegocioException("Este automóvel não está disponível para aluguel");
        }
        if (pedidoRepository.existeContratoNoPeriodo(automovel.getId(), request.dataInicio(), request.dataFim(), pedidoId)) {
            throw new RegraNegocioException("Este automóvel já está contratado neste período");
        }
        return automovel;
    }

    private void exigirAcesso(PedidoAluguel pedido, UsuarioAutenticado usuario) {
        if (!usuario.isAgente() && !pedido.pertenceAoCliente(usuario.id())) {
            throw new AccessDeniedException("Acesso negado");
        }
    }

    private PedidoAluguel obter(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));
    }

    private String textoOuNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
