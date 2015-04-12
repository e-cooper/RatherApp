package cs4912.g4907.rather.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.QuestionType;
import cs4912.g4907.rather.R;

public class NewQuestionActivity extends Activity {

    private Question question;
    private TextView surveyTextView;
    private EditText contentField;
    private Button addNewQuestionButton;
    private Button finishNewQuestionButton;
    private Spinner questionTypeSpinner;
    private ParseQueryAdapter<QuestionType> mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        question = new Question();

        Intent i = getIntent();
        final String surveyId = i.getStringExtra("survey_id");
        final Number[] questionOrder = new Number[1];
        questionOrder[0] = i.getIntExtra("order_number", 0);

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

        mainAdapter = new ParseQueryAdapter<>(this, new ParseQueryAdapter.QueryFactory<QuestionType>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("QuestionType");
                return query;
            }
        });
        mainAdapter.setTextKey("name");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        surveyTextView = (TextView) findViewById(R.id.new_question_survey_label);
        contentField = (EditText) findViewById(R.id.new_question_content_input);
        addNewQuestionButton = (Button) findViewById(R.id.add_question);
        finishNewQuestionButton = (Button) findViewById(R.id.finish_question);
        questionTypeSpinner = (Spinner) findViewById(R.id.new_question_spinner);

        questionTypeSpinner.setAdapter(mainAdapter);
        questionTypeSpinner.setSelection(1);
        questionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                QuestionType selectedQuestionType = (QuestionType) questionTypeSpinner.getSelectedItem();
                String name = selectedQuestionType.getName();
                ParseQuery questionTypeQuery = new ParseQuery("QuestionType");
                questionTypeQuery.whereEqualTo("name", name);
                questionTypeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {
                            finish();
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            question.setQuestionType(object);
                        }
                    }
                });
            }
            public void onNothingSelected(AdapterView<?> parent) {
                String selectedQuestionType = questionTypeSpinner.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), selectedQuestionType, Toast.LENGTH_SHORT).show();
            }
        });

        addNewQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setOrder(questionOrder[0]);
                question.setContent(contentField.getText().toString());

                question.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            setResult(RESULT_OK);
                            Intent i = new Intent(NewQuestionActivity.this, NewQuestionActivity.class);
                            i.putExtra("survey_id", surveyId);
                            i.putExtra("order_number", (int) questionOrder[0] + 1);
                            startActivity(i);
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

        finishNewQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.setOrder(questionOrder[0]);
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
