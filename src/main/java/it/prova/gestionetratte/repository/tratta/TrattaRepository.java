package it.prova.gestionetratte.repository.tratta;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.prova.gestionetratte.model.Tratta;

public interface TrattaRepository extends CrudRepository<Tratta, Long>, CustomTrattaRepository {

	@Query("from Tratta t join fetch t.airbus where t.id = ?1")
	Tratta findSingleTrattaEager(Long id);

	List<Tratta> findAllByOraAtterraggioBefore(LocalTime oraAttuale);

}
