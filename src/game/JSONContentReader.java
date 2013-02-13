/*
package game;

import java.util.*;
import java.io.File;

import org.json.simple.*;
import org.json.simple.parser.*;

public class JSONContentReader {
	public String		content;
	private JSONParser	parser;
	private JSONArray	arr;
	private Map			json;

	ContainerFactory containerFactory = new ContainerFactory() {
	    public List creatArrayContainer()
	    	{ return new LinkedList(); }
	    public Map createObjectContainer()
	    	{ return new LinkedHashMap(); }
	};
	
	public JSONContentReader(String file) {
		content = "{\"error\":\"FileNotFound\"}";

		try{
			content = new Scanner(new File(file), "UTF-8").useDelimiter("\\A").next();
			parser = new JSONParser();
			json = (Map)parser.parse(content, containerFactory);

			Iterator iter = json.entrySet().iterator();
			System.out.println("==iterate result==");
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry)iter.next();
				System.out.println(entry.getKey() + "=>" + entry.getValue());
			}
		                        
			System.out.println("==toJSONString()==");
		} catch(ParseException e){
			System.err.println(e);
			System.err.println("position: " + e.getPosition());
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public Map getValueById(String id) {
		
		return json;
	}
	public Object getValueById(int id) 
		{ return getValueById("" + id); }
}
*/
