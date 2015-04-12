package cs4912.g4907.rather.View;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

public class SurveyDetailsActivity extends Activity {

    private TextView surveyNameTextView;
    private TextView surveyAuthorTextView;
    private TextView surveyPrivacyTextView;
    private String surveyListAdapterInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);

        surveyNameTextView = (TextView) findViewById(R.id.survey_details_name);
        surveyAuthorTextView = (TextView) findViewById(R.id.survey_details_author);
        surveyPrivacyTextView = (TextView) findViewById(R.id.survey_details_privacy);

        Intent i = getIntent();
        final String surveyId = i.getStringExtra("survey_id");

        surveyListAdapterInfo = i.getStringExtra("adapter_info");

        ParseQuery surveyQuery = new ParseQuery("Survey");
        surveyQuery.whereEqualTo("objectId", surveyId);
        surveyQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    finish();
                    Toast.makeText(
                            getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    setFields((Survey)object);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey_details, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("adapter_info",surveyListAdapterInfo);
        setResult(RESULT_OK, returnIntent); //This will help us return to the proper screen on SurveyListActivity, either My Surveys or Available Surveys
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("adapter_info",surveyListAdapterInfo);
                setResult(RESULT_OK, returnIntent);
                finish();
                return true;

            case R.id.action_profile: {
                viewProfile();
                break;
            }

            case R.id.action_respond: {
                newResponse();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewProfile() {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    private void newResponse() {
        Intent g = getIntent();
        final String surveyId = g.getStringExtra("survey_id");

        Intent i = new Intent(this, ResponseActivity.class);
        i.putExtra("survey_id", surveyId);
        startActivity(i);
    }

    private void setFields(Survey survey){
        surveyNameTextView.setText(survey.getTitle());
        String author = "Error";
        try{
            author = survey.getAuthor().fetchIfNeeded().getString("name");
        }
        catch(ParseException e){
            finish();
            Toast.makeText(
                    getApplicationContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        surveyAuthorTextView.setText(author);
        surveyPrivacyTextView.setText(survey.getPrivacy()?"Public":"Private");
    }
}
