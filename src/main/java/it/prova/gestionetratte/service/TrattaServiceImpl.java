package it.prova.gestionetratte.service;

import java.time.LocalTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.prova.gestionetratte.model.Stato;
import it.prova.gestionetratte.model.Tratta;
import it.prova.gestionetratte.repository.tratta.TrattaRepository;
import it.prova.gestionetratte.web.api.exception.TrattaNotAnnullataException;

@Service
public class TrattaServiceImpl implements TrattaService {

	@Autowired
	private TrattaRepository trattaRepository;

	
	@Override
	public List<Tratta> listAllElements(boolean eager) {
		return (List<Tratta>) trattaRepository.findAll();
	}

	@Override
	public Tratta caricaSingoloElemento(Long id) {
		return trattaRepository.findById(id).orElse(null);
	}

	@Override
	public Tratta caricaSingoloElementoEager(Long id) {
		return trattaRepository.findSingleTrattaEager(id);
	}

	@Override
	@Transactional
	public Tratta aggiorna(Tratta trattaInstance) {
		return trattaRepository.save(trattaInstance);
	}

	@Override
	@Transactional
	public Tratta inserisciNuovo(Tratta trattaInstance) {
		return trattaRepository.save(trattaInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Tratta trattaInstance) {
		if(!trattaInstance.getStato().equals(Stato.ANNULLATA)) {
			throw new TrattaNotAnnullataException("Per eliminare una tratta quest'ultima deve essere annullata.");
		}
		trattaRepository.delete(trattaInstance);		
	}

	@Override
	public List<Tratta> findByExample(Tratta example) {
		return trattaRepository.findByExample(example);
	}

	@Override
	public List<Tratta> findAllByOraAtterraggioBefore(LocalTime oraAttuale) {
		 return trattaRepository.findAllByOraAtterraggioBefore(oraAttuale);
	}
	
	
	
}
