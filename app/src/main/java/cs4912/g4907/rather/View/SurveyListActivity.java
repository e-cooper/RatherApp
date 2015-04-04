package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import cs4912.g4907.rather.R;

/**
 * Created by Eli on 4/4/2015.
 */
public class SurveyListActivity extends ListActivity {
    private ParseQueryAdapter<ParseUser> mainAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setClickable(true);

        mainAdapter = new ParseQueryAdapter<ParseUser>(this, ParseUser.class);
        mainAdapter.setTextKey("name");

        // Default view is all surveys
        setListAdapter(mainAdapter);
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

            /*
            case R.id.action_new: {
                newSurvey();
                break;
            }
            */
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSurveyList() {
        mainAdapter.loadObjects();
        setListAdapter(mainAdapter);
    }

    /*
    private void newSurvey() {
        Intent i = new Intent(this, NewSurveyActivity.class);
        startActivity(i);
    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // If a new survey has been added, update
            // the list of surveys
            updateSurveyList();
        }
    }
}
