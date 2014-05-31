package sim;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Simulation {
	
	public static final boolean DEBUG = false;
	static World world;
	
	public static int Zombies = 0;
	public static int Humans = 0;
	public static int WorldSize = 0;

	public static void main(String[] args) throws Exception {
		
		//String file = "{\"worldsize\": 10,\"zombiecount\":12,\"humancount\": 11}";
		
		//parseJSON(file);
		parseJSON(args[0]);
		
		if(Simulation.DEBUG) System.out.println("DEBUG: Parameter Humans:" + Humans);
		if(Simulation.DEBUG) System.out.println("DEBUG: Parameter Zombies:" + Zombies);
		if(Simulation.DEBUG) System.out.println("DEBUG: Parameter World:" + WorldSize);
		
		int count = 0;
		world = new World(WorldSize, Humans, Zombies, 1, 1);
		if(Simulation.DEBUG) System.out.println("World populated");

		if(Simulation.DEBUG) updateUI();
		while(world.HumansLeft()){
			if(Simulation.DEBUG) System.out.println("Zombies: " + world.getZombieCount());
			if(Simulation.DEBUG) System.out.println("Humans: " + world.getHumanCount());
			if(Simulation.DEBUG) System.out.println("Step: " + count);
			if(Simulation.DEBUG) System.out.println();
			if(Simulation.DEBUG) System.out.flush();
			world.resetMovement();
			world.update();
			if(Simulation.DEBUG) updateUI();
			count++;
		}
		if(Simulation.DEBUG) System.out.println("==== THE END ====");
		if(Simulation.DEBUG) updateUI();
		if(Simulation.DEBUG) System.out.println("Humans survived " + count + " Steps");
		
		//Main Output
		System.out.println(count);
		
	}
	
	private static void parseJSON(String file){
		
		if(Simulation.DEBUG) System.out.println("DEBUG: Parsing File: " + file);
		
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(file);
				 
			Long worldsize = (Long)jsonObject.get("worldsize");
			WorldSize = worldsize.intValue();
			Long humans  = (Long)jsonObject.get("humancount");
			Humans = humans.intValue();
			Long zombies = (Long)jsonObject.get("zombiecount");
			Zombies = zombies.intValue();
			
			if(Simulation.DEBUG) System.out.println("DEBUG: Parsed WorldSize: " + WorldSize);
			if(Simulation.DEBUG) System.out.println("DEBUG: Parsed Humans: " + Humans);	
			if(Simulation.DEBUG) System.out.println("DEBUG: Parsed Zombies: " + Zombies);	
			
		} catch (ParseException ex) {
			  ex.printStackTrace();
		}	 
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
