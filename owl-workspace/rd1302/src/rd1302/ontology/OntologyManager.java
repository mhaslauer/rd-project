package rd1302.ontology;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;



/**
 * This class holds all information about the ontology and the manager. This class is a singleton
 * @author mhaslauer
 *
 */
public class OntologyManager {

	private static OWLOntology ontology = null;
	private static String path = null;
	private static OWLOntologyManager manager = null;
	private static String ontologyIRI = null;
	
	/**
	 * Get OntologyManager if it is created yet. Otherwise create one.
	 * @return OWLOntologyManager manager
	 */
	public static OWLOntologyManager getManager()
	{
		if(manager == null)
		{
			manager = OWLManager.createOWLOntologyManager();
		}
		
		return manager;
	}
	
	/**
	 * Load an ontology from an path
	 * @param pathtoontology
	 * @throws OWLOntologyCreationException
	 */
	 public static void loadOntology(String pathtoontology) throws OWLOntologyCreationException {
		path = pathtoontology;
		File file = new File(path);
        
        //load the local copy
		ontology = getManager().loadOntologyFromOntologyDocument(file);
        ontologyIRI = ontology.getOntologyID().getOntologyIRI().toString();
        System.out.println("Loaded Ontology: " + ontology);
        System.out.println("Ontologoy IRI: " + ontologyIRI);
	}
	 
	 /**
	  * returns the loaded ontology
	  * @return OWLOntology
	  */
	 public static OWLOntology getOntology(){
		 return ontology;
	 }
	 
	 /**
	 * Remove the ontology from the manager
	 */
	 public static void unloadOntology() {
		
        manager.removeOntology(ontology);
        
		ontology = null;
        ontologyIRI = null;
        path = null;
	}
	 
	/**
	 * Save the modified ontology to a file
	 * @throws OWLOntologyStorageException 
	 */
	public static void saveOntology() throws OWLOntologyStorageException {
		File file = new File(path);
        manager.saveOntology(ontology, IRI.create(file.toURI()));
        OWLOntologyFormat format = manager.getOntologyFormat(ontology);
        System.out.println("Ontology saved with format: " + format);
	}
	
	/**
	 * Function to get the IRI of the loaded ontology
	 * @return Ontology IRI as String
	 */
	public static String getOntologyIRI(){
		return ontologyIRI;
	}
	
}
