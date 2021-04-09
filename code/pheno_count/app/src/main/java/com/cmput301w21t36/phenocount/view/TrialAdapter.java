package com.cmput301w21t36.phenocount;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Measure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

/**
 * This Controller/View class accepts parameters from ResultsActivity and displays the results
 * of the trials
 */
public class TrialAdapter extends ArrayAdapter<Trial> {
    private ArrayList<Trial> trialList;
    private Context context;
    private String user;
    private String owner;
    private ArrayList<String> blacklist;


    public TrialAdapter(Context context, ArrayList<Trial> trialList,String currentUser,String owner) {
        super(context,0,trialList);
        this.trialList = trialList;
        this.context = context;
        this.user = currentUser;
        this.owner = owner;
        blacklist = new ArrayList<>();

    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_trials,parent,false);
        }

        //getting the trial object from results activity
        Trial trial = getItem(position);

/*        if (!trial.getStatus()){
            trialList.remove(position);
        }*/

        //initializing textviews
        TextView trial_no = view.findViewById(R.id.trial_no);
        TextView trial_owner = view.findViewById(R.id.trial_owner);
        TextView trial_outcome = view.findViewById(R.id.trial_outcome);
        TextView trial_date = view.findViewById(R.id.trial_date);
        ImageView deleteImage =  view.findViewById(R.id.image_delete);

        //showing ImageView only if current user is owner of exp
        System.out.println("UUID: "+user);
        System.out.println("OWNER: "+owner);

        if (!user.equals(owner)) {
            deleteImage.setVisibility(View.GONE);
        }

        //setting common trial attributes
        trial_owner.setText(trial.getOwner().getProfile().getUsername());
        trial_date.setText(trial.getDate());

        //checking if trial is ignored
        if (!trial.getStatus()){
            trial_no.setText("Trial "+(position+1)+"    [IGNORED]");
        }
        else {
            trial_no.setText("Trial " + (position+1));
        }
        //checking type of trial and setting result
        if (trial.getType().equals("Binomial")) {
            Binomial btrial = (Binomial) getItem(position);
            if (btrial.getResult()){
                trial_outcome.setText("Success");
            }
            else{
                trial_outcome.setText("Failure");
            }
        }
        if (trial.getType().equals("Count")) {
            Count ctrial = (Count) getItem(position);
            trial_outcome.setText(""+ctrial.getCount());
        }
        if (trial.getType().equals("Measurement")) {
            Measurement mtrial = (Measurement) getItem(position);
            trial_outcome.setText(""+mtrial.getMeasurement());
        }
        if (trial.getType().equals("NonNegativeCount")) {
            NonNegativeCount ntrial = (NonNegativeCount) getItem(position) ;
            trial_outcome.setText(""+ntrial.getValue());
        }
        //to delete a trial
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Confirmation")
                        .setMessage("Do you want to ignore this experimenter")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                trial.setStatus(false);
                                for (Trial trial:trialList){
                                    if(!trial.getStatus()) {
                                        String UUID = trial.getOwner().getUID();
                                        blacklist.add(UUID);
                                    }
                                }
                                for (Trial trial : trialList){
                                    if(blacklist.contains(trial.getOwner().getUID())){
                                        trial.setStatus(false);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
                notifyDataSetChanged();
            }
        });


        return view;
    }

}
