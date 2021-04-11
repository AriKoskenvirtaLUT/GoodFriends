package com.example.goodfriends;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.goodfriends.databinding.ActivityMainBinding;
import com.example.goodfriends.databinding.OneLinePersonBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private ActivityMainBinding activityMainBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        //database object and query
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("friends");


        RecyclerView mFirestoreRecyclerView;
        mFirestoreRecyclerView = activityMainBinding.rlBodyList;

        //rl_recyclerList rows
        FirestoreRecyclerOptions<Person> options = new FirestoreRecyclerOptions.Builder<Person>()
                .setQuery(query, Person.class)
                .build();

        //create adapter to recyclerlist
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Person, PersonViewHolder>(options) {

            //viewholder for recyclelist
            @NonNull
            @Override
            public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                OneLinePersonBinding oneLinePersonBinding = OneLinePersonBinding.inflate(layoutInflater, parent, false);
                return new PersonViewHolder(oneLinePersonBinding);
            }

            @Override
            protected void onBindViewHolder(@NonNull PersonViewHolder holder, int position, @NonNull Person model) {

                holder.tv_personName.setText(model.getPersonName());
                holder.tv_phoneNumber.setText(model.getPhoneNumber());


                if (model.getProfilePicture().toString() != null) {
                    Glide
                            .with(MainActivity.this)
                            .load(model.getProfilePicture().toString())
                            .into(holder.iw_profilePicture);
                }

            }
        };

        mFirestoreRecyclerView.setHasFixedSize(true);
        mFirestoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreRecyclerView.setAdapter(firestoreRecyclerAdapter);

    }

    private class PersonViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_personName;
        private TextView tv_phoneNumber;
        private ImageView iw_profilePicture;
        private de.hdodenhof.circleimageview.CircleImageView iw_personPicture2;

        OneLinePersonBinding oneLinePersonBinding;

        public PersonViewHolder(@NonNull OneLinePersonBinding oneLinePersonBinding) {
            super(oneLinePersonBinding.getRoot());
            this.oneLinePersonBinding = oneLinePersonBinding;

            tv_personName = oneLinePersonBinding.tvPersonName;
            tv_phoneNumber = oneLinePersonBinding.tvPhoneNumber;
            iw_profilePicture = oneLinePersonBinding.iwProfilePicture;



            this.oneLinePersonBinding.oneLinePersonLayout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      //StorageReference storageReference;

                      DocumentSnapshot snapshot;
                      snapshot = (DocumentSnapshot) firestoreRecyclerAdapter.getSnapshots().getSnapshot(getAdapterPosition());
                      String uid = snapshot.getId();

                      Intent intent = new Intent(MainActivity.this, AddOrEditPerson.class);
                      intent.putExtra("uid", uid);

                      MainActivity.this.startActivity(intent);
                  }
              }
            );



        }
    }




    @Override
    protected void  onStart(){
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    protected void  onStop(){
        super.onStop();

        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.stopListening();
        }
    }

    //attach menu to main activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //solve selected menu row
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //no need to resolve selected menu, because there is only one option "Add"
        /*if needed later, use like
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                finish();
                return true;
            case R.id.action_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
        }
        */

        Intent intent = new Intent(MainActivity.this, AddOrEditPerson.class);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }


}