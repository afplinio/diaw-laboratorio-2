package br.pucminas.diaw.aluguel.service;

import br.pucminas.diaw.aluguel.dto.AgenteRequest;
import br.pucminas.diaw.aluguel.dto.AgenteResponse;
import br.pucminas.diaw.aluguel.exception.RegraNegocioException;
import br.pucminas.diaw.aluguel.model.Agente;
import br.pucminas.diaw.aluguel.model.TipoAgente;
import br.pucminas.diaw.aluguel.repository.AgenteRepository;
import br.pucminas.diaw.aluguel.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenteService {

    private final AgenteRepository agenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AgenteService(AgenteRepository agenteRepository, UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.agenteRepository = agenteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AgenteResponse cadastrar(AgenteRequest request) {
        String login = request.login().trim();
        String cnpj = Documentos.somenteDigitos(request.cnpj());
        if (usuarioRepository.existsByLogin(login)) {
            throw new RegraNegocioException("Este usuário já está em uso");
        }
        if (agenteRepository.existsByCnpj(cnpj)) {
            throw new RegraNegocioException("Este CNPJ já está cadastrado");
        }
        Agente agente = new Agente(login, passwordEncoder.encode(request.senha()), request.nome().trim(), cnpj,
                request.tipo());
        return AgenteResponse.de(agenteRepository.save(agente));
    }

    @Transactional(readOnly = true)
    public List<AgenteResponse> listarBancos() {
        return agenteRepository.findByTipoOrderByNome(TipoAgente.BANCO).stream().map(AgenteResponse::de).toList();
    }
}
