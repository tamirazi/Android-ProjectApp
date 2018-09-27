package dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.presence.bar.tamir.firebaseuitest.MainActivity;
import com.presence.bar.tamir.firebaseuitest.R;
import com.presence.bar.tamir.firebaseuitest.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Course_pac.Course;
import Course_pac.CourseInfo;
import Course_pac.CourseListAdapter;
import Course_pac.RecyclerItemClickListener;
import ch.uepaa.p2pkit.AlreadyEnabledException;
import ch.uepaa.p2pkit.P2PKit;
import ch.uepaa.p2pkit.P2PKitStatusListener;
import ch.uepaa.p2pkit.StatusResult;
import ch.uepaa.p2pkit.discovery.DiscoveryInfoTooLongException;
import ch.uepaa.p2pkit.discovery.DiscoveryListener;
import ch.uepaa.p2pkit.discovery.DiscoveryPowerMode;
import ch.uepaa.p2pkit.discovery.Peer;
import io.opencensus.tags.propagation.TagContextSerializationException;

public class Search_course_dialog extends DialogFragment implements P2PKitStatusListener{


    private final DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onStateChanged(final int state) {
            Log.d("DiscoveryListener", "State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            Log.d("DiscoveryListener", "Peer discovered: " + peer.getPeerId() + " with info: " + new String(peer.getDiscoveryInfo()));
            String[] msg = new String(peer.getDiscoveryInfo()).split("->");
            String uid = msg[0];
            String course_name = msg[1];
            String path = msg[2];
            paths.add(path);
            Course c = new Course();
            c.set("name" , course_name);
            courses_list.add(c);
            course_adapter.notifyDataSetChanged();



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
    private FirebaseFirestore db;
    private RecyclerView list;
    private List<Course> courses_list;
    private CourseListAdapter course_adapter;
    private TextView canclebtn;
    private List<String> paths;
    private User user;
    private static final String APP_KEY = "02665f6c436b4ae58e7979973f728393";

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onEnabled() {
        Log.d("DiscoveryListener", "P2p onEnabled " );
        Toast.makeText(getContext() , "מחפש קורסים פתוחים באזורך",Toast.LENGTH_SHORT ).show();
        P2PKit.addDiscoveryListener(mDiscoveryListener);
    }

    @Override
    public void onDisabled() {
        Log.d("DiscoveryListener", "P2p onDisabled " );
    }

    @Override
    public void onError(StatusResult result) {
        Toast.makeText(getContext() , "בעיה בחיפוש קורסים",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onException(Throwable throwable) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        Toast.makeText(getContext() , "מפסיק לחפש",Toast.LENGTH_SHORT ).show();
//        if(P2PKit.isEnabled())
//            P2PKit.disable();
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_course_dialog , container , false);

        paths = new ArrayList<>();
        list = view.findViewById(R.id.c_list);
        courses_list = new ArrayList<>();
        course_adapter = new CourseListAdapter(courses_list);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(course_adapter);
        canclebtn = view.findViewById(R.id.stop_listening);
        db = FirebaseFirestore.getInstance();

        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext() , "מפסיק לחפש",Toast.LENGTH_SHORT ).show();
                P2PKit.disable();
                dismiss();
            }
        });

        list.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), list,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String pt = paths.get(position);
                        DocumentReference doc = db.document(pt);

                        doc.collection("students").document(user.getValue("id")).set(user.getMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Discovey" , "i fucking write to DB");
                                Toast.makeText(getContext() , "נוכחותך נרשמה בהצלחה",Toast.LENGTH_SHORT ).show();

                                final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.definite);
                                mp.start();
                            }
                        });



                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                }));



        if(!P2PKit.isEnabled()){
            try {
                P2PKit.enable(view.getContext(), APP_KEY, this);
                P2PKit.startDiscovery("ms".getBytes() , DiscoveryPowerMode.HIGH_PERFORMANCE , mDiscoveryListener);
            } catch (AlreadyEnabledException | DiscoveryInfoTooLongException e) {
                e.printStackTrace();
            }
        }


        return view;
    }
}
