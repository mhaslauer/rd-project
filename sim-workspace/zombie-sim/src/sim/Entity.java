package sim;

public class Entity {
	public static int movement;
	protected int movement_left;
	
	public Entity(){
		
	}
	
	public void update(World world, Field field) {
		
	}
	
	public void resetMovement(){
		movement_left = movement;
	}
}
