package cs4912.g4907.rather.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.QuestionType;
import cs4912.g4907.rather.Model.Response;
import cs4912.g4907.rather.Model.ResponseSet;
import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

public class SurveyResultsActivity extends Activity {

    private Survey concernedSurvey;
    private ListView surveyResultsList;
    private SurveyResultsAdapter QuestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_results);
        surveyResultsList = (ListView)findViewById(R.id.survey_results_listview);

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
                    Log.d("onCreate","finds survey");
                    concernedSurvey = (Survey) object;
                    setupRows(concernedSurvey);
                }
            }
        });


    }

    private void setupRows(Survey s) {
        final Survey survey = s;
        QuestionAdapter = new SurveyResultsAdapter(this, survey);
//        QuestionAdapter.setTextKey("content");
        surveyResultsList.setAdapter(QuestionAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
