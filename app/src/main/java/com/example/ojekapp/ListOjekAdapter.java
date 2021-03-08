package com.example.ojekapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ListOjekAdapter extends FirebaseRecyclerAdapter<OjekModel, ListOjekAdapter.myviewholder>{

    public ListOjekAdapter(@NonNull FirebaseRecyclerOptions<OjekModel> options)
    {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final myviewholder holder, final int position, @NonNull final OjekModel model)
    {
        holder.name.setText(model.getName());
        holder.phone.setText(model.getPhone());
        holder.region.setText(model.getRegion());
    } // End of OnBindViewMethod

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        return new myviewholder(view);
    }


    class myviewholder extends RecyclerView.ViewHolder
    {
//        CircleImageView img;
        ImageView edit,delete;
        TextView name,phone,region;
        public myviewholder(@NonNull View itemView)
        {
            super(itemView);
//            img=(CircleImageView) itemView.findViewById(R.id.img1);
            name = (TextView)itemView.findViewById(R.id.nametext);
            phone = (TextView)itemView.findViewById(R.id.phonetext);
            region = (TextView)itemView.findViewById(R.id.regiontext);

//            edit=(ImageView)itemView.findViewById(R.id.editicon);
//            delete=(ImageView)itemView.findViewById(R.id.deleteicon);
        }
    }
}


