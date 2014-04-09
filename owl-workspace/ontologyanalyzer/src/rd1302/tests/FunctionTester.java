package rd1302.tests;

import java.util.HashSet;
import java.util.Set;

import rd1302.analyzer.OntologyAnalyzer;

public class FunctionTester {
	
	private static final String ontology_file = "/Users/mhaslauer/git/rd-project/owl-workspace/ontologyanalyzer/zombiesim.owl";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Set<String> params = new HashSet<String>();
		
		params.add("ManyZombies");
    	params.add("LessHumans");
    	params.add("SmallWorld");
		
		OntologyAnalyzer.init(ontology_file);
		for (String bla : OntologyAnalyzer.evaluateParameter(params)){
			System.out.println(bla);
		}
	}

}
