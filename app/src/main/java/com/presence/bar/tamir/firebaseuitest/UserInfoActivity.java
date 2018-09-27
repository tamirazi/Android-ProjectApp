package com.presence.bar.tamir.firebaseuitest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class UserInfoActivity extends AppCompatActivity {

    private Spinner title;
    private EditText first_name , last_name , state_id;
    String title_s = "מר";
    private ArrayList<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        titles = new ArrayList<>();
        titles.add("מר");
        titles.add("גברת");
        titles.add("דוקטור");
        titles.add("פרופסור");


        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        state_id = findViewById(R.id.state_id);

        title = findViewById(R.id.titles);
        ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(this , R.array.title_spinner , android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                title_s = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        title.setAdapter(ad);

        Intent i = getIntent();
        if(i.hasExtra("map")){
            HashMap<String , String> map = (HashMap<String , String> )i.getSerializableExtra("map");
            first_name.setText(map.get("first name"));
            last_name.setText(map.get("last name"));
            title.setSelection(titles.indexOf(map.get("title")));
            state_id.setText(map.get("state id"));
        }




    }

    public void saveClicked(View view) {
        if(first_name.getText().toString().isEmpty() || last_name.getText().toString().isEmpty() || state_id.getText().toString().isEmpty()){
            Toast.makeText(UserInfoActivity.this, "בבקשה מלא את כל הפרטים",Toast.LENGTH_LONG ).show();
        }else{
            Intent ans = new Intent();
            HashMap<String , String> map = new HashMap<>();
            map.put("title" , title_s);
            map.put("last name" , last_name.getText().toString());
            map.put("first name" , first_name.getText().toString());
            map.put("state id" , state_id.getText().toString());
            ans.putExtra("map" , map);
            setResult(RESULT_OK , ans);
            finish();
        }

    }
}
