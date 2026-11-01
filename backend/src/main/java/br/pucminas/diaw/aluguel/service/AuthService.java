package br.pucminas.diaw.aluguel.service;

import br.pucminas.diaw.aluguel.dto.LoginRequest;
import br.pucminas.diaw.aluguel.dto.LoginResponse;
import br.pucminas.diaw.aluguel.dto.UsuarioResponse;
import br.pucminas.diaw.aluguel.exception.CredenciaisInvalidasException;
import br.pucminas.diaw.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.diaw.aluguel.model.Usuario;
import br.pucminas.diaw.aluguel.repository.UsuarioRepository;
import br.pucminas.diaw.aluguel.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByLogin(request.login().trim())
                .filter(u -> passwordEncoder.matches(request.senha(), u.getSenhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);
        return new LoginResponse(jwtService.gerarToken(usuario), UsuarioResponse.de(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}
