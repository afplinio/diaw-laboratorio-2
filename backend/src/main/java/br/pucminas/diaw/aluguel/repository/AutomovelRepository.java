package br.pucminas.diaw.aluguel.repository;

import br.pucminas.diaw.aluguel.model.Automovel;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {

    boolean existsByPlaca(String placa);

    boolean existsByMatricula(String matricula);

    boolean existsByPlacaAndIdNot(String placa, Long id);

    boolean existsByMatriculaAndIdNot(String matricula, Long id);

    boolean existsByProprietarioId(Long proprietarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Automovel a where a.id = :id")
    Optional<Automovel> buscarComBloqueio(@Param("id") Long id);
}
