package br.pucminas.diaw.aluguel.controller;

import br.pucminas.diaw.aluguel.dto.PedidoRequest;
import br.pucminas.diaw.aluguel.dto.PedidoResponse;
import br.pucminas.diaw.aluguel.model.StatusPedido;
import br.pucminas.diaw.aluguel.security.UsuarioAutenticado;
import br.pucminas.diaw.aluguel.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse criar(@Valid @RequestBody PedidoRequest request,
                                @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return pedidoService.criar(request, usuario.id());
    }

    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) StatusPedido status,
                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return pedidoService.listar(usuario, status);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return pedidoService.buscar(id, usuario);
    }

    @PutMapping("/{id}")
    public PedidoResponse modificar(@PathVariable Long id, @Valid @RequestBody PedidoRequest request,
                                    @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return pedidoService.modificar(id, request, usuario);
    }

    @PostMapping("/{id}/cancelamento")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse cancelar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return pedidoService.cancelar(id, usuario.id());
    }
}
