package com.cmput301w21t36.phenocount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * @author Charffy
 * This DiscussionActivity class stores all the questions asked by
 * users related to a certain experiment.
 * Context: ExperimentActivity -> MenuActivity-> 'Discuss' button -> this;
 * Click 'Discuss' button in the MenuActivity can transfer user to the this activity,
 * so the DiscussionActivity class has its own UI.
 *
 */
public class DiscussionActivity extends AppCompatActivity implements ShowFragment.OnFragmentInteractionListener {
    //a collection of question posts of a certain experiment
    private ListView qList;
    private ArrayList<Question> questions;
    private Experiment experiment;
    private User user; //I think we need to get who is currently viewing this forum

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion_activity);
    }

    /**
     * This method allows user to ask a question of the experiment,
     * the newly added question will be stored in the discussion forum.
     * how: click '+' button in UI, a fragment shows, has two text boxes,
     * the top one is a title, says 'New Question',
     * the bottom one is the an edit text, let the user type in the question body.
     */
    public void addQuestion(){
        String text = ""; //get from the user input in edit text in the fragment
        Question question = new Question(user, text);
        questions.add(question);
    }

    public void onAddQuePressed(){
        Intent intent = new Intent(this, Question.class);
        //intent.putExtra(EXTRA_MESSAGE, index);
        startActivity(intent);
        }

    }

}
