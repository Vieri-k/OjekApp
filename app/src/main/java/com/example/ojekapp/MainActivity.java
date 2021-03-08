package com.example.ojekapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    RecyclerView recview, recviewcus, recviewojek;
    com.example.ojekapp.adapter adapter;
    com.example.ojekapp.adapterCustomer adapterCus;
    com.example.ojekapp.adapterAll adapterojekall;
    private final String log = "RESULT DATA";

    // Customer Order
    DatabaseReference db;
    private Button btnmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recview=(RecyclerView)findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<model> options =
                new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("driver"), model.class)
                        .build();

        adapter=new adapter(options);
        recview.setAdapter(adapter);

        /////////////////////////////////////////////////////
//
//        recviewojek = (RecyclerView)findViewById(R.id.recviewalll);
//        recviewojek.setLayoutManager(new LinearLayoutManager(this));
//
//        FirebaseRecyclerOptions<modelAll> options1 =
//                new FirebaseRecyclerOptions.Builder<modelAll>()
//                        .setQuery(FirebaseDatabase.getInstance().getReference().child("driver"), modelAll.class)
//                        .build();
//
//        adapterojekall = new adapterAll(options1);
//        recviewojek.setAdapter(adapterojekall);

        //////////////////////////////////////////////////////

        recviewcus = (RecyclerView)findViewById(R.id.recyclerViewCustomer);
        recviewcus.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CusOrder> option =
                new FirebaseRecyclerOptions.Builder<CusOrder>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("order"), CusOrder.class)
                        .build();

        adapterCus = new adapterCustomer(option);
        recviewcus.setAdapter(adapterCus);

    }

    public void btnmap(View view){
        Intent intent = new Intent(getApplicationContext(), Map.class);
        startActivity(intent);
    }

//    ValueEventListener valueEventListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//           CusList.clear();
//            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                CusOrder md = snapshot.getValue(CusOrder.class);
//                CusList.add(md);
//                adapterCus.notifyDataSetChanged();
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    };

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapterCus.startListening();
//        adapterojekall.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        adapterCus.stopListening();
//        adapterojekall.stopListening();
    }

//    public void createNewContactDialog(){
//
//    }
}