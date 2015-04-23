package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.Response;
import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;
import cs4912.g4907.rather.Utilities.SurveyResultsAdapter;

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

    public void viewTextResults(View view){
        LinearLayout parent = (LinearLayout)view.getParent();
        String questionID = ((TextView)parent.findViewById(R.id.question_id_hack_t)).getText().toString(); //Fetch relevant question id from our hacky phantom textview
        ParseQuery<Question> query = ParseQuery.getQuery("Question");
        query.getInBackground(questionID, new GetCallback<Question>() {
            public void done(Question q, ParseException e) {
                if (e == null) {
                    ParseQueryAdapter<Response> textResultsAdapter = getTextResultQueryAdapter(q);
                    Log.e("adapter debugging", String.valueOf(textResultsAdapter.getCount()));

                    Dialog dialog = new Dialog(SurveyResultsActivity.this);
                    dialog.setContentView(R.layout.text_results_dialog);
                    ListView textResults = (ListView) dialog.findViewById(R.id.text_results_listview);
                    textResults.setAdapter(textResultsAdapter);
                    dialog.setCancelable(true);
                    dialog.setTitle(q.getContent());
                    dialog.show();

                } else {
                    Log.e("viewTextResults", "question query failed");
                }
            }
        });
    }

    private ParseQueryAdapter<Response> getTextResultQueryAdapter(Question q){
        final Question concernedQuestion = q;
        ParseQueryAdapter<Response> adapter = new ParseQueryAdapter<Response>(this,
                new ParseQueryAdapter.QueryFactory<Response>() {
                    public ParseQuery create() {
                        ParseQuery query = new ParseQuery("Response");
                        query.whereEqualTo("question", concernedQuestion);
                        return query;
                    }
                }
        );
        adapter.setTextKey("text");
        return adapter;
    }
}
