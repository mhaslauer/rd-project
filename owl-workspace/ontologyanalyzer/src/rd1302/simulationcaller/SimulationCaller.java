/**
 * 
 */
package rd1302.simulationcaller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author mhaslauer
 *
 */
public class SimulationCaller {
	
	private static Properties properties = new Properties();
	
	public static String callSimulation(String parameter){
		try {
			properties.load(new FileInputStream("properties"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String path = properties.getProperty("simulation.jar");
		
		Process ps;
		try {
			ps = Runtime.getRuntime().exec(new String[]{"java","-jar",path, parameter});
		
		    ps.waitFor(); 
		    
		    InputStream is;
		    
		    if(ps.getErrorStream().available() == 0){
		    	is = ps.getInputStream();
		    }
		    else{
		    	is = ps.getErrorStream();
		    }	
		    
		    byte b[]=new byte[is.available()];
		     
		    is.read(b,0,b.length);
		    
		    String value = new String(b);
		    
		    return value.trim();
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
