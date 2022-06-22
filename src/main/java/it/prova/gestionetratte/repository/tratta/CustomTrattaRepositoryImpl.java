package it.prova.gestionetratte.repository.tratta;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CustomTrattaRepositoryImpl implements CustomTrattaRepository {

	@PersistenceContext
	private EntityManager entityManager;
	
}
