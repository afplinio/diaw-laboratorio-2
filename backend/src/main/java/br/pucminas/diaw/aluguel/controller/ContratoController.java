package br.pucminas.diaw.aluguel.controller;

import br.pucminas.diaw.aluguel.dto.ContratoRequest;
import br.pucminas.diaw.aluguel.dto.ContratoResponse;
import br.pucminas.diaw.aluguel.security.UsuarioAutenticado;
import br.pucminas.diaw.aluguel.service.ContratoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @PostMapping("/api/pedidos/{pedidoId}/contrato")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AGENTE')")
    public ContratoResponse executar(@PathVariable Long pedidoId, @Valid @RequestBody ContratoRequest request,
                                     @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return contratoService.executar(pedidoId, request, usuario.id());
    }

    @GetMapping("/api/contratos")
    public List<ContratoResponse> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return contratoService.listar(usuario);
    }
}
