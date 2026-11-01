package br.pucminas.diaw.aluguel.repository;

import br.pucminas.diaw.aluguel.model.PedidoAluguel;
import br.pucminas.diaw.aluguel.model.StatusPedido;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoAluguelRepository extends JpaRepository<PedidoAluguel, Long> {

    List<PedidoAluguel> findByClienteIdOrderByCriadoEmDesc(Long clienteId);

    List<PedidoAluguel> findAllByOrderByCriadoEmDesc();

    List<PedidoAluguel> findByStatusOrderByCriadoEmDesc(StatusPedido status);

    boolean existsByClienteId(Long clienteId);

    boolean existsByAutomovelId(Long automovelId);

    /** Verifica se o automóvel já está contratado em algum dia do período informado. */
    @Query("""
            select count(p) > 0 from PedidoAluguel p
            where p.automovel.id = :automovelId
              and p.status = br.pucminas.diaw.aluguel.model.StatusPedido.CONTRATADO
              and p.dataInicio <= :fim
              and p.dataFim >= :inicio
              and (:ignorarId is null or p.id <> :ignorarId)
            """)
    boolean existeContratoNoPeriodo(@Param("automovelId") Long automovelId,
                                    @Param("inicio") LocalDate inicio,
                                    @Param("fim") LocalDate fim,
                                    @Param("ignorarId") Long ignorarId);
}
