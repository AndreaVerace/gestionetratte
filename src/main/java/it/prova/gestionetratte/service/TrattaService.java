package it.prova.gestionetratte.service;

import java.time.LocalTime;
import java.util.List;

import it.prova.gestionetratte.model.Tratta;


public interface TrattaService {

	List<Tratta> listAllElements(boolean eager);

	Tratta caricaSingoloElemento(Long id);

	Tratta caricaSingoloElementoEager(Long id);

	Tratta aggiorna(Tratta trattaInstance);

	Tratta inserisciNuovo(Tratta trattaInstance);

	void rimuovi(Tratta trattaInstance);

	List<Tratta> findByExample(Tratta example);
	
	List<Tratta> findAllByOraAtterraggioBefore(LocalTime oraAttuale);
	
	
	
}
