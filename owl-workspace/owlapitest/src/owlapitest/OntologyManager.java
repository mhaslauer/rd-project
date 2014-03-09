package owlapitest;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyManager {
	
	private static OWLOntologyManager manager = null;
	/**
	 * This Method creates an Ontology Manager if it hasn't been created yet.
	 * @return Returns an OWLOntologyManager Class
	 */
	public static OWLOntologyManager getManager()
	{
		if(manager == null)
		{
			manager = OWLManager.createOWLOntologyManager();
		}
		
		return manager;
	}
	
}
