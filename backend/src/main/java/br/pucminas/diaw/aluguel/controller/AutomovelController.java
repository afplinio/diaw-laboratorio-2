package br.pucminas.diaw.aluguel.controller;

import br.pucminas.diaw.aluguel.dto.AutomovelRequest;
import br.pucminas.diaw.aluguel.dto.AutomovelResponse;
import br.pucminas.diaw.aluguel.security.UsuarioAutenticado;
import br.pucminas.diaw.aluguel.service.AutomovelService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/automoveis")
public class AutomovelController {

    private final AutomovelService automovelService;

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    @GetMapping
    public List<AutomovelResponse> listar(@RequestParam(defaultValue = "false") boolean disponiveis) {
        return automovelService.listar(disponiveis);
    }

    @GetMapping("/{id}")
    public AutomovelResponse buscar(@PathVariable Long id) {
        return automovelService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AGENTE')")
    public AutomovelResponse cadastrar(@Valid @RequestBody AutomovelRequest request,
                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return automovelService.cadastrar(request, usuario.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGENTE')")
    public AutomovelResponse atualizar(@PathVariable Long id, @Valid @RequestBody AutomovelRequest request,
                                       @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return automovelService.atualizar(id, request, usuario.id());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('AGENTE')")
    public void excluir(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario) {
        automovelService.excluir(id, usuario.id());
    }
}
