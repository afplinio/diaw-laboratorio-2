package br.pucminas.diaw.aluguel.controller;

import br.pucminas.diaw.aluguel.dto.LoginRequest;
import br.pucminas.diaw.aluguel.dto.LoginResponse;
import br.pucminas.diaw.aluguel.dto.UsuarioResponse;
import br.pucminas.diaw.aluguel.security.UsuarioAutenticado;
import br.pucminas.diaw.aluguel.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UsuarioResponse usuarioAtual(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return authService.buscarUsuario(usuario.id());
    }
}
