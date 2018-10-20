package com.presence.bar.tamir.firebaseuitest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import Course_pac.RecyclerItemClickListener;
import ch.uepaa.p2pkit.AlreadyEnabledException;
import ch.uepaa.p2pkit.P2PKit;
import ch.uepaa.p2pkit.P2PKitStatusListener;
import ch.uepaa.p2pkit.StatusResult;
import ch.uepaa.p2pkit.discovery.DiscoveryInfoTooLongException;
import ch.uepaa.p2pkit.discovery.DiscoveryListener;
import ch.uepaa.p2pkit.discovery.DiscoveryPowerMode;
import ch.uepaa.p2pkit.discovery.Peer;

public class UsersArrivedActivity extends AppCompatActivity implements P2PKitStatusListener {

    private FirebaseFirestore db;
    private RecyclerView list;
    private TextView info;
    private List<User> users_list;
    private UsersListAdapter users_list_adapter;
    private String uid , course_name;
    private int phones_near_by = 0;
    private Button refresh;
    private Toolbar tb;
    private static final String APP_KEY = "02665f6c436b4ae58e7979973f728393";
    String path;


    private final DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onStateChanged(final int state) {
            Log.d("DiscoveryListener", "State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            Log.d("DiscoveryListener", "Peer discovered: " + peer.getPeerId() + " with info: " + new String(peer.getDiscoveryInfo()));
            phones_near_by++;
        }

        @Override
        public void onPeerLost(final Peer peer) {
            Log.d("DiscoveryListener", "Peer lost: " + peer.getPeerId());
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            Log.d("DiscoveryListener", "Peer updated: " + peer.getPeerId() + " with new info: " + new String(peer.getDiscoveryInfo()));
        }

        @Override
        public void onProximityStrengthChanged(Peer peer) {
            Log.d("DiscoveryListener", "Peer " + peer.getPeerId() + " changed proximity strength: " + peer.getProximityStrength());
        }
    };

    @Override
    protected void onStop() {
        Toast.makeText(this ,  "אינך גלוי יותר" ,Toast.LENGTH_SHORT).show();
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_arrived);
        db = FirebaseFirestore.getInstance();
        list = findViewById(R.id.users_list);
        info = findViewById(R.id.no_students);
        users_list = new ArrayList<>();
        users_list_adapter = new UsersListAdapter(users_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(users_list_adapter);
        tb = findViewById(R.id.toolbar2);



        Intent data = getIntent();
        uid = data.getStringExtra("uid");
        course_name = data.getStringExtra("course_name");
        path = data.getStringExtra("path");

        tb.setSubtitle(data.getStringExtra("date"));
        tb.setTitle(course_name);

//        db.collection(data.getStringExtra("path") + "/" + "students")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                users_list.clear();
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot document : task.getResult()) {
//                        info.setVisibility(View.INVISIBLE);
//                        User user = new User();
//                        user.updateMap((Serializable)document.getData());
//                        users_list.add(user);
//                        users_list_adapter.notifyDataSetChanged();
//                    }
//
//                } else {
//                    Log.w("stam", "Error getting documents.", task.getException());
//                }
//            }
//        });

        db.collection(data.getStringExtra("path") + "/" + "students").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("realtime", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Log.d("realtime", "New student: " + dc.getDocument().getData());
                        info.setVisibility(View.INVISIBLE);
                        User user = new User();
                        user.updateMap((Serializable)dc.getDocument().getData());
                        users_list.add(user);
                        users_list_adapter.notifyDataSetChanged();
                    }
                }
            }
        });



        list.addOnItemTouchListener(new RecyclerItemClickListener(this, list,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //todo : show dialog with user information
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
        if(P2PKit.isEnabled()) P2PKit.disable();
        if(!P2PKit.isEnabled()){
            try {
                P2PKit.enable(this, APP_KEY, this);
            } catch (AlreadyEnabledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnabled() {
        try {
            String msg =  uid +"->"+course_name + "->" + path;
            P2PKit.startDiscovery(msg.getBytes(), DiscoveryPowerMode.HIGH_PERFORMANCE, mDiscoveryListener);
            Log.d("Discovery", "Start Discovery" + msg);
            Toast.makeText(this ,  "כעת אנשים סביבך יוכלו לגלות אותך" ,Toast.LENGTH_SHORT).show();
        } catch (DiscoveryInfoTooLongException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisabled() {
        System.out.println("P2P disabled");
        Log.d("Discovery", "onDisabled");
    }

    @Override
    public void onError(StatusResult result) {

    }

    @Override
    public void onException(Throwable throwable) {

    }


}
