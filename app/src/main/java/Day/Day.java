package Day;

import com.presence.bar.tamir.firebaseuitest.User;

import java.util.ArrayList;

public class Day {

    String date;
    private ArrayList<User> users;

    public Day(){
        users = new ArrayList<>();
    }

    public void add(User newUser){
        users.add(newUser);
    }
    public void setDate(String date){
        this.date = date;
    }

    public ArrayList<User> getArrayUsers(){
        return users;
    }

    public String getDate(){
        return date;
    }
}
