package br.pucminas.diaw.aluguel.controller;

import br.pucminas.diaw.aluguel.dto.AgenteRequest;
import br.pucminas.diaw.aluguel.dto.AgenteResponse;
import br.pucminas.diaw.aluguel.service.AgenteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agentes")
public class AgenteController {

    private final AgenteService agenteService;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgenteResponse cadastrar(@Valid @RequestBody AgenteRequest request) {
        return agenteService.cadastrar(request);
    }

    @GetMapping("/bancos")
    @PreAuthorize("hasRole('AGENTE')")
    public List<AgenteResponse> listarBancos() {
        return agenteService.listarBancos();
    }
}
