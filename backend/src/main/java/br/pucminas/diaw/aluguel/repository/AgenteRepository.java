package br.pucminas.diaw.aluguel.repository;

import br.pucminas.diaw.aluguel.model.Agente;
import br.pucminas.diaw.aluguel.model.TipoAgente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenteRepository extends JpaRepository<Agente, Long> {

    boolean existsByCnpj(String cnpj);

    List<Agente> findByTipoOrderByNome(TipoAgente tipo);
}
