package sim;

public class Simulation {
	
	public static final boolean DEBUG = true;

	static World world;

	public static void main(String[] args) throws Exception {
		int count = 0;
		world = new World(10, 10, 1, 1, 1);
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
