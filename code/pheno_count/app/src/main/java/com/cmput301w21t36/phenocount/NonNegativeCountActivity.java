package com.cmput301w21t36.phenocount;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This class represents NonNegativeCountActivity trials and is part of the GUI
 * Sets count to 0 if field is left blank
 */
public class NonNegativeCountActivity extends AppCompatActivity {
    NonNegativeCount trial;
    Experiment newexp;//defining the Experiment object
    Boolean location=false;
    TextView coordinates;
    DecimalFormat numberFormat;
    SharedPreferences sharedPrefs;
    Menu expMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_non_negative_count);
        numberFormat = new DecimalFormat("#.0000");

        // receiving intent object
        newexp = (Experiment) getIntent().getSerializableExtra("experiment");

        //setting user to owner of trial
        sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String username = sharedPrefs.getString("Username", "");
        String UUID = sharedPrefs.getString("ID", "");
        Profile profile = new Profile(username);
        User user = new User(UUID,profile);
        trial = new NonNegativeCount(user);

        newexp.getDates().add(trial.getDate());

        //setting type of trial
        trial.setType("NonNegativeCount");

        // Capture the layout's TextView and set the string as its text
        TextView desc = findViewById(R.id.desc4);
        desc.setText("Description:" + String.valueOf(newexp.getDescription()));

        TextView owner = findViewById(R.id.owner4);
        owner.setText("Owner:" + newexp.getOwner().getProfile().getUsername());

        TextView status = findViewById(R.id.status4);
        status.setText("Status:" + String.valueOf(newexp.getExpStatus()));

        TextView exptype= findViewById(R.id.exptype4);
        exptype.setText("Experiment Type: Non-Negative Count");

        EditText count = findViewById(R.id.count_editText);

        //setting location coordinates
        coordinates= findViewById(R.id.coordinates);
        coordinates.setText("Location : NOT ADDED");

        final Button recordcbtn = findViewById((R.id.recordcbtn));
        recordcbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks if location is provided
                if(location || !newexp.isRequireLocation()) {
                    String temp = count.getText().toString();
                    int value = 0;
                    if (!"".equals(temp)) {
                        value = Integer.parseInt(temp);
                    }
                    trial.setValue(value);
                    newexp.getTrials().add(trial);
                    Toast.makeText(
                            NonNegativeCountActivity.this,
                            "Count Recorded",
                            Toast.LENGTH_SHORT).show();

                    //passing the experiment object back to DisplayExperimentActivity
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("experiment", newexp);
                    setResult(Activity.RESULT_OK, returnIntent);

                    finish();
                }else{
                    Toast.makeText(
                            NonNegativeCountActivity.this,
                            "Please add a location first",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button lbtn = findViewById(R.id.locationbtn4);
        lbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //passing trial object to get location updated
                Intent intent = new Intent (NonNegativeCountActivity.this,MapsActivity.class);
                intent.putExtra("trial_obj",trial);

                int LAUNCH_SECOND_ACTIVITY = 1;
                startActivityForResult(intent,LAUNCH_SECOND_ACTIVITY); }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_menu, menu);
        expMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.myList) {
            Intent intent = new Intent(NonNegativeCountActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.search) {
            Intent intent = new Intent(NonNegativeCountActivity.this, SearchingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    //Sends the experiment object and retrieves the updated object
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int LAUNCH_SECOND_ACTIVITY = 1;
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                location = true;
                //catching the trial object back
                trial = (NonNegativeCount) data.getSerializableExtra("trial_obj");

                if(trial.getLatitude() == 200 && trial.getLongitude() == 200) //location has not been added as these values can never be achieved.
                    coordinates.setText("Location : NOT ADDED");
                else
                    coordinates.setText("Location : ("+numberFormat.format(trial.getLatitude())+","+numberFormat.format(trial.getLongitude())+")");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("No Data");
            }
        }
    }
}
