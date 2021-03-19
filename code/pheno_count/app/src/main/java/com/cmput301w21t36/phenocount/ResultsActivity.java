package com.cmput301w21t36.phenocount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * This model class passes a trials list to TrialAdapter to display results of the trials conducted
 * It also starts an intent to the QR activity
 * @author Marzookh
 */
public class ResultsActivity extends AppCompatActivity {
    ListView trials;
    ArrayAdapter<Trial> trialAdapter;
    ArrayList<Trial> trialList;
    Experiment exp;//defining the Experiment object

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //initializing attributes
        trials =findViewById(R.id.trial_list);
        trialList = new ArrayList<>();

        //getting intent
        exp = (Experiment) getIntent().getSerializableExtra("experiment");//defining the Experiment object
        trialList = exp.getTrials();

        Measurement mtrial = (Measurement) trialList.get(trialList.size()-1);
        System.out.println("Measurement:"+mtrial.getMeasurement());

        //initializing adapter
        trialAdapter = new TrialAdapter(this,trialList);
        trials.setAdapter(trialAdapter);

        trials.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (ResultsActivity.this, GenerateQrActivity.class);
                intent.putExtra("data", 1); //change this
                startActivity(intent);
            }
        });
    }
}
