package Course_pac;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class Courses {

    ArrayList<Map<String, Object>> courses_names_list;

    public Courses(){
        courses_names_list = new ArrayList<>();
    }

    public ArrayList<Map<String, Object>> updateCourses(DocumentReference id){

        id.collection("active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            courses_names_list.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                courses_names_list.add(document.getData());
                            }

                        } else {
                            Log.w("stam", "Error getting documents.", task.getException());
                        }
                    }
                });
        return courses_names_list;
    }

    public Map<String, Object> getCourse(String key){
        for(Map<String, Object> course : courses_names_list){
            if(course.get("name").equals(key)){
                return courses_names_list.get(courses_names_list.indexOf(course));
            }
        }
        return null;
    }

    public void add(Map<String, Object> c){

        courses_names_list.add(c);
    }


    public void writeUserToDB(DocumentReference id){
        //db.child(id).setValue(userInfo);
//        Date currentTime = Calendar.getInstance().getTime();

        for(Map<String, Object> course : courses_names_list){
            id.collection("active").document(course.get("cid").toString()).set(course);

        }


    }


}
