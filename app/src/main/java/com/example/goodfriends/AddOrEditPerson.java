package com.example.goodfriends;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrEditPerson extends AppCompatActivity {

    //variable is used in debugging
    private static final String TAG = "Edit";

    private StorageReference storageReference;


    //variables for layout components
    private TextView tv_id;
    private EditText et_personName, et_phoneNumber, et_profilePicture;
    public ImageView iw_profilePicture;
    public Uri imageURi;

    String id;
    Person person = null;
    List<Person> personList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_or_edit_person);

        tv_id =  findViewById(R.id.tv_id);
        et_personName = findViewById(R.id.et_personName);
        et_phoneNumber =  findViewById(R.id.et_phoneNumber);
        et_profilePicture =  findViewById(R.id.et_profilePicture);
        iw_profilePicture =  findViewById(R.id.iw_profilePicture);


        //variables for firebase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //id is like a parameter for person. If it is not null then user has selected current person, if null then user have pressed new button in menu
        Intent intent = getIntent();
        id = intent.getStringExtra("uid");

        if ( id != null) {

            DocumentReference docRef = db.collection("friends").document(id);
            docRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Person person = documentSnapshot.toObject(Person.class);

                            tv_id.setText(person.getId());
                            et_personName.setText(person.getPersonName());
                            et_phoneNumber.setText(person.getPhoneNumber());
                            et_profilePicture.setText(person.getProfilePicture());

                            //glide is helper to get image from database to imageview
                            if (person.getProfilePicture().toString()!="") {
                                Glide
                                    .with(getApplicationContext())
                                    .load(person.getProfilePicture().toString())
                                    .placeholder(R.drawable.ic_baseline_photo_library_24)
                                    .into(iw_profilePicture);
                            }

                        }
                    });

        }


        Button btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> person = new HashMap<>();
                //parameter must be exact like in class ( case sensitive in firebase)
                person.put("personName", et_personName.getText().toString());
                person.put("phoneNumber", et_phoneNumber.getText().toString());
                person.put("profilePicture", et_profilePicture.getText().toString());

                updatePerson ( person, id);

                Intent intent = new Intent( AddOrEditPerson.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("friends").document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Friend has been removed successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error removing friend!", e);
                            }
                        });

                Intent intent = new Intent( AddOrEditPerson.this, MainActivity.class);
                startActivity(intent);
            }
        });




        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( AddOrEditPerson.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_chooseProfilePicture = findViewById(R.id.btn_chooseProfilePicture);
        btn_chooseProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });


        Button btn_removeProfilePicture = findViewById(R.id.btn_removeProfilePicture);
        btn_removeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename =  et_personName.getText().toString();

                deleteFileFromFirebase(filename);

                et_profilePicture.setText("");

                Map<String, Object> person = new HashMap<>();
                person.put("personName", et_personName.getText().toString());
                person.put("phoneNumber", et_phoneNumber.getText().toString());
                person.put("profilePicture", et_profilePicture.getText().toString());

                //updatePerson ( person, id);
                iw_profilePicture.setImageResource(0);

            }
        });


    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void deleteFileFromFirebase(String filename) {
        // Create a storage reference from our app
        StorageReference storageRef = storageReference.child("ProfilePictures/"+ filename );


        // Delete the file
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(getApplicationContext(), "Profile picture deleted " + filename , Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Error Profile picture deleting " + filename , Toast.LENGTH_SHORT).show();
            }
        });    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageURi = data.getData();
            //url is saved to person data
            iw_profilePicture.setImageURI(imageURi);

            //profilepicture get its name from person name
            String filename = et_personName.getText().toString();
            //upload file to firebase storage
            uploadPicture(filename);
        }

    }

    private void uploadPicture(String filename) {
        StorageReference storageRef = storageReference.child("ProfilePictures/"+ filename );
        storageRef.putFile(imageURi)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Snackbar.make(findViewById(android.R.id.content), "Profilepicture " + filename + " uploaded.", Snackbar.LENGTH_LONG).show();

                    Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                    firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            et_profilePicture.setText(url);
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload profilepicture" + filename, Toast.LENGTH_LONG).show();
                }
            });
    }


    private void updatePerson(Map<String, Object> person, String id) {

        if (id != null) {
            db
                .collection("friends")
                .document(id)
                .set(person)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data successfully saved");
                        Toast.makeText(getApplicationContext(), "Data successfully saved!" , Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error in save", e);
                        Toast.makeText(getApplicationContext(), "Error in save! " , Toast.LENGTH_SHORT).show();
                    }
                });

        }
        else {
            db
                .collection("friends")
                .add(person)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Data saved with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Data saved with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding data", e);
                    }
                });
        }
    }

    //remember to set permission in AndroidManifest.xml "android.permission.CALL_PHONE"
    //and also in physical phone
    public void makePhoneCall(View view) {
        String dial = "tel:" + et_phoneNumber.getText().toString();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(dial));
        startActivity(intent);

        Toast.makeText(this, "Call to " + et_personName.getText().toString(), Toast.LENGTH_SHORT).show();
    }

}



