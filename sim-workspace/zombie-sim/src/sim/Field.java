package sim;

import java.util.ArrayList;

public class Field {
	private int x;
	private int y;
	private ArrayList<Entity> entities;
	private ArrayList<Entity> entitiesToRemove;
	private int infectedHumans = 0;
	
	public Field(int x, int y){
		this.x = x;
		this.y = y;
		entities = new ArrayList<Entity>();
		entitiesToRemove = new ArrayList<Entity>();
	}

	public boolean isEmpty() {
		return entities.size() > 0;
	}

	public void addEntity(Entity entity) {
		entities.add(entity);
		
	}
	
	public void removeEntity(Entity entity){
		entities.remove(entity);
	}

	public boolean containsHuman() {
		for(Entity entity : entities){
			if(entity instanceof Human){
				return true;
			}
		}
		return false;
	}
	
	public boolean containsZombie() {
		for(Entity entity : entities){
			if(entity instanceof Zombie){
				return true;
			}
		}
		return false;
	}

	public void update(World world) {
		for(Entity entity : entities){
			entity.update(world, this);
		}
		apply();
	}
	
	public void apply() {
		entities.removeAll(entitiesToRemove);
		entitiesToRemove.clear();

		for(int i = 0; i < infectedHumans; i++){
			entities.add(new Zombie());
		}
		infectedHumans = 0;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void infectHumans(){
		for (Entity entity : entities) {
			if(entity instanceof Human && entitiesToRemove.contains(entity) == false){
				infectedHumans++;
				entitiesToRemove.add(entity);
			}
		}
	}
	
	public void resetMovement(){
		for (Entity entity : entities) {
			entity.resetMovement();
		}
	}
	
	public int getHumanCount(){
		int count = 0;
		for (Entity entity : entities) {
			if(entity instanceof Human){
				count++;
			}
		}
		return count;
	}
	
	public int getZombieCount(){
		int count = 0;
		for (Entity entity : entities) {
			if(entity instanceof Zombie){
				count++;
			}
		}
		return count;
	}

	public ArrayList<Entity> getEntitiesToRemove() {
		return entitiesToRemove;
	}
}
