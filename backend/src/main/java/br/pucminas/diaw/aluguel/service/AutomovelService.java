package br.pucminas.diaw.aluguel.service;

import br.pucminas.diaw.aluguel.dto.AutomovelRequest;
import br.pucminas.diaw.aluguel.dto.AutomovelResponse;
import br.pucminas.diaw.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.diaw.aluguel.exception.RegraNegocioException;
import br.pucminas.diaw.aluguel.model.Agente;
import br.pucminas.diaw.aluguel.model.Automovel;
import br.pucminas.diaw.aluguel.repository.AgenteRepository;
import br.pucminas.diaw.aluguel.repository.AutomovelRepository;
import java.time.Year;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutomovelService {

    private final AutomovelRepository automovelRepository;
    private final AgenteRepository agenteRepository;

    public AutomovelService(AutomovelRepository automovelRepository, AgenteRepository agenteRepository) {
        this.automovelRepository = automovelRepository;
        this.agenteRepository = agenteRepository;
    }

    @Transactional
    public AutomovelResponse cadastrar(AutomovelRequest request, Long agenteId) {
        Dados dados = Dados.de(request);
        if (automovelRepository.existsByPlaca(dados.placa())) {
            throw new RegraNegocioException("Esta placa já está cadastrada");
        }
        if (automovelRepository.existsByMatricula(dados.matricula())) {
            throw new RegraNegocioException("Esta matrícula já está cadastrada");
        }
        Agente agente = agenteRepository.findById(agenteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado"));
        Automovel automovel = new Automovel(dados.matricula(), request.ano(), dados.marca(), dados.modelo(),
                dados.placa(), agente);
        return AutomovelResponse.de(automovelRepository.save(automovel));
    }

    @Transactional(readOnly = true)
    public List<AutomovelResponse> listar(boolean somenteDisponiveis) {
        return automovelRepository.findAll().stream()
                .filter(a -> !somenteDisponiveis || a.isDisponivelParaAluguel())
                .map(AutomovelResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public AutomovelResponse buscar(Long id) {
        return AutomovelResponse.de(obter(id));
    }

    @Transactional
    public AutomovelResponse atualizar(Long id, AutomovelRequest request, Long agenteId) {
        Automovel automovel = obterDoProprietario(id, agenteId);
        Dados dados = Dados.de(request);
        if (automovelRepository.existsByPlacaAndIdNot(dados.placa(), id)) {
            throw new RegraNegocioException("Esta placa já está cadastrada");
        }
        if (automovelRepository.existsByMatriculaAndIdNot(dados.matricula(), id)) {
            throw new RegraNegocioException("Esta matrícula já está cadastrada");
        }
        automovel.atualizarDados(dados.matricula(), request.ano(), dados.marca(), dados.modelo(), dados.placa());
        return AutomovelResponse.de(automovel);
    }

    @Transactional
    public void excluir(Long id, Long agenteId) {
        Automovel automovel = obterDoProprietario(id, agenteId);
        automovelRepository.delete(automovel);
    }

    private Automovel obter(Long id) {
        return automovelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));
    }

    private Automovel obterDoProprietario(Long id, Long agenteId) {
        Automovel automovel = obter(id);
        if (!automovel.getProprietario().getId().equals(agenteId)) {
            throw new AccessDeniedException("Acesso negado");
        }
        return automovel;
    }

    private record Dados(String matricula, String marca, String modelo, String placa) {

        static Dados de(AutomovelRequest request) {
            if (request.ano() > Year.now().getValue() + 1) {
                throw new RegraNegocioException("Ano inválido");
            }
            return new Dados(request.matricula().trim(), request.marca().trim(), request.modelo().trim(),
                    request.placa().replace("-", "").toUpperCase());
        }
    }
}
