package com.example.jay.syncdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jay on 06-06-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private ArrayList<Contact> arrayList = new ArrayList<>();

    RecyclerAdapter(ArrayList<Contact> arrayList){
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview,parent,false);
        return new MyViewHolder(view);
    }
	
	//This method will set resources on view components.
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.NameTV.setText(arrayList.get(position).getName());
        holder.EmailTV.setText(arrayList.get(position).getEmail());
        int Sync_Status = arrayList.get(position).getSync_Status();

        if(Sync_Status==DBContact.Sync_Status_OK){
            holder.imageView.setImageResource(R.drawable.done);
        }
        else{
            holder.imageView.setImageResource(R.drawable.wait);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView NameTV,EmailTV;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            NameTV = (TextView)itemView.findViewById(R.id.NameTV);
            EmailTV = (TextView)itemView.findViewById(R.id.EmailTV);

            imageView = (ImageView)itemView.findViewById(R.id.imageView);
        }
    }
}
