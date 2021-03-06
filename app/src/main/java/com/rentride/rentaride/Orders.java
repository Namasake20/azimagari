package com.rentride.rentaride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rentride.rentaride.MyAdapter.OrderAdapter;
import com.rentride.rentaride.MyModel.OrderModel;

public class Orders extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerView = findViewById(R.id.orderRV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(Orders.this);
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        FirebaseRecyclerOptions<OrderModel> options = new FirebaseRecyclerOptions.Builder<OrderModel>().setQuery(ordersRef.child(String.valueOf(signInAccount.getEmail()).replace(".","*")).child("Booked"),OrderModel.class).build();

        FirebaseRecyclerAdapter<OrderModel, OrderAdapter> adapter = new FirebaseRecyclerAdapter<OrderModel, OrderAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderAdapter orderAdapter, int i, @NonNull OrderModel orderModel) {
                orderAdapter.txtCarNameO.setText(orderModel.getCar_name());
                orderAdapter.txtPickDate.setText(orderModel.getPickUpDate());
                orderAdapter.txtDropDate.setText(orderModel.getReturnDate());
//                orderAdapter.txtPickPlace.setText(orderModel.getPhone_number());
                orderAdapter.txtCarPriceO.setText("KES"+orderModel.getTotal_amount());
            }

            @NonNull
            @Override
            public OrderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout_file,parent,false);
                OrderAdapter holder = new OrderAdapter(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}