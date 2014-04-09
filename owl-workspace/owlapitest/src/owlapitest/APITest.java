package owlapitest;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

public class APITest {
	
	private static final String ontology_file = "/Users/mhaslauer/git/rd-project/owl-workspace/owlapitest/zombiesim.owl";
	private static final String path = "/Users/mhaslauer/git/rd-project/owl-workspace/owlapitest/";

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		// TODO Auto-generated method stub
		
		OWLOntology temp = APITest.LoadOntology(ontology_file);
		//APITest.SaveOntology(temp, path);
		//APITest.printClassHierarchy(temp);
		//APITest.Reasoning(temp);
		APITest.addClass(temp);
		APITest.Reasoning(temp);
		APITest.SaveOntology(temp, path);
		
	}
	
	

    /**
	* The examples here show how to load ontologies.
	*
	* @throws OWLOntologyCreationException
	*/
    public static OWLOntology LoadOntology(String pathtoontology) throws OWLOntologyCreationException {
        // Get hold of an ontology manager
        OWLOntologyManager manager = OntologyManager.getManager();
        
        
        // Let's load an ontology from the web
        //IRI iri = IRI.create("http://owl.cs.manchester.ac.uk/co-ode-files/ontologies/pizza.owl");
        //OWLOntology pizzaOntology = manager.loadOntologyFromOntologyDocument(iri);
        //System.out.println("Loaded ontology: " + pizzaOntology);
        // Remove the ontology so that we can load a local copy.
        //manager.removeOntology(pizzaOntology);
        
        
        // We can also load ontologies from files. Download the pizza ontology
        // from http://owl.cs.manchester.ac.uk/co-ode-files/ontologies/pizza.owl
        // and put it
        // somewhere on your hard drive Create a file object that points to the
        // local copy
        File file = new File(pathtoontology);
        
        // Now load the local copy
        OWLOntology localOntology = manager.loadOntologyFromOntologyDocument(file);
        System.out.println("Loaded ontology: " + localOntology);
        System.out.println("Docuemnt IRI: " + localOntology.getOntologyID().getDefaultDocumentIRI());
        System.out.println("Ontologoy IRI: " + localOntology.getOntologyID().getOntologyIRI());
        
        // We can always obtain the location where an ontology was loaded from
        IRI documentIRI = manager.getOntologyDocumentIRI(localOntology);
        System.out.println(" from: " + documentIRI);
        
        // Remove the ontology again so we can reload it later
        //manager.removeOntology(pizzaOntology);
        
        return localOntology;
    }

    /**
    * This example shows how an ontology can be saved in various formats to
    * various locations and streams.
    *
    * @throws OWLOntologyStorageException
    * @throws OWLOntologyCreationException
    * @throws IOException
    */
    public static void SaveOntology(OWLOntology ontology, String path) throws OWLOntologyStorageException, OWLOntologyCreationException, IOException {
        // Get hold of an ontology manager
        OWLOntologyManager manager = OntologyManager.getManager();

        // Now save a local copy of the ontology. (Specify a path appropriate to your setup)
        
        File file = new File(path + "saved_ontology.owl");
        manager.saveOntology(ontology, IRI.create(file.toURI()));
        // By default ontologies are saved in the format from which they were
        // loaded. In this case the ontology was loaded from an rdf/xml file We
        // can get information about the format of an ontology from its manager
        OWLOntologyFormat format = manager.getOntologyFormat(ontology);
        System.out.println("Format: " + format);
        
        /*
        // We can save the ontology in a different format Lets save the ontology
        // in owl/xml format
        OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
        // Some ontology formats support prefix names and prefix IRIs. In our
        // case we loaded the pizza ontology from an rdf/xml format, which
        // supports prefixes. When we save the ontology in the new format we
        // will copy the prefixes over so that we have nicely abbreviated IRIs
        // in the new ontology document
        if (format.isPrefixOWLOntologyFormat()) {
            owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
        }
        manager.saveOntology(pizzaOntology, owlxmlFormat,
                IRI.create(file.toURI()));
        // We can also dump an ontology to System.out by specifying a different
        // OWLOntologyOutputTarget Note that we can write an ontology to a
        // stream in a similar way using the StreamOutputTarget class
        @SuppressWarnings("unused")
        OWLOntologyDocumentTarget documentTarget = new SystemOutDocumentTarget();
        // Try another format - The Manchester OWL Syntax
        ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
        if (format.isPrefixOWLOntologyFormat()) {
            manSyntaxFormat
                    .copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
        }
        manager.saveOntology(pizzaOntology, manSyntaxFormat,
                new StreamDocumentTarget(new ByteArrayOutputStream()));
        file.delete();
        */
    }
    
    
    public static void getClass(OWLOntology ontology){
    	OWLOntologyManager manager = OntologyManager.getManager();
   	 
    	IRI iri = IRI.create("http://www.semanticweb.org/mhaslauer/ontologies/2013/11/zombiesim#Choice");
        OWLClass thing = manager.getOWLDataFactory().getOWLClass(iri);
        Set<OWLAnnotation> test = thing.getAnnotations(ontology);
        System.out.println("Thing: " + thing);
        
        Set<OWLClassExpression> set = thing.getSuperClasses(ontology);
        System.out.println("Superclasses: " + set.toString());
    	
    }
    
    public static void printClassHierarchy(OWLOntology ontology){
    	
    	OWLOntologyManager manager = OntologyManager.getManager();
    	 
        OWLClass thing = manager.getOWLDataFactory().getOWLThing();
        System.out.println("Thing: " + thing);
        
        if(thing.getSubClasses(ontology).isEmpty()){
        	System.out.println("Nothing under Thing");
        	return;
        }
        
       Set<OWLClassExpression> subclass = thing.getSubClasses(ontology);
       
       for (OWLClassExpression a : subclass){
    	   printSubclasses(ontology, a);
       }
    
    }
    
    public static void printSubclasses(OWLOntology ontology, OWLClassExpression clazz){
    	System.out.println(clazz.asOWLClass());
    	//System.out.println(clazz.asOWLClass().getIndividuals(ontology));
    	Set<OWLClassExpression> subclasses = clazz.asOWLClass().getSubClasses(ontology);
    	for(OWLClassExpression a : subclasses){
    		printSubclasses(ontology, a);
    	}
    	
    }
    
    public static void Reasoning(OWLOntology ontology){
    	OWLOntologyManager manager = OntologyManager.getManager();
    	
    	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
    	//ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
    	//OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
    	//OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, config);
    	OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
    	
    	APITest.getClass(ontology);
    	
    	
    	reasoner.precomputeInferences();
        // We can determine if the ontology is actually consistent (in this
        // case, it should be).
        boolean consistent = reasoner.isConsistent();
        System.out.println("Consistent: " + consistent);
        System.out.println("\n");
        
        OWLDataFactory fac = manager.getOWLDataFactory();
        OWLClass choice = fac.getOWLClass(IRI.create("http://www.semanticweb.org/mhaslauer/ontologies/2013/11/zombiesim#MyChoice"));
        
        NodeSet<OWLClass> subClses = reasoner.getSuperClasses(choice, true);
       
       Set<OWLClass> clses = subClses.getFlattened();
       System.out.println("Superclasses of choice: ");
       for (OWLClass cls : clses) {
           System.out.println(" " + cls);
       }
       System.out.println("\n");
    }
    
    public static void addClass(OWLOntology ontology){
    	OWLOntologyManager manager = OntologyManager.getManager();
    	
    	Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
    	OWLDataFactory fac = manager.getOWLDataFactory();
    	
    	IRI iri = IRI.create("http://www.semanticweb.org/mhaslauer/ontologies/2013/11/zombiesim#SimulationScenario");
        OWLClass thing = manager.getOWLDataFactory().getOWLClass(iri);
        OWLObjectProperty prop = fac.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/mhaslauer/ontologies/2013/11/zombiesim#hasParameter"));
    	OWLClassExpression expr1 = fac.getOWLClass(IRI.create("http://www.semanticweb.org/mhaslauer/ontologies/2013/11/zombiesim#ManyZombies"));
    	OWLClassExpression expr2 = fac.getOWLClass(IRI.create("http://www.semanticweb.org/mhaslauer/ontologies/2013/11/zombiesim#LessHumans"));
    	OWLClassExpression expr3 = fac.getOWLClass(IRI.create("http://www.semanticweb.org/mhaslauer/ontologies/2013/11/zombiesim#SmallWorld"));
    	
    	
    	OWLClass newclass = fac.getOWLClass(IRI.create("http://www.semanticweb.org/mhaslauer/ontologies/2013/11/zombiesim#MyChoice"));
    	axioms.add(fac.getOWLSubClassOfAxiom(newclass, thing));
    	
    	OWLClassExpression add1 = fac.getOWLObjectSomeValuesFrom(prop, expr1);
    	OWLClassExpression add2 = fac.getOWLObjectSomeValuesFrom(prop, expr2);
    	OWLClassExpression add3 = fac.getOWLObjectSomeValuesFrom(prop, expr3);
    	
    	OWLClassExpression add4 = fac.getOWLObjectIntersectionOf(add1, add2, add3);
    	
    
    
    	
    	axioms.add(fac.getOWLSubClassOfAxiom(newclass, add4));
    	
    	manager.addAxioms(ontology, axioms);    	
    	
    }
    

}
