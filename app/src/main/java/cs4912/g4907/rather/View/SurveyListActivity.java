package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    protected void onListItemClick (ListView l, View v, int position, long id) {
        Toast.makeText(this, "Clicked row " + position, Toast.LENGTH_SHORT).show();
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
