package rd1302.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
	
	public static String used_ontology;
	public static Properties properties = new Properties();

	public static void init(String path){
		if (path == null){
			try {
				properties.load(OntologyAnalyzer.class.getClassLoader().getResourceAsStream("properties"));
				used_ontology = properties.getProperty("ontology.file");
				System.out.println(used_ontology);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else{
			used_ontology = path;
		}
		
		try {
			
			OntologyManager.loadOntology(used_ontology);
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
		if(clazz.toString().compareToIgnoreCase("owl:thing") != 0){
			String[] name = clazz.toString().split("#");
			sb.append("\"name\":\""+name[name.length-1].substring(0, name[name.length-1].length()-1)+"\", \"subclasses\":[");
		}
		else{
			sb.append("\"name\":\""+clazz.toString()+"\", \"subclasses\":[");
		}
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
	public static String[] evaluateParameter(ArrayList<String> params){
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
        
        
        //Revoke temporary changes in the ontology and reload original ontology
        OntologyManager.unloadOntology();
        try {
			OntologyManager.loadOntology(used_ontology);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return temp;
		
	}
	
	/**
	 * Get the Annotation parameter of the scenario class that contains tha values for the simulation
	 * @param scenarioname
	 * @return String value of annotation
	 */
	public static String getScenarioParameterValues(String scenarioname){
		OWLOntology ontology = OntologyManager.getOntology();
		OWLOntologyManager manager = OntologyManager.getManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		String value = null;
		
		//Get the scenario class by name
		IRI iri = IRI.create(OntologyManager.getOntologyIRI()+"#"+scenarioname);
		OWLClass scenario = factory.getOWLClass(iri);
		
		//Get the Annotation paramter by name
		IRI iri_param = IRI.create(OntologyManager.getOntologyIRI()+"#parameter");
		OWLAnnotationProperty param = factory.getOWLAnnotationProperty(iri_param);
		
		//Get all parameter annotations of scenario (should be only one)
		Set<OWLAnnotation> annotations = scenario.getAnnotations(ontology, param);
		
		//Get value
		for (OWLAnnotation annotation :annotations) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                   value = val.getLiteral();
            }
        }
		return value;
	}
	
	public static String evaluateSimulationResult(double value){
		OWLOntology ontology = OntologyManager.getOntology();
		OWLOntologyManager manager = OntologyManager.getManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
    	Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
    	
    	//Get the mandatory SimulationTime data property
    	OWLDataProperty dataproperty = factory.getOWLDataProperty(IRI.create(OntologyManager.getOntologyIRI()+"#SimulationTime"));
    			
    	//Get the mandatory SimulationResult class
    	OWLClass ResultClass = factory.getOWLClass(IRI.create(OntologyManager.getOntologyIRI()+"#SimulationResult"));
    	
    	//Get a new individual for evaluation
    	OWLNamedIndividual individual = factory.getOWLNamedIndividual(IRI.create(OntologyManager.getOntologyIRI()+"#UserResult"));
    	newAxioms.add(factory.getOWLDeclarationAxiom(individual));
    	
    	//Add new class as subclass to the SimulationScenario class
    	newAxioms.add(factory.getOWLClassAssertionAxiom(ResultClass, individual));
    	
    	//Add Dataproperty to Individual
    	newAxioms.add(factory.getOWLDataPropertyAssertionAxiom(dataproperty, individual, value));
    	
    	
    	
    	manager.addAxioms(ontology, newAxioms);  
    	
    	//-------------------------
    	// Now reasoning
    	
    	String temp = null;
    	
    	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
    	OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
    	
    	reasoner.precomputeInferences();
    	
    	Set<OWLClass> types = reasoner.getTypes(individual, true).getFlattened();
       
        for(OWLClass cls : types){
        	if(cls.toString().compareToIgnoreCase("<"+OntologyManager.getOntologyIRI()+"#SimulationResult>") != 0){
        		temp = cls.toString().split("#")[1].substring(0, cls.toString().split("#")[1].length()-1);
        	}
        }
        
        
        //Revoke temporary changes in the ontology and reload original ontology
        OntologyManager.unloadOntology();
        try {
			OntologyManager.loadOntology(used_ontology);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return temp;
	}
	
	
}
