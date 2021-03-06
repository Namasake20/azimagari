 package com.rentride.rentaride;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rentride.rentaride.MyAdapter.CarAdapter;
import com.rentride.rentaride.MyModel.Cars;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private DatabaseReference productsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

//    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Orders.class);
                startActivity(intent);
            }
        });
        SessionService service = new SessionService(Home.this);
        HashMap<String,String> userDetail =   service.getUserDetailsFromSession();
        String userName = userDetail.get(SessionService.USERNAME);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.RView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        View headerView = navigationView.getHeaderView(0);
        TextView UserName = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImage = headerView.findViewById(R.id.user_profile_image);

        //TODO:returns a null pointer exception
//        UserName.setText(Prevalent.CurrentOnlineUser.getUsername());
//        UserName.setText(userName);
//        Picasso.get().load(Prevalent.CurrentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImage);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null){
            UserName.setText(signInAccount.getEmail());
            Picasso.get().load(signInAccount.getPhotoUrl()).placeholder(R.drawable.profile).into(profileImage);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cars> options =
                new FirebaseRecyclerOptions.Builder<Cars>().setQuery(productsRef,Cars.class)
                        .build();
        FirebaseRecyclerAdapter<Cars, CarAdapter> adapter =
                new FirebaseRecyclerAdapter<Cars, CarAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CarAdapter carAdapter, int i, @NonNull Cars cars) {

                        carAdapter.txtName.setText(cars.getName());
                        carAdapter.txtPass.setText(cars.getPassengers()+" passengers");
                        carAdapter.txtBag.setText("Air conditioning "+cars.getAc());
                        carAdapter.txtDoor.setText(cars.getDoors()+" Doors");
                        carAdapter.txtTrans.setText(cars.getTransmission()+" transmission");
                        carAdapter.txtPrice.setText("KES "+ cars.getPrice()+"/Day");
                        Picasso.get().load(cars.getImg()).into(carAdapter.carImg);

                        carAdapter.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Home.this,CarDetail.class);
                                intent.putExtra("pid",cars.getPid());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CarAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cars_layout,parent,false);

                        return new CarAdapter(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {
//            Intent intent = new Intent(Home.this,Fleet.class);
//            startActivity(intent);

        }
        else if (id == R.id.nav_orders)
        {
            Intent intent = new Intent(Home.this,Orders.class);
            startActivity(intent);

        }

//        else if (id == R.id.nav_settings)
//        {
//            Intent intent = new Intent(Home.this,Profile.class);
//            startActivity(intent);
//
//
//        }
        else if (id == R.id.nav_logout)
        {
//            Paper.book().destroy();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Home.this,SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
        else if (id == R.id.helpc){
            startActivity(new Intent(Home.this,HelpCenter.class));
        }
        else  if (id == R.id.Dinfo){
            startActivity(new Intent(Home.this,About.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}