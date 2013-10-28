package sim;

import java.util.ArrayList;
import java.util.Random;

public class Human extends Entity {

	
	public Human(){
		super();
	}
	
	@Override
	public void update(World world, Field field) {		
		while(movement_left > 0){
			ArrayList<Field> neigborhood = getNeighborhood(world, field);
			Random rand = new Random();
			
			if(neigborhood.size() == 0){
				// wua wua wuaaaah
				//All neigborhood fields contain zombies,
				// you are pretty much fucked
				return;
			}
			Field target = neigborhood.get(rand.nextInt(neigborhood.size()));
			
			field.getEntitiesToRemove().add(this);
			target.addEntity(this);
			movement_left--;
		}
	}
	
	private ArrayList<Field> getNeighborhood(World world, Field field){
		ArrayList<Field> ok_fields = new ArrayList<Field>();
		
		for(int i = -1;i<=1;i++){
			for (int j = -1;j<=1;j++){
				if(i==0 && j==0){
					continue;
				}
				Field temp = world.getField(field.getX()+i, field.getY()+j);
				if(temp == null){
					continue;
				}
				if(temp.containsZombie() == false){
					ok_fields.add(temp);
				}
			}
		}
		
		return ok_fields;
	}
}
