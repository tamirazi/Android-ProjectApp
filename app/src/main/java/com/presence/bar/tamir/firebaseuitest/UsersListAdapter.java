package com.presence.bar.tamir.firebaseuitest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import Course_pac.Course;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder>{

    public List<User> data;


    public class ViewHolder extends RecyclerView.ViewHolder{

        View aView;
        TextView name , loc;

        public ViewHolder(View v){
            super(v);
            aView = v;
            name = aView.findViewById(R.id.user_name);
            loc = aView.findViewById(R.id.uid);
        }


    }



    public UsersListAdapter(List<User> list ){
        this.data = list;
    }

    @NonNull
    @Override
    public UsersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent , false);
        return new UsersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListAdapter.ViewHolder holder, int position) {
        String fullName = data.get(position).getValue("first name") + " " + data.get(position).getValue("last name");
        holder.name.setText(fullName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
