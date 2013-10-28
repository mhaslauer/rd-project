package sim;

import java.util.Random;

public class World {
	
	public Field[][] fields;
	private int size;
	
	public World(int size, int humans, int zombies, int human_movement, int zombie_movement) throws Exception{
		if(size*size < humans + zombies){
			throw new Exception("to much Objects for World Size");
		}
		
		Human.movement = human_movement;
		Zombie.movement = zombie_movement;
		this.size = size;
		fields = new Field[size][size];
		
		for(int i = 0; i<size;i++){
			for (int j = 0;j<size;j++){
				fields[i][j] = new Field(i, j);
			}
		}
		
		if(Simulation.DEBUG) System.out.println("DEBUG: Fields created"); 
		
		Random rand = new Random();
		
		//create Humans
		for(int i = 0;i<humans;i++){
			
			int x = rand.nextInt(size);
			int y = rand.nextInt(size);
			if (fields[x][y].containsZombie() == false){
				fields[x][y].addEntity(new Human());
			}
			else{
				i--;
			}
		}
		if(Simulation.DEBUG) System.out.println("DEBUG: Humans created"); 
		
		//create Zombies
		for(int i = 0;i<zombies;i++){
			
			int x = rand.nextInt(size);
			int y = rand.nextInt(size);
			if (fields[x][y].containsHuman() == false){
				fields[x][y].addEntity(new Zombie());
			}
			else{
				i--;
			}
		}
		if(Simulation.DEBUG) System.out.println("DEBUG: Zombies created"); 
	}
	
	public boolean HumansLeft(){
		for(int i = 0; i<size;i++){
			for (int j = 0;j<size;j++){
				if (fields[i][j].containsHuman()){
					return true;
				}
			}
		}
		return false;
	}
	
	public void update(){
		for(int i = 0; i<size;i++){
			for (int j = 0;j<size;j++){
				fields[i][j].update(this);
			}
		}
	}
	
	public void resetMovement(){
		for(int i = 0; i<size;i++){
			for (int j = 0;j<size;j++){
				fields[i][j].resetMovement();
			}
		}
	}
	
	
	public Field getField(int x, int y){
		if(x < 0 || x >= size){
			return null;
		}
		if(y < 0 || y >= size){
			return null;
		}
		return fields[x][y];
	}

	public int getHumanCount(){
		int count = 0;
		for(int i = 0; i<size;i++){
			for (int j = 0;j<size;j++){
				count += fields[i][j].getHumanCount();
			}
		}
		return count;
	}
	
	public int getZombieCount(){
		int count = 0;
		for(int i = 0; i<size;i++){
			for (int j = 0;j<size;j++){
				count += fields[i][j].getZombieCount();
			}
		}
		return count;
	}
	
}
