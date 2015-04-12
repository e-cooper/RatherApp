package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import cs4912.g4907.rather.Model.ResponseSet;
import cs4912.g4907.rather.R;

public class ResponseActivity extends Activity {

    private ResponseSet responseSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        responseSet = new ResponseSet();
        responseSet.setComplete(false);
        responseSet.setUser(ParseUser.getCurrentUser());
        Intent g = getIntent();
        final String surveyId = g.getStringExtra("survey_id");
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
                    responseSet.setSurvey(object);
                    saveResponseSet();
                }
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new ResponseFragment();
            manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }

    private void saveResponseSet() {
        responseSet.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    setResult(RESULT_OK);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public ResponseSet getCurrentResponseSet() {
        return responseSet;
    }

}
