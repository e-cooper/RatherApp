package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseQueryAdapter;

import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

/**
 * Created by Eli on 4/4/2015.
 */
public class SurveyListActivity extends ListActivity {
    private ParseQueryAdapter<Survey> mainAdapter;
//    private EditText private_survey_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setClickable(true);

        mainAdapter = new ParseQueryAdapter<Survey>(this, Survey.class);
        mainAdapter.setTextKey("title");

        // Default view is all surveys
        setListAdapter(mainAdapter);
    }

    @Override
    protected void onListItemClick ( ListView l, View v, int position, long id) {
        final Survey survey = mainAdapter.getItem(position);
        if(!survey.getPrivacy()) {
            // get private_survey_password_prompt.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.private_survey_password_prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set private_survey_password_prompt.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(userInput.getText().toString().equals(survey.getPassword())){
                                        Intent i = new Intent(SurveyListActivity.this, SurveyDetailsActivity.class);
                                        i.putExtra("survey_id", survey.getObjectId());
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(SurveyListActivity.this, "Sorry, that's the wrong password" , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
        else {
            Intent i = new Intent(this, SurveyDetailsActivity.class);
            i.putExtra("survey_id", survey.getObjectId());
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_survey_list, menu);
        return true;
    }

    /*
	 * Posting surveys and refreshing the list will be controlled from the Action
	 * Bar.
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh: {
                updateSurveyList();
                break;
            }

            case R.id.action_profile: {
                viewProfile();
                break;
            }

            case R.id.action_new: {
                newSurvey();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSurveyList() {
        mainAdapter.loadObjects();
        setListAdapter(mainAdapter);
    }

    private void newSurvey() {
        Intent i = new Intent(this, NewSurveyActivity.class);
        startActivity(i);
    }

    private void viewProfile() {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // If a new survey has been added, update
            // the list of surveys
            updateSurveyList();
        }
    }
}
