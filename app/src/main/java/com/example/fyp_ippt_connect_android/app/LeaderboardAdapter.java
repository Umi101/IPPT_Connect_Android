package com.example.fyp_ippt_connect_android.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fyp_ippt_connect_android.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    // Data
    private Context mContext;
    private List<User> list;
    private String exercise;

    public LeaderboardAdapter(Context mContext, List<User> list, String exercise) {
        this.mContext = mContext;
        this.list = list;
        this.exercise = exercise;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_leaderboard,parent,false);
        return new LeaderboardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        User user = list.get(position);
        holder.name.setText(user.getName());
        if (exercise.equals("Push-up")){
            holder.count.setText(String.valueOf(user.getPushUpTotalCount()));
        }
        else if (exercise.equals("Sit-up")){
            holder.count.setText(String.valueOf(user.getSitUpTotalCount()));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder{

        TextView name, count;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.leaderboardName);
            count = itemView.findViewById(R.id.leaderboardScore);
        }
    }
}
