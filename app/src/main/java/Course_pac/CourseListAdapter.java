package Course_pac;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.presence.bar.tamir.firebaseuitest.MainActivity;
import com.presence.bar.tamir.firebaseuitest.R;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    public List<Course> data;


    public class ViewHolder extends RecyclerView.ViewHolder{

        View aView;
        LinearLayout color;
        TextView name , loc;

        public ViewHolder(View v){
            super(v);
            aView = v;
            name = aView.findViewById(R.id.course_name);
            loc = aView.findViewById(R.id.loc);
            color = aView.findViewById(R.id.ccolor);
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
        //    GradientDrawable buttonColor = (GradientDrawable) holder.color.getBackground();
//        buttonColor.setColor(Integer.valueOf(data.get(position).getValue("color")));



    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
