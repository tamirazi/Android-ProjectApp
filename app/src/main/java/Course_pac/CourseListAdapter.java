package Course_pac;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.presence.bar.tamir.firebaseuitest.R;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    public List<Course> data;


    public class ViewHolder extends RecyclerView.ViewHolder{

        View aView;
        TextView name , loc;

        public ViewHolder(View v){
            super(v);
            aView = v;
            name = aView.findViewById(R.id.course_name);
            loc = aView.findViewById(R.id.loc);
        }


    }



    public CourseListAdapter(List<Course> list ){
        this.data = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_item, parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(data.get(position).getValue("name"));
        holder.loc.setText(data.get(position).getValue("location"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
