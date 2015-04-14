package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.Response;
import cs4912.g4907.rather.Model.ResponseSet;
import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

public class ResponseActivity extends Activity {

    private Response response;
    private boolean canAnswer = false;

    private TextView questionLabel;
    private Button yesButton, noButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        questionLabel = (TextView) findViewById(R.id.response_question_label);
        yesButton = (Button) findViewById(R.id.response_button_yes);
        noButton = (Button) findViewById(R.id.response_button_no);
        submitButton = (Button) findViewById(R.id.response_button_answer);

        response = new Response();
        Intent g = getIntent();
        final String responseSetId = g.getStringExtra("response_set_id");
        ParseQuery responseSetQuery = new ParseQuery("ResponseSet");
        responseSetQuery.whereEqualTo("objectId", responseSetId);
        responseSetQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    finish();
                    Toast.makeText(
                            getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    response.setResponseSet(object);
                    setResponseQuestion();
                }
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canAnswer) {
                    response.setYesNo(true);
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.response_recorded,
                            Toast.LENGTH_SHORT
                            ).show();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Answer could not be set",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canAnswer) {
                    response.setYesNo(false);
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.response_recorded,
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Answer could not be set",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (response.getYesNo() != null) {
                    saveResponse();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.response_select_answer,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private void setResponseQuestion() {
        Intent g = getIntent();
        final String surveyId = g.getStringExtra("survey_id");
        final Number questionOrder = g.getIntExtra("question_order", 1);
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
                    ParseQuery questionQuery = new ParseQuery("Question");
                    questionQuery.whereEqualTo("survey", object);
                    questionQuery.whereEqualTo("order", questionOrder);
                    questionQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                finish();
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                response.setQuestion(object);
                                canAnswer = true;
                                questionLabel.setText(object.getString("content"));
                            }
                        }
                    });
                }
            }
        });
    }

    private void saveResponse() {
        response.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent g = getIntent();
                    final String responseSetId = g.getStringExtra("response_set_id");
                    final String surveyId = g.getStringExtra("survey_id");
                    final int[] questionOrder = new int[1];
                    questionOrder[0] = g.getIntExtra("question_order", 1);
                    final int questionCount = g.getIntExtra("question_count", 0);
                    if (questionOrder[0] < questionCount) {
                        setResult(RESULT_OK);
                        Intent i = new Intent(ResponseActivity.this, ResponseActivity.class);
                        i.putExtra("response_set_id", responseSetId);
                        i.putExtra("survey_id", surveyId);
                        i.putExtra("question_order", questionOrder[0] + 1);
                        i.putExtra("question_count", questionCount);
                        startActivity(i);
                        finish();
                    } else {
                        ParseQuery responseSetQuery = new ParseQuery("ResponseSet");
                        responseSetQuery.whereEqualTo(
                                "objectId",
                                responseSetId
                        );
                        responseSetQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {
                                if (object == null) {
                                    finish();
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    object.put("complete", true);
                                    object.saveEventually();
                                    Toast.makeText(
                                            getApplicationContext(),
                                            R.string.response_set_complete,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        finish();
                    }
                }
            }
        });
    }

}
