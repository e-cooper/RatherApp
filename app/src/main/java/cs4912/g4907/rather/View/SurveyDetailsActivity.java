package cs4912.g4907.rather.View;

import android.app.Activity;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);

        surveyNameTextView = (TextView) findViewById(R.id.survey_details_name);
        surveyAuthorTextView = (TextView) findViewById(R.id.survey_details_author);
        surveyPrivacyTextView = (TextView) findViewById(R.id.survey_details_privacy);

        Intent i = getIntent();
        final String surveyId = i.getStringExtra("survey_id");

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
//        Log.i("survey details debug",author);
        surveyAuthorTextView.setText(author);
        surveyPrivacyTextView.setText(survey.getPrivacy()?"Public":"Private");
    }
}
