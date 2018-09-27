package Day;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.presence.bar.tamir.firebaseuitest.R;

import java.util.List;

public class DaysListAdapter extends RecyclerView.Adapter<DaysListAdapter.ViewHolder>{

    public List<Day> data;


    public class ViewHolder extends RecyclerView.ViewHolder{

        View aView;
        TextView name , date , hour;

        public ViewHolder(View v){
            super(v);
            aView = v;
            date = aView.findViewById(R.id.date);
            name = aView.findViewById(R.id.day_name);
            hour = aView.findViewById(R.id.hour);

        }
    }

    public DaysListAdapter(List<Day> list ){
        this.data = list;
    }

    @NonNull
    @Override
    public DaysListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_list_item, parent , false);
        return new DaysListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaysListAdapter.ViewHolder holder, int position) {
        String[] fullDate = data.get(position).getDate().split(" ");
        holder.name.setText( "יום " + fullDate[0]);
        holder.date.setText(fullDate[2]);
        holder.hour.setText(fullDate[3]);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
