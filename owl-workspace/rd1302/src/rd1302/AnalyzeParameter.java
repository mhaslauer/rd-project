package rd1302;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rd1302.analyzer.OntologyAnalyzer;

/**
 * Servlet implementation class AnalyizeParameter
 */
@WebServlet("/analyzeparameter")
public class AnalyzeParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AnalyzeParameter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Get");
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
				
				ArrayList<String> parameter = new ArrayList<String>();
				
				JSONArray params = (JSONArray) jsonObject.get("parameter");
				Iterator<String> iterator = params.iterator();
				while (iterator.hasNext()) {
					parameter.add(iterator.next());
				}
				
				//OntologyAnalyzer.init(null);
				String[] szenarios = OntologyAnalyzer.evaluateParameter(parameter);
				
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				
				out.write("{ \"szenarios\":[");
				
				for(String szenario : szenarios){
					out.write("\""+ szenario + "\",");
				}
				out.write("]}");
			}
			
		} catch (ParseException ex) {
			  ex.printStackTrace();
		}	 
	}

}
