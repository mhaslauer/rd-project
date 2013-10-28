package sim;

import java.util.ArrayList;
import java.util.Random;

public class Zombie extends Entity {

	public Zombie(){
		super();
	}
	
	@Override
	public void update(World world, Field field) {
		while(movement_left > 0){
			ArrayList<Field> neigborhood = getNeighborhood(world, field);
			Random rand = new Random();
			
			Field target = neigborhood.get(rand.nextInt(neigborhood.size()));
			
			field.getEntitiesToRemove().add(this);
			target.addEntity(this);
			
			target.infectHumans();
			target.apply();
			movement_left--;
		}
	}
	
	private ArrayList<Field> getNeighborhood(World world, Field field){
		ArrayList<Field> ok_fields = new ArrayList<Field>();
		ArrayList<Field> human_fields = new ArrayList<Field>();
		
		for(int i = -1;i<=1;i++){
			for (int j = -1;j<=1;j++){
				if(i==0 && j==0){
					continue;
				}
				Field temp = world.getField(field.getX()+i, field.getY()+j);
				if(temp == null){
					continue;
				}
				if(temp.containsHuman() == true){
					human_fields.add(temp);
				}
				ok_fields.add(temp);
			}
		}
		
		return human_fields.size() > 0 ? human_fields : ok_fields;
	}
}
