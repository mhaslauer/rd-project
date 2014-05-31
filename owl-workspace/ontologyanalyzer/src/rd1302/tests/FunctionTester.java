package rd1302.tests;


import rd1302.analyzer.OntologyAnalyzer;
import rd1302.simulationcaller.SimulationCaller;

public class FunctionTester {
	
	private static final String ontology_file = "/Users/mhaslauer/git/rd-project/owl-workspace/ontologyanalyzer/zombiesim.owl";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		OntologyAnalyzer.init(null);
		/*
		for (String bla : OntologyAnalyzer.evaluateParameter(params)){
			System.out.println(bla);
		}
		System.out.println(OntologyAnalyzer.exportClassHierarchie());
		*/
		String temp = OntologyAnalyzer.getScenarioParameterValues("BestCase");
		System.out.println(SimulationCaller.callSimulation(temp));
		
		System.out.println(OntologyAnalyzer.evaluateSimulationResult(Integer.parseInt(SimulationCaller.callSimulation(temp))));
	}

}
