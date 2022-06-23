package it.prova.gestionetratte.web.api;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.gestionetratte.dto.TrattaDTO;
import it.prova.gestionetratte.model.Airbus;
import it.prova.gestionetratte.model.Stato;
import it.prova.gestionetratte.model.Tratta;
import it.prova.gestionetratte.service.AirbusService;
import it.prova.gestionetratte.service.TrattaService;
import it.prova.gestionetratte.web.api.exception.IdNotNullForInsertException;
import it.prova.gestionetratte.web.api.exception.TrattaNotFoundException;


@RestController
@RequestMapping("api/tratta")
public class TrattaController {

	@Autowired
	private TrattaService trattaService;
	
	@Autowired
	private AirbusService airbusService;
	
	@GetMapping
	public List<TrattaDTO> getAll() {
		return TrattaDTO.createTrattaDTOListFromModelList(trattaService.listAllElements(true), true);
	}
	
	@PostMapping
	public TrattaDTO createNew(@Valid @RequestBody TrattaDTO trattaInput) {
		// se mi viene inviato un id jpa lo interpreta come update ed a me (producer)
		// non sta bene
		if (trattaInput.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");

		Tratta trattaInserita = trattaService.inserisciNuovo(trattaInput.buildTrattaModel());
		return TrattaDTO.buildTrattaDTOFromModel( trattaInserita, true);
	}
	
	@GetMapping("/{id}")
	public TrattaDTO findById(@PathVariable(value = "id", required = true) long id) {
		Tratta tratta = trattaService.caricaSingoloElementoEager(id);

		if (tratta == null)
			throw new TrattaNotFoundException("Tratta not found con id: " + id);

		return TrattaDTO.buildTrattaDTOFromModel(tratta, true);
	}
	
	@PutMapping("/{id}")
	public TrattaDTO update(@Valid @RequestBody TrattaDTO trattaInput, @PathVariable(required = true) Long id) {
		Tratta tratta = trattaService.caricaSingoloElemento(id);

		if (tratta == null)
			throw new TrattaNotFoundException("Tratta not found con id: " + id);

		trattaInput.setId(id);
		Tratta trattaAggiornata = trattaService.aggiorna(trattaInput.buildTrattaModel());
		return TrattaDTO.buildTrattaDTOFromModel(trattaAggiornata, true);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(required = true) Long id) {
		Tratta tratta = trattaService.caricaSingoloElemento(id);

		if (tratta == null)
			throw new TrattaNotFoundException("Tratta not found con id: " + id);

		trattaService.rimuovi(tratta);
	}
	
	@PostMapping("/search")
	public List<TrattaDTO> search(@RequestBody TrattaDTO example) {
		return TrattaDTO.createTrattaDTOListFromModelList(trattaService.findByExample(example.buildTrattaModel()),
				true);
	}

	@GetMapping("/concludiTratte")
	public List<TrattaDTO> concludiTratte(){
		
		for(Tratta tratta : trattaService.findAllByOraAtterraggioBefore(LocalTime.now())) {
			if(tratta.getStato().equals(Stato.ATTIVA)) {
				tratta.setStato(Stato.CONCLUSA);
				trattaService.aggiorna(tratta);
			}
		}
		
		return TrattaDTO.createTrattaDTOListFromModelList(trattaService
				.findAllByOraAtterraggioBefore(LocalTime.now()), true);
	}
	
	@GetMapping("/listaAirbusEvidenziandoSovrapposizioni")
	public List<TrattaDTO> listaAirbusEvidenziandoSovrapposizioni(){
		List<TrattaDTO> result = new ArrayList<>();
		for(Airbus airbus : airbusService.listAllElements()) {
			if(airbus.getTratte().size() > 1) {
				for(int i = 1; i < airbus.getTratte().size(); i++) {
					Tratta trattaUno = airbus.getTratte().stream().toList().get(0);			
					Tratta trattaN = airbus.getTratte().stream().toList().get(i);
					
					if(trattaUno.getOraDecollo().isAfter(trattaN.getOraDecollo()) && trattaUno.getOraDecollo().isBefore(trattaN.getOraAtterraggio())
							|| trattaUno.getOraAtterraggio().isBefore(trattaN.getOraAtterraggio()) && trattaUno.getOraAtterraggio().isAfter(trattaN.getOraDecollo())) {
						result.add(TrattaDTO.buildTrattaDTOFromModel(trattaUno,true));
					}
					if(trattaN.getOraDecollo().isAfter(trattaUno.getOraDecollo()) && trattaN.getOraDecollo().isBefore(trattaUno.getOraAtterraggio())
							|| trattaN.getOraAtterraggio().isBefore(trattaUno.getOraAtterraggio()) && trattaN.getOraAtterraggio().isAfter(trattaUno.getOraDecollo())) {
						result.add(TrattaDTO.buildTrattaDTOFromModel(trattaN, true));
					}
				}
			}
		}
		for(TrattaDTO tratta : result) {
			tratta.getAirbus().setConSovrapposizioni(true);
		}
		return result;
	}
	
	
	
	
}
