package Course_pac;

import java.io.Serializable;
import java.util.HashMap;

public class Course{

    HashMap<String , String> course_map;
    public Course(){
        course_map = new HashMap<>();
    }

    public String getValue(String key){
        if(course_map.containsKey(key)){
            return course_map.get(key);
        }
        return null;
    }

    public void set(String key , String value){
        course_map.put(key , value);
    }

    public void updateMap(Serializable newMap){
        course_map.putAll((HashMap<String , String>)newMap);
        //userInfo = new HashMap<>((HashMap<String , String>)newMap);
    }

    public HashMap<String, String> getCourse_map() {
        return course_map;
    }
}
