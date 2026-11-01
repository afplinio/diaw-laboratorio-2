package br.pucminas.diaw.aluguel.service;

import br.pucminas.diaw.aluguel.dto.ClienteRequest;
import br.pucminas.diaw.aluguel.dto.ClienteResponse;
import br.pucminas.diaw.aluguel.dto.ClienteUpdateRequest;
import br.pucminas.diaw.aluguel.dto.RendimentoDto;
import br.pucminas.diaw.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.diaw.aluguel.exception.RegraNegocioException;
import br.pucminas.diaw.aluguel.model.Cliente;
import br.pucminas.diaw.aluguel.repository.ClienteRepository;
import br.pucminas.diaw.aluguel.repository.UsuarioRepository;
import br.pucminas.diaw.aluguel.security.UsuarioAutenticado;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest request) {
        String login = request.login().trim();
        String cpf = Documentos.somenteDigitos(request.cpf());
        String rg = request.rg().trim();
        if (usuarioRepository.existsByLogin(login)) {
            throw new RegraNegocioException("Este usuário já está em uso");
        }
        if (clienteRepository.existsByCpf(cpf)) {
            throw new RegraNegocioException("Este CPF já está cadastrado");
        }
        if (clienteRepository.existsByRg(rg)) {
            throw new RegraNegocioException("Este RG já está cadastrado");
        }
        Cliente cliente = new Cliente(login, passwordEncoder.encode(request.senha()), rg, cpf,
                request.nome().trim(), request.endereco().trim(), request.profissao().trim());
        cliente.definirRendimentos(request.rendimentos().stream().map(RendimentoDto::paraEntidade).toList());
        return ClienteResponse.de(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id, UsuarioAutenticado usuario) {
        if (!usuario.isAgente() && !usuario.id().equals(id)) {
            throw new AccessDeniedException("Acesso negado");
        }
        return ClienteResponse.de(obter(id));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteUpdateRequest request, UsuarioAutenticado usuario) {
        exigirProprioCliente(id, usuario);
        Cliente cliente = obter(id);
        String cpf = Documentos.somenteDigitos(request.cpf());
        String rg = request.rg().trim();
        if (clienteRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new RegraNegocioException("Este CPF já está cadastrado");
        }
        if (clienteRepository.existsByRgAndIdNot(rg, id)) {
            throw new RegraNegocioException("Este RG já está cadastrado");
        }
        cliente.atualizarDados(rg, cpf, request.nome().trim(), request.endereco().trim(), request.profissao().trim());
        cliente.definirRendimentos(request.rendimentos().stream().map(RendimentoDto::paraEntidade).toList());
        if (request.novaSenha() != null) {
            cliente.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        }
        return ClienteResponse.de(cliente);
    }

    @Transactional
    public void excluir(Long id, UsuarioAutenticado usuario) {
        exigirProprioCliente(id, usuario);
        Cliente cliente = obter(id);
        clienteRepository.delete(cliente);
    }

    private Cliente obter(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    private void exigirProprioCliente(Long id, UsuarioAutenticado usuario) {
        if (!usuario.id().equals(id)) {
            throw new AccessDeniedException("Acesso negado");
        }
    }
}
