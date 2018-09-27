package dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.presence.bar.tamir.firebaseuitest.MainActivity;
import com.presence.bar.tamir.firebaseuitest.R;

public class Course_info_dialog extends DialogFragment {

    public interface OnInputListener{
        void sendInput(String name , String loc);
    }
    public OnInputListener onInputListener;
    private static final String TAG = "course_info";

    private TextView save , cancle;
    private EditText name , loc;

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_info_dialog , container , false);

        save = view.findViewById(R.id.save_btn);
        cancle = view.findViewById(R.id.cancle_btn);
        loc = view.findViewById(R.id.loc_dialog);
        name = view.findViewById(R.id.course_name_dialog);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_input , loc_input;
                name_input = name.getText().toString();
                loc_input = loc.getText().toString();

                if(name_input.isEmpty() || loc_input.isEmpty()){
                    Toast.makeText(getContext() , "בבקשה מלא את כל הפרטים",Toast.LENGTH_LONG ).show();
                }
                else{//therw is input
                    onInputListener.sendInput(name.getText().toString() , loc.getText().toString());
                    getDialog().dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onInputListener = (OnInputListener)getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
