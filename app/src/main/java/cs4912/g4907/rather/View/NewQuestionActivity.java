package cs4912.g4907.rather.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.R;

public class NewQuestionActivity extends Activity {

    private Question question;
    private final ParseObject[] survey = new ParseObject[1];
    private TextView surveyTextView;
    private EditText contentField;
    private Button createNewQuestionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        question = new Question();

        Intent i = getIntent();
        String surveyId = i.getStringExtra("survey_id");

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
                    question.setSurvey(object);
                    setData();
                }
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        surveyTextView = (TextView) findViewById(R.id.new_question_survey_label);
        contentField = (EditText) findViewById(R.id.new_question_content_input);
        createNewQuestionButton = (Button) findViewById(R.id.create_question);

        createNewQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setContent(contentField.getText().toString());

                question.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void setData() {
        surveyTextView.setText(question.getSurvey().getString("title"));
    }

}
