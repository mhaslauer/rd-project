package rd1302.analyzer;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import rd1302.ontology.OntologyManager;


/**
 * This class holds all functions to analyze and manipulate an ontology
 * @author mhaslauer
 *
 */
public class OntologyAnalyzer {

	public static void init(String path){
		try {
			OntologyManager.loadOntology(path);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to export the whole class hierarchie of an ontology
	 * @return JSON String with class hierarchie
	 */
	public static String exportClassHierarchie(){
    	OWLOntology ontology = OntologyManager.getOntology();
    	
    	StringBuilder sb = new StringBuilder();
    	 
        OWLClass thing = OntologyManager.getManager().getOWLDataFactory().getOWLThing();
        
        sb.append("{");
        
        if(thing.getSubClasses(ontology).isEmpty()){
        	System.out.println("Nothing under Thing");
        	sb.append("\"name\":\"thing\"");
        	sb.append('}');
        	return sb.toString();
        }
    	   
        exportSubclasses(ontology, thing, sb);
        sb.append("}");
       
        return sb.toString();
    
    }
	
	private static void exportSubclasses(OWLOntology ontology, OWLClassExpression clazz, StringBuilder sb){
    	//System.out.println(clazz.asOWLClass());
	 	String[] name = clazz.toString().split("#");
    	sb.append("\"name\":\""+name[name.length-1]+"\", \"subclasses\":[");
    	Set<OWLClassExpression> subclasses = clazz.asOWLClass().getSubClasses(ontology);
    	
    	for(OWLClassExpression a : subclasses){
    		sb.append("{");
    		exportSubclasses(ontology, a, sb);
    		sb.append("},");
    	}
    	sb.append("]");
	}
	
	/**
	 * evaluates the chosen parameter of a user, add them as a new class and performs reasoning to evaluate the superclass
	 * @param params
	 * @return String array with names of superclasses
	 */
	public static String[] evaluateParameter(Set<String> params){
		OWLOntology ontology = OntologyManager.getOntology();
		OWLOntologyManager manager = OntologyManager.getManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
    	Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
    	Set<OWLClassExpression> parameterExpressions = new HashSet<OWLClassExpression>();
    	Set<OWLClassExpression> someValueExpressions = new HashSet<OWLClassExpression>();
    	
    	//Get the mandatory hasParameter property
    	OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(OntologyManager.getOntologyIRI()+"#hasParameter"));
    	//Get the mandatory SimulationScenario class
    	OWLClass scenarioClass = factory.getOWLClass(IRI.create(OntologyManager.getOntologyIRI()+"#SimulationScenario"));
    	//Get a new class for evaluation
    	OWLClass newclass = factory.getOWLClass(IRI.create(OntologyManager.getOntologyIRI()+"#UserChoice"));
    	//Add new class as subclass to the SimulationScenario class
    	newAxioms.add(factory.getOWLSubClassOfAxiom(newclass, scenarioClass));
    	
    	//Get all classes of chosen parameters
    	for(String param : params){
    		parameterExpressions.add(factory.getOWLClass(IRI.create(OntologyManager.getOntologyIRI()+"#"+param)));
    	}
    	
    	//Add hasParameter axion with "some"
    	for (OWLClassExpression expr : parameterExpressions){
    		someValueExpressions.add(factory.getOWLObjectSomeValuesFrom(property, expr));
    	}
    	
    	//create an intersection of the previously created axioms (AND)
    	OWLClassExpression intersection = factory.getOWLObjectIntersectionOf(someValueExpressions);
    	
    	//Add a axiom for the new class as subclass of the generated expression
    	newAxioms.add(factory.getOWLSubClassOfAxiom(newclass, intersection));
    	
    	manager.addAxioms(ontology, newAxioms);  
		
    	//-------------------------
    	// Now reasoning
    	
    	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
    	OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
    	
    	reasoner.precomputeInferences();
        
        Set<OWLClass> superclasses = reasoner.getSuperClasses(newclass, true).getFlattened();
       
        String[] temp = new String[superclasses.size()];
        int i = 0;
        
        for(OWLClass cls : superclasses){
        	temp[i] = cls.toString().split("#")[1].substring(0, cls.toString().split("#")[1].length()-1);
        	i++;
        }
		
        return temp;
		
	}
	
	
}
