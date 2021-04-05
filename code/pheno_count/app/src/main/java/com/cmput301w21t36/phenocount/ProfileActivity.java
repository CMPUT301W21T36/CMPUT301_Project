package com.cmput301w21t36.phenocount;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Activity which deals with displaying a profile for the current user
 * which will include contact information, their display name, and a unique randomly generated ID.
 * @see MainActivity
 */
public class ProfileActivity extends AppCompatActivity implements ProfileDialog.ProfileDialogListener {

    private String defaultUsername = "Username";
    private String defaultContact = "Contact info";

    private Profile profile = new Profile(defaultUsername, defaultContact);;
    private TextView UIDTextView;
    private TextView usernameTextView;
    private TextView contactTextView;
    private Button editButton;



    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set all TextView variables
        UIDTextView = (TextView) findViewById(R.id.UIDTextView);
        usernameTextView = (TextView) findViewById(R.id.displayNameTextView);
        contactTextView = (TextView) findViewById(R.id.contactTextView);
        editButton = (Button) findViewById(R.id.editInfoButton);

        final String UUID = getIntent().getExtras().getString("UUID");
        DocumentReference docRef = db.collection("User").document(UUID);

        UIDTextView.setText(UUID);

        usernameTextView.setText(profile.getUsername());
        contactTextView.setText(profile.getPhone());

        // Retrieve username and contact info from the database and set the respective textviews
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("Username");
                        String contact = documentSnapshot.getString("ContactInfo");

                        usernameTextView.setText(username);
                        contactTextView.setText(contact);

                        SharedPreferences sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();

                        editor.putString("Username", username);
                        editor.putString("ContactInfo", contact);
                        editor.apply();
                    }
                });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    public void openDialog() {
        ProfileDialog profileDialog = new ProfileDialog();
        profileDialog.show(getSupportFragmentManager(), "Profile Dialog");

    }

    @Override
    public void applyChanges(String username, String contact) {
        usernameTextView.setText(username);
        contactTextView.setText(contact);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String UID = sharedPreferences.getString("ID","");

        Map<String, Object> map = new HashMap<>();
        map.put("ContactInfo", contact);
        map.put("UID", UID);
        map.put("Username", username);

        db.collection("User").document(UID).set(map);

        SharedPreferences sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();


        // this query updates the old username in the users old experiments
        db.collection("Experiment").whereEqualTo("owner",UID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value) {

                    if (error == null) {
                        String expID = doc.getId();
                        db.collection("Experiment").document(expID).update("owner_name",username);
                    }
                }
            }
        });

        // this query updates the old username in the users old trials
        db.collection("Experiment").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value) {
                    if(error == null){
                        String docID = doc.getId();
                        db.collection("Experiment")
                                .document(docID).collection("Trials")
                                .whereEqualTo("userID",UID)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value2, @Nullable FirebaseFirestoreException error) {
                                        for (QueryDocumentSnapshot document : value2) {
                                            if(error ==null){
                                                db.collection("Experiment")
                                                        .document(docID).collection("Trials")
                                                        .document(document.getId())
                                                        .update("owner",username);
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });
        editor.putString("Username", username);
        editor.putString("ContactInfo",contact);
        editor.apply();

        profile.setUsername(username);
        profile.setPhone(contact);
    }
}