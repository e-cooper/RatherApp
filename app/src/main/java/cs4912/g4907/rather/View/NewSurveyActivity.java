package cs4912.g4907.rather.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

public class NewSurveyActivity extends Activity {

    private Survey survey;
    private EditText nameField;
    private CheckBox privacyCheckbox;
    private EditText passwordField;
    private Button createNewSurveyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        survey = new Survey();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        nameField = (EditText) findViewById(R.id.new_survey_name_input);
        privacyCheckbox = (CheckBox) findViewById(R.id.new_survey_privacy_input);
        passwordField = (EditText) findViewById(R.id.new_survey_password_input);
        createNewSurveyButton = (Button) findViewById(R.id.create_survey);

        createNewSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                survey.setAuthor(ParseUser.getCurrentUser());
                survey.setTitle(nameField.getText().toString());
                survey.setPrivacy(privacyCheckbox.isChecked());
                if (!privacyCheckbox.isChecked()) {
                    survey.setPassword(passwordField.getText().toString());
                }

                survey.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            setResult(RESULT_OK);
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
        });
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
