package Course_pac;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;
import com.presence.bar.tamir.firebaseuitest.MainActivity;
import com.presence.bar.tamir.firebaseuitest.R;
import com.presence.bar.tamir.firebaseuitest.UserInfoActivity;
import com.presence.bar.tamir.firebaseuitest.UsersArrivedActivity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Day.Day;
import Day.DaysListAdapter;
import dialogs.Course_info_dialog;

public class CourseInfo extends AppCompatActivity{

    private static final String DIALOG_DATE = "course_info";
    Toolbar tb;
    FloatingActionButton plus;
    RecyclerView days_recyclerView_list;
    TextView info;
    private String course_name;
    private FirebaseFirestore db;
    private DaysListAdapter daysListAdapter;
    private List<Day> dayList;
    private HashMap<String, String> user;
    private HashMap<String, String> course;
    ArrayList<String> days_names;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        tb = findViewById(R.id.toolbar);
        plus = findViewById(R.id.floatingActionButton2);
        days_recyclerView_list = findViewById(R.id.days_list);
        info = findViewById(R.id.no_data);


        dayList = new ArrayList<>();
        daysListAdapter = new DaysListAdapter(dayList);
        days_recyclerView_list.setLayoutManager(new LinearLayoutManager(this));
        days_recyclerView_list.setAdapter(daysListAdapter);
        build_days_name();

        days_recyclerView_list.addOnItemTouchListener(new RecyclerItemClickListener(this, days_recyclerView_list,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(CourseInfo.this, UsersArrivedActivity.class);
                        String path = db.collection("users").document(user.get("id")).collection("active").document(course.get("cid"))
                                .collection("days").document(dayList.get(position).getDate()).getPath();
                        System.out.println(path);
                        i.putExtra("path", path);
                        i.putExtra("uid" , user.get("id"));
                        i.putExtra("course_name" , course.get("name"));
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CourseInfo.this);
                        String msg = "למחוק את יום " + dayList.get(position).getDate() + "?";
                        dialog.setMessage(msg).setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("users").document(user.get("id")).collection("active").document(course.get("cid")).collection("days")
                                        .document(dayList.get(position).getDate()).delete();
                                dayList.remove(position);
                                if (dayList.isEmpty()) info.setVisibility(View.VISIBLE);
                                daysListAdapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = dialog.create();
                        alert.show();

                    }
                }));

    }

    private void build_days_name() {

        days_names = new ArrayList<>();
        days_names.add("");
        days_names.add("ראשון");
        days_names.add("שני");
        days_names.add("שלישי");
        days_names.add("רביעי");
        days_names.add("חמישי");
        days_names.add("שישי");
        days_names.add("שבת");
    }


        @Override
        protected void onStart () {
            super.onStart();

            Intent data = getIntent();
            user = (HashMap<String, String>) data.getSerializableExtra("user");
            course = (HashMap<String, String>) data.getSerializableExtra("course");

            String userName = user.get("title") + " " + user.get("first name") + " " + user.get("last name");
            tb.setSubtitle(userName);
            course_name = course.get("name");
            tb.setTitle(course_name);

            db = FirebaseFirestore.getInstance();
            //fill the days opened in the list
            db.collection("users").document(user.get("id"))
                    .collection("active").document(course.get("cid"))
                    .collection("days").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    dayList.clear();
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot document : task.getResult()) {
                            info.setVisibility(View.INVISIBLE);
                            Day day = new Day();
                            day.setDate(document.getId());
                            dayList.add(day);
                            daysListAdapter.notifyDataSetChanged();
                        }


                    } else {
                        Log.w("stam", "Error getting documents.", task.getException());
                    }
                }
            });


        }

        public void addDay (View view){

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            //to convert Date to String, use format method of SimpleDateFormat class.
            Calendar cal = Calendar.getInstance();
            info.setVisibility(View.INVISIBLE);
            final String strDate =days_names.get(cal.get(Calendar.DAY_OF_WEEK)) +"  " +dateFormat.format(date) ;
            //check if the is already open day

            DocumentReference test = db.collection("users").document(user.get("id")).collection("active")
                    .document(course.get("cid")).collection("days").document(strDate);

            test.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(!task.getResult().exists()){
                        //write to database and update list
                        db.collection("users").document(user.get("id")).collection("active")
                                .document(course.get("cid")).collection("days").document(strDate).set(new HashMap<String, Object>());
                        Day day = new Day();
                        day.setDate(strDate);
                        dayList.add(day);
                        daysListAdapter.notifyDataSetChanged();
                    }
                }
            });

        }

    public void editbtn(View view) {
        Course_info_dialog dialog = new Course_info_dialog();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        dialog.show(fm, DIALOG_DATE);
    }


}
