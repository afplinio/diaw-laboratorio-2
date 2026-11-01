package br.pucminas.diaw.aluguel.controller;

import br.pucminas.diaw.aluguel.dto.ClienteRequest;
import br.pucminas.diaw.aluguel.dto.ClienteResponse;
import br.pucminas.diaw.aluguel.dto.ClienteUpdateRequest;
import br.pucminas.diaw.aluguel.security.UsuarioAutenticado;
import br.pucminas.diaw.aluguel.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse cadastrar(@Valid @RequestBody ClienteRequest request) {
        return clienteService.cadastrar(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('AGENTE')")
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return clienteService.buscar(id, usuario);
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteUpdateRequest request,
                                     @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return clienteService.atualizar(id, request, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario) {
        clienteService.excluir(id, usuario);
    }
}
