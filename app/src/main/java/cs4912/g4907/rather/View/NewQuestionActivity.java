package cs4912.g4907.rather.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cs4912.g4907.rather.Model.Choice;
import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.QuestionType;
import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

public class NewQuestionActivity extends Activity {

    private Question question;
    private TextView surveyTextView;
    private EditText contentField;
    private Button addNewQuestionButton;
    private Button finishNewQuestionButton;
    private Spinner questionTypeSpinner;
    private String currentQuestionType;
    private String surveyId;

    private EditText choice1Field;
    private EditText choice2Field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        question = new Question();

        Intent i = getIntent();
        surveyId = i.getStringExtra("survey_id");
        final Number[] questionOrder = new Number[1];
        questionOrder[0] = i.getIntExtra("order_number", 1);

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
        choice1Field = (EditText) findViewById(R.id.new_question_choice_1);
        choice2Field = (EditText) findViewById(R.id.new_question_choice_2);
        addNewQuestionButton = (Button) findViewById(R.id.add_question);
        finishNewQuestionButton = (Button) findViewById(R.id.finish_question);
        questionTypeSpinner = (Spinner) findViewById(R.id.new_question_spinner);

        choice1Field.setVisibility(View.GONE);
        choice2Field.setVisibility(View.GONE);

        ParseQuery<QuestionType> query = ParseQuery.getQuery("QuestionType");
        query.findInBackground(new FindCallback<QuestionType>() {
            public void done(List<QuestionType> questionTypesList, ParseException e) {
                if (e == null) {
                    ArrayList<String> questionTypeNames = new ArrayList<String>();

                    for (int i = 0; i < questionTypesList.size(); i++) {
                        questionTypeNames.add(questionTypesList.get(i).getName());
                    }

                    //Manually order spinner elements here
                    //Make Yes/No the first item
                    int yesNoIndex = questionTypeNames.indexOf("Yes/No");
                    questionTypeNames.add(0, questionTypeNames.get(yesNoIndex));
                    int yesNoLastIndex = questionTypeNames.lastIndexOf("Yes/No");
                    questionTypeNames.remove(yesNoLastIndex);
                    //Make Single Choice the second item
                    int singleChoiceIndex = questionTypeNames.indexOf("Single Choice");
                    questionTypeNames.add(1, questionTypeNames.get(singleChoiceIndex));
                    int singleChoiceLastIndex = questionTypeNames.lastIndexOf("Single Choice");
                    questionTypeNames.remove(singleChoiceLastIndex);

                    questionTypeSpinner.setAdapter(new ArrayAdapter<String>(NewQuestionActivity.this, android.R.layout.simple_spinner_item, questionTypeNames));
                    currentQuestionType = questionTypeNames.get(0);
                    setChoiceFieldsVisibility();
                } else {
                    Log.d("Retrieve failed", "Error: " + e.getMessage());
                }
            }
        });

        questionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currentQuestionType = questionTypeSpinner.getSelectedItem().toString();
                setChoiceFieldsVisibility();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                String selectedQuestionType = questionTypeSpinner.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), selectedQuestionType, Toast.LENGTH_SHORT).show();
            }
        });

        addNewQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object[] areFieldsEmpty = checkFieldsEmpty();
                if(checkFieldsEmpty()==null){
                saveQuestion(questionOrder[0],contentField.getText().toString(),"next");}
                else{
                    Toast.makeText(getApplicationContext(), (String)areFieldsEmpty[1] , Toast.LENGTH_SHORT).show();
                }
            }
        });

        finishNewQuestionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Object[] areFieldsEmpty = checkFieldsEmpty();
                if(checkFieldsEmpty()==null) {
                    Survey parentSurvey;
                    try {
                        parentSurvey = (Survey) question.getSurvey().fetchIfNeeded();
                        parentSurvey.setPublished(true);
                        parentSurvey.setPublicationDate(Calendar.getInstance().getTime());
                    } catch (ParseException e) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Error, couldn't publish Survey: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    saveQuestion(questionOrder[0], contentField.getText().toString(), "finish");
                }
                else{
                    Toast.makeText(getApplicationContext(), (String)areFieldsEmpty[1] , Toast.LENGTH_SHORT).show();
                }
                //Publish the survey

            }
        });
    }

    private void setData() {
        surveyTextView.setText(question.getSurvey().getString("title"));
    }

    private void saveQuestion(final Number order, final String content, final String buttonPressed){
        ParseQuery questionTypeQuery = new ParseQuery("QuestionType");
        questionTypeQuery.whereEqualTo("name", currentQuestionType);
        questionTypeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    question.setQuestionType(object);
                    question.setContent(content);
                    question.setOrder(order);
                    if(buttonPressed.equals("finish")){
                        question.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                            if (e == null) {
                                saveChoices(question);
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
                    else if(buttonPressed.equals("next")){
                        question.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                            if (e == null) {
                                saveChoices(question);
                                Intent i = new Intent(NewQuestionActivity.this, NewQuestionActivity.class);
                                i.putExtra("survey_id", surveyId);
                                i.putExtra("order_number", (int) order + 1);
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
                }
            }
        });
    }

    private void setChoiceFieldsVisibility(){
        if(currentQuestionType.equals("Single Choice")){
            choice1Field.setVisibility(View.VISIBLE);
            choice2Field.setVisibility(View.VISIBLE);
        }
        else{
            choice1Field.setVisibility(View.GONE);
            choice2Field.setVisibility(View.GONE);
        }
    }

    private void saveChoices(Question q){
        if(currentQuestionType.equals("Single Choice")){
            Choice choice1 = new Choice();
            Choice choice2 = new Choice();

            choice1.setOrder(1);
            choice1.setContent(choice1Field.getText().toString());
            choice1.setQuestion(q);

            choice2.setOrder(2);
            choice2.setContent(choice2Field.getText().toString());
            choice2.setQuestion(q);

            choice1.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            choice2.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    private Object[] checkFieldsEmpty(){
        Boolean bool = false;
        String message = "";
        Object[] result = null;
        if(contentField.getText().toString().length()==0){
            bool = true;
            message = "Enter a question";
            result = new Object[]{bool,message};
        }
        switch (currentQuestionType){
            case "Single Choice":
                if(choice1Field.getText().toString().length()==0 || choice2Field.getText().toString().length()==0){
                    bool = true;
                    message = "Enter both choices";
                    result = new Object[]{bool,message};
                }
                break;
        }
        return result;
    }

}
