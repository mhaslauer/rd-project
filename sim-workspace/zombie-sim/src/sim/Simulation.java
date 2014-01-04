package sim;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Simulation {
	
	public static final boolean DEBUG = true;
	
	private static ArrayList<Parameter> ParamZombie = new ArrayList<Parameter>();
	private static ArrayList<Parameter> ParamHuman = new ArrayList<Parameter>();
	private static ArrayList<Parameter> ParamWorld = new ArrayList<Parameter>();


	
	static World world;

	public static void main(String[] args) throws Exception {
		int Zombies = 0;
		int Humans = 0;
		int WorldSize = 0;
		
		parseXML(args[0]);
		//parseXML("/Users/mhaslauer/xFH/Dropbox/RD13-02/Protege/ontologies/zombiesim.owl");
		//modifyXML("/Users/mhaslauer/xFH/Dropbox/RD13-02/Protege/ontologies/zombiesim.owl", 10);
		
		
		Humans = ChooseParameter(ParamHuman, "Number of Humans");
		Zombies = ChooseParameter(ParamZombie, "Number of Zombies");
		WorldSize = ChooseParameter(ParamWorld, "Size of the World");
		
		if(Simulation.DEBUG) System.out.println("DEBUG: Parameter Humans:" + Humans);
		if(Simulation.DEBUG) System.out.println("DEBUG: Parameter Zombies:" + Zombies);
		if(Simulation.DEBUG) System.out.println("DEBUG: Parameter World:" + WorldSize);
		
		int count = 0;
		world = new World(WorldSize, Humans, Zombies, 1, 1);
		System.out.println("World populated");

		updateUI();
		while(world.HumansLeft()){
			System.out.println("Zombies: " + world.getZombieCount());
			System.out.println("Humans: " + world.getHumanCount());
			System.out.println("Step: " + count);
			System.out.println();
			System.out.flush();
			world.resetMovement();
			world.update();
			updateUI();
			count++;
		}
		System.out.println("==== THE END ====");
		updateUI();
		System.out.println("Humans survived " + count + " Steps");
		

		modifyXML(args[0], count);
		
	}
	
	private static void modifyXML(String file, int Steps){
		
		if(Simulation.DEBUG) System.out.println("DEBUG: Modifying File: " + file);
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			
			Element root = doc.getDocumentElement();
			String prefix = root.getAttribute("xmlns:zombiesim");
			String xsd = root.getAttribute("xmlns:xsd");
			
			Random rand = new Random();
			int ID = 1000 + rand.nextInt(9000);
			
			Element newIndividual = doc.createElement("owl:NamedIndividual");
			newIndividual.setAttribute("rdf:about", prefix+"GeneratedResult"+ID);
			
			Element newType = doc.createElement("rdf:type");
			newType.setAttribute("rdf:resource", prefix+"SimulationResult");
			
			Element newValue = doc.createElement("SimulationTime");
			newValue.setAttribute("rdf:datatype", xsd+"integer");
			newValue.appendChild(doc.createTextNode(Integer.toString(Steps)));
			
			newIndividual.appendChild(newType);
			newIndividual.appendChild(newValue);
			
			root.appendChild(newIndividual);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			String filename = file.split("\\.(?=[^\\.]+$)")[0] + "_modified." + file.split("\\.(?=[^\\.]+$)")[1];
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);
			
			
			
			if(Simulation.DEBUG) System.out.println("DEBUG: Done. Wrote file to: " + filename);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void parseXML(String file){
		
		if(Simulation.DEBUG) System.out.println("DEBUG: Parsing File: " + file);
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			
			if(Simulation.DEBUG) System.out.println("DEBUG: Root Element: " + doc.getDocumentElement().getNodeName());
		
			NodeList nodes = doc.getElementsByTagName("owl:NamedIndividual");
			
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					
					if(Simulation.DEBUG) System.out.println("DEBUG: rdf:abount " + element.getAttribute("rdf:about").split("#")[1]);
					if(Simulation.DEBUG) System.out.println("DEBUG: Attribute rdf:resource " + getXMLAttribute("rdf:type", "rdf:resource", element).split("#")[1]);
					
					if(getXMLAttribute("rdf:type", "rdf:resource", element).split("#")[1].equalsIgnoreCase("WorldSize")){
						ParamWorld.add(new Parameter(element.getAttribute("rdf:about").split("#")[1], Integer.parseInt(getXMLValue("Size", element))));
					}
					else if(getXMLAttribute("rdf:type", "rdf:resource", element).split("#")[1].equalsIgnoreCase("Human")){
						ParamHuman.add(new Parameter(element.getAttribute("rdf:about").split("#")[1], Integer.parseInt(getXMLValue("HumanCount", element))));
					}
					else if(getXMLAttribute("rdf:type", "rdf:resource", element).split("#")[1].equalsIgnoreCase("Zombie")){
						ParamZombie.add(new Parameter(element.getAttribute("rdf:about").split("#")[1], Integer.parseInt(getXMLValue("ZombieCount", element))));
					}
				}


			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			 
	}
	
	private static String getXMLValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = nodes.item(0);
		 
		return node.getNodeValue();
	}
	 
	private static String getXMLAttribute(String tag, String attribute, Element element) {
		NodeList nodes = element.getElementsByTagName(tag);
		
		Node node = nodes.item(0);
		
		Element temp = (Element) node;
		
		return temp.getAttribute(attribute);
	}
	
	private static int ChooseParameter(ArrayList<Parameter> params, String DisplayName){
		int choice = -1;
		Scanner in = new Scanner(System.in);
		
		System.out.println("The following Options are available for the Parameter: " + DisplayName);
		
		for(int i=0;i<params.size();i++){
			System.out.println(i + " : " + params.get(i).getName());
		}
		
		do{
			System.out.println("Please insert the desired Number");
		
			choice = in.nextInt();
		
		}while(choice < 0 || choice > params.size()-1);
		System.out.println();
		return params.get(choice).getValue();
	}
	
	private static void updateUI(){
		for(int x = 0; x < world.fields.length; x++){
			for(int y = 0; y < world.fields.length; y++){
				int h = world.fields[x][y].getHumanCount();
				int z = world.fields[x][y].getZombieCount();
				String s = h + "" + z + " ";
				System.out.print(s);
			}
			System.out.print("\n");
		}
	}

}
