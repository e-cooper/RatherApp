package cs4912.g4907.rather.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import cs4912.g4907.rather.Model.Response;
import cs4912.g4907.rather.R;
import cs4912.g4907.rather.Utilities.OnSwipeTouchListener;

public class ResponseActivity extends Activity {

    private Response response;
    private boolean canAnswer = false;
    private boolean answered = false;

    private TextView questionLabel;
    private Button yesButton, noButton, submitButton;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        questionLabel = (TextView) findViewById(R.id.response_question_label);
        yesButton = (Button) findViewById(R.id.response_button_yes);
        noButton = (Button) findViewById(R.id.response_button_no);
        submitButton = (Button) findViewById(R.id.response_button_answer);
        frameLayout = (FrameLayout) findViewById(R.id.fragmentContainer);

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

        frameLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                //Toast.makeText(ResponseActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                if (canAnswer) {
                    response.setYesNo(true);
                    answered = true;
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.response_yes_recorded,
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

            public void onSwipeLeft() {
                if (canAnswer) {
                    response.setYesNo(false);
                    answered = true;
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.response_no_recorded,
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

            public void onSwipeBottom() {
                //Toast.makeText(ResponseActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answered) {
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
