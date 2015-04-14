package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;

import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

public class NewSurveyActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener{

    private Survey survey = new Survey();
    private EditText nameField;
    private CheckBox privacyCheckbox;
    private EditText passwordField;
    private Button createNewSurveyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        survey = new Survey();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        nameField = (EditText) findViewById(R.id.new_survey_name_input);
        privacyCheckbox = (CheckBox) findViewById(R.id.new_survey_privacy_input);
        passwordField = (EditText) findViewById(R.id.new_survey_password_input);
        createNewSurveyButton = (Button) findViewById(R.id.create_survey);

        passwordField.setVisibility(privacyCheckbox.isChecked()?View.GONE:View.VISIBLE);

        privacyCheckbox.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                passwordField.setVisibility(privacyCheckbox.isChecked()?View.GONE:View.VISIBLE);
            }
        });

        createNewSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                survey.setAuthor(ParseUser.getCurrentUser());
                survey.setTitle(nameField.getText().toString()); //Needs input validation
                survey.setPrivacy(privacyCheckbox.isChecked());
                if (!privacyCheckbox.isChecked()) {
                    if(passwordField.getText().toString().length()!=0) {
                        survey.setPassword(passwordField.getText().toString());
                        expiryDateDialog(); //Once ready, this initiates the expiration date dialog
                    }
                    else{
                        Toast.makeText(NewSurveyActivity.this, "Please enter a password" , Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    expiryDateDialog(); //Once ready, this initiates the expiration date dialog
                }
            }
        });
    }

    private void saveSurvey(Survey s){
        s.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent i = new Intent(NewSurveyActivity.this, NewQuestionActivity.class);
                    i.putExtra("survey_id", survey.getObjectId());
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

    //Call when ready to move on to NewQuestionsActivity
    private void expiryDateDialog(){
        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new ExpirationDatePickerFragment();
        newFragment.show(fm,"datePicker");
    }

    //Survey is saved only after expiration date is set
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(view.isShown()) { //bug fix for onDateSet getting called twice
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, 23, 59, 59);
            Date date = calendar.getTime();
            survey.setExpirationDate(date);
            saveSurvey(survey);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_survey, menu);
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
}
