package com.presence.bar.tamir.firebaseuitest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User {

    private HashMap<String, String> userInfo;
    private String id;
    private String email;


    public User(){
        userInfo = new HashMap<>();
    }
    public User(String uid , String userEmail){
        userInfo = new HashMap<>();
        id = new String(uid);
        email = new String(userEmail);
        userInfo.put("id",uid);
        userInfo.put("email",userEmail);
    }
    public String getValue(String key){
        if(userInfo.containsKey(key)){
            return userInfo.get(key);
        }
        return null;
    }

    public void set(String key , String value){
        userInfo.put(key , value);
    }

    boolean hasKey(String key){
        return userInfo.containsKey(key);
    }

    public void writeUserToDB(FirebaseFirestore db){
        //db.child(id).setValue(userInfo);
        db.collection("users").document(id).set(userInfo);
    }

    public HashMap<String, String> getMap() {
        return userInfo;
    }

    public void updateMap(Serializable newMap){
        userInfo.putAll((HashMap<String , String>)newMap);
        //userInfo = new HashMap<>((HashMap<String , String>)newMap);
    }
}
