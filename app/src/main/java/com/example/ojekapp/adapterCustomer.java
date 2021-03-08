package com.example.ojekapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class adapterCustomer extends FirebaseRecyclerAdapter<CusOrder, adapterCustomer.myviewholder>{

    public adapterCustomer(@NonNull FirebaseRecyclerOptions<CusOrder> option)
    {
        super(option);
    }

    @Override
    protected void onBindViewHolder(@NonNull final myviewholder holder, final int position, @NonNull final CusOrder cusOrder)
    {
        holder.name.setText(cusOrder.getName());
        holder.phone.setText(cusOrder.getPhone());
        holder.lokasi.setText(cusOrder.getLokasi());
        holder.jarak.setText(cusOrder.getJarak());
        holder.total.setText(cusOrder.getTotal());
        holder.Lt.setText(cusOrder.getLatitude());
        holder.Lg.setText(cusOrder.getLongitude());
        holder.OID.setText(cusOrder.getOrderID());

        holder.btnTO.setOnClickListener(new View.OnClickListener() {
            private Context context = holder.btnTO.getContext();
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Order");
                builder.setMessage("Take Order ??");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("order")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });

        holder.btnMap.setOnClickListener(new View.OnClickListener() {
            private Context context = holder.btnMap.getContext();
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Map.class);
                intent.putExtra("Order ID", cusOrder.getOrderID());
                context.startActivity(intent);
            }
        });
    } // End of OnBindViewMethod

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.customercard,parent,false);
        return new myviewholder(view);
    }


    class myviewholder extends RecyclerView.ViewHolder
    {
        //        CircleImageView img;
        Button btnTO, btnMap;
        TextView name,phone,lokasi,jarak,total, Lt, Lg, OID;
        public myviewholder(@NonNull View itemView)
        {
            super(itemView);
//            img=(CircleImageView) itemView.findViewById(R.id.img1);
            name = (TextView)itemView.findViewById(R.id.cusName);
            phone = (TextView)itemView.findViewById(R.id.cusPhone);
            lokasi = (TextView)itemView.findViewById(R.id.cusDestination);
            jarak = (TextView)itemView.findViewById(R.id.cusDistance);
            total = (TextView)itemView.findViewById(R.id.cusPay);
            Lt = (TextView)itemView.findViewById(R.id.Latitude);
            Lg = (TextView)itemView.findViewById(R.id.Longitude);
            OID = (TextView)itemView.findViewById(R.id.IDOrder);

            btnTO = (Button)itemView.findViewById(R.id.TO);
            btnMap = (Button) itemView.findViewById(R.id.TOMap);
        }
    }
}