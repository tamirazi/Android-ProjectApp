package com.presence.bar.tamir.firebaseuitest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Course_pac.Course;
import Course_pac.CourseInfo;
import Course_pac.CourseListAdapter;
import Course_pac.Courses;
import Course_pac.RecyclerItemClickListener;
import dialogs.Course_info_dialog;
import dialogs.Search_course_dialog;


public class MainActivity extends AppCompatActivity implements Course_info_dialog.OnInputListener {

    private static final String DIALOG_DATE = "course_info";
    private static final int RC_SIGN_IN = 123;

    public User getUser() {
        return user;
    }

    private TextView textUser , info;
    private FirebaseUser firebaseUser;
    private boolean hasUser = false;
    private User user;
    private FirebaseFirestore db;
    private RecyclerView list;
    private List<Course> courses_list;
    private CourseListAdapter course_adapter;
    private Courses my_courses;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handlePerrmision();
        textUser = findViewById(R.id.loc);
        info = findViewById(R.id.no_courses);
        courses_list = new ArrayList<>();
        course_adapter = new CourseListAdapter(courses_list);
        list = findViewById(R.id.listy);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(course_adapter);
        my_courses = new Courses();
        list.addOnItemTouchListener(new RecyclerItemClickListener(this, list,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent course_info = new Intent(MainActivity.this, CourseInfo.class);
                        course_info.putExtra("user" , user.getMap());
                        course_info.putExtra("course" , courses_list.get(position).getCourse_map());
                        startActivityForResult(course_info, 152);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        final int index = position;
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        String msg = "למחוק את " + courses_list.get(index).getValue("name") + " מהרשימה שלך?";
                        dialog.setMessage(msg).setPositiveButton("כן", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("users").document(firebaseUser.getUid()).collection("active").document(courses_list.get(index).getValue("cid")).delete();
                                courses_list.remove(index);
                                if(courses_list.isEmpty()) info.setVisibility(View.VISIBLE);
                                course_adapter.notifyDataSetChanged();
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

    private void handlePerrmision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        15);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        /**
         * the firebaseUser is already signed in
         */
        if (auth.getCurrentUser() != null) {
            System.out.println("The user in already have email and password");
            db= FirebaseFirestore.getInstance();
            firebaseUser = auth.getCurrentUser();
            user = new User(firebaseUser.getUid(), firebaseUser.getEmail());
            /**
             * the firebaseUser in DB
             * show the firebaseUser activity
             */

            db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {    //get information about the current user
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                                    hasUser = document.getId().equals(firebaseUser.getUid());
                                     System.out.println("asking for " + document.getId() + " with  " + firebaseUser.getUid());
                                    if(hasUser){
                                    System.out.println("hasUser in DB");
                                        insetFromDbToUser(document.getData());
                                        break;
                                    }
                                }
                                if(user.hasKey("first name") && user.hasKey("last name") && user.hasKey("title")){
                                    String title = user.getValue("title");
                                    String first = user.getValue("first name");
                                    String last = user.getValue("last name");
                                    String userName =  title + " " + first +  " " + last ;
                                    textUser.setText(userName );
                                }else{
                                    /**
                                     * the firebaseUser not in the DB
                                     * show fill firebaseUser info activity
                                     * send to this activity ref to DB
                                     */
                                    System.out.println("hasNotUser in DB");
                                    fiilInfo(hasUser);
                                }


                            } else {
                                Log.w("Error_onComplete", "Error getting documents.", task.getException());
                            }
                        }
                    });

            //get information about all user active courses
            db.collection("users").document(user.getValue("id")).collection("active").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    courses_list.clear();
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            info.setVisibility(View.INVISIBLE);
                            Course course = new Course();
                            course.updateMap((Serializable) document.getData());
                            courses_list.add(course);
                            course_adapter.notifyDataSetChanged();
                        }

                    } else {
                        Log.w("stam", "Error getting documents.", task.getException());
                    }
                }
            });
        } else {
            /**
             * default by google
             */
            System.out.println("NEW USER ENTER THE SYSTEM FIREBASE UI AUTH IS RUNNING");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
                            .setTheme(R.style.AppTheme)
                            .setIsSmartLockEnabled(false)
                            .setLogo(R.drawable.logo2)
                            .build()
                    , RC_SIGN_IN);
        }
    }

    public void sign_out(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage("אתה בטוח שברצונך להתנתק מהמערכת?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sign out and go to firebase ui auth page
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // firebaseUser is now signed out
                                startActivityForResult(
                                        AuthUI.getInstance()
                                                .createSignInIntentBuilder()
                                                .setAvailableProviders(Arrays.asList(
                                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                                        new AuthUI.IdpConfig.PhoneBuilder().build()))
                                                .setTheme(R.style.GreenTheme)
                                                .setIsSmartLockEnabled(false)
                                                .setLogo(R.drawable.logo)
                                                .build()
                                        , RC_SIGN_IN);

                            }
                        });
            }
        }).setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();

    }   //sign out button

    public void insetFromDbToUser(Map<String, Object> data) {
        user.updateMap((Serializable) data);
    }

    public void fiilInfo(boolean hasUser) {
        Intent i = new Intent(MainActivity.this, UserInfoActivity.class);
        //new user
        if (!hasUser) {
            startActivityForResult(i, 150);
        } else {      //user want to edit data
            i.putExtra("map", user.getMap());
            startActivityForResult(i, 150);
        }

    }

    public void editbtn(View view) {
        fiilInfo(hasUser);
    }

    public void addbtn(View view) {
        Course_info_dialog dialog = new Course_info_dialog();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        dialog.show(fm, DIALOG_DATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 150 && resultCode == RESULT_OK) {
            user.updateMap(data.getSerializableExtra("map"));
            user.writeUserToDB(db);
        }
        //create new CourseInfo
        if (requestCode == 151 && resultCode == RESULT_OK) {
            //fill all course info and save it on db
            Map<String , Object> map = (Map<String , Object> )data.getSerializableExtra("map");
            my_courses.add(map);
            my_courses.writeUserToDB(db.collection("users").document(user.getValue("id")));
            my_courses.updateCourses(db.collection("users").document(user.getValue("id")));
        }

        if(requestCode == 123 && resultCode == RESULT_OK){
             //todo: here we can take the full name user from google auto ui and use it
        }


    }

    @Override
    public void sendInput(String name, String loc ,String color) {
        HashMap<String , Object> map = new HashMap<>() ;
        map.put("name" , name);
        map.put("location" , loc);
        map.put("color" , color);
        map.put("cid" , Calendar.getInstance().getTime().toString());
        my_courses.add(map);
        my_courses.writeUserToDB(db.collection("users").document(user.getValue("id")));
        my_courses.updateCourses(db.collection("users").document(user.getValue("id")));
        Course course = new Course();
        course.updateMap(map);
        courses_list.add(course);
        info.setVisibility(View.INVISIBLE);
        course_adapter.notifyDataSetChanged();
    }

    public void sendInfoBtn(View view) {
        Search_course_dialog dialog = new Search_course_dialog();
        dialog.setUser(user);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        dialog.show(fm, DIALOG_DATE);
    }

}