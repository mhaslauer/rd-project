package rd1302;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rd1302.analyzer.OntologyAnalyzer;
import rd1302.simulationcaller.SimulationCaller;

/**
 * Servlet implementation class EvaluateSimulation
 */
@WebServlet("/EvaluateSimulation")
public class EvaluateSimulation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EvaluateSimulation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			StringBuffer jb = new StringBuffer();
			String line = null;
			    
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null){
				jb.append(line);
			}
			
			if(jb.toString().isEmpty() == false){
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(jb.toString());
				
				String szenario = (String)jsonObject.get("szenario");
				
				String simuParams = OntologyAnalyzer.getScenarioParameterValues(szenario);
				String result = SimulationCaller.callSimulation(simuParams);
				String simuResult = OntologyAnalyzer.evaluateSimulationResult(Double.parseDouble(result));
				
				PrintWriter out = response.getWriter();
				response.setContentType("application/json");
				out.write("{ \"simulationresult\":\"" + simuResult + "\"}");
			}
		} catch (Exception e){
			
		}
	}

}
