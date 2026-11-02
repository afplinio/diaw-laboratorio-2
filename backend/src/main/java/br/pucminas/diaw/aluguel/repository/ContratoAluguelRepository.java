package br.pucminas.diaw.aluguel.repository;

import br.pucminas.diaw.aluguel.model.ContratoAluguel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContratoAluguelRepository extends JpaRepository<ContratoAluguel, Long> {

    List<ContratoAluguel> findByPedidoClienteIdOrderByAssinadoEmDesc(Long clienteId);

    @Query("""
            select c from ContratoAluguel c
            left join c.contratoCredito credito
            where c.agente.id = :agenteId or credito.banco.id = :agenteId
            order by c.assinadoEm desc
            """)
    List<ContratoAluguel> listarDoAgente(@Param("agenteId") Long agenteId);
}
