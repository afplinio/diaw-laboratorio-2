package br.pucminas.diaw.aluguel.security;

import br.pucminas.diaw.aluguel.model.Usuario;
import br.pucminas.diaw.aluguel.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Autentica a requisição pelo token Bearer. O usuário é relido do banco para invalidar contas excluídas. */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecalho = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecalho != null && cabecalho.startsWith(PREFIXO)) {
            jwtService.extrairUsuarioId(cabecalho.substring(PREFIXO.length()))
                    .flatMap(usuarioRepository::findById)
                    .ifPresent(this::autenticar);
        }
        chain.doFilter(request, response);
    }

    private void autenticar(Usuario usuario) {
        var principal = new UsuarioAutenticado(usuario.getId(), usuario.getLogin(), usuario.getPapel());
        var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPapel().name()));
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, autoridades));
    }
}
