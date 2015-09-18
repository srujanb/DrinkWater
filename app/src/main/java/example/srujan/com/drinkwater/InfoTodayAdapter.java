package example.srujan.com.drinkwater;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by Srujan on 07-09-2015.
 */
public class InfoTodayAdapter extends RecyclerView.Adapter<InfoTodayAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<InformationToday> data2 = Collections.emptyList();
    FileOutputStream fos;
    FileInputStream fis;
    Context contextMain;

    public InfoTodayAdapter(Context context,List<InformationToday> data){
        inflater = LayoutInflater.from(context);
        this.data2 = data;

        contextMain = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_today, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InformationToday current = data2.get(position);
        holder.volume.setText(String.valueOf(current.value));
        String timeInString = "";
        if(current.hours < 10)
            timeInString = "0" + current.hours;
        else
            timeInString = "" + current.hours;
        timeInString = timeInString + ":";
        if (current.minutes < 10)
            timeInString = timeInString + "0" + current.minutes;
        else
            timeInString = timeInString + current.minutes;
        holder.time.setText(timeInString);
    }

    @Override
    public int getItemCount() {
        return data2.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView volume;
        ImageView deleteIcon;
        TextView time;

        public MyViewHolder(View itemView) {
            super(itemView);
            volume = (TextView) itemView.findViewById(R.id.volume);
            deleteIcon = (ImageView) itemView.findViewById(R.id.deleteRow);
            deleteIcon.setOnClickListener(this);
            time = (TextView)itemView.findViewById(R.id.time);
        }

        @Override
        public void onClick(View v) {
            try {
                delete(getPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void delete(int position) throws IOException {
        data2.remove(position);
        notifyItemRemoved(position);
        fos = contextMain.openFileOutput("arrayListToday",Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(data2);
        oos.close();
        fos.close();
    }

}

