package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        survey = new Survey();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        setContentView(R.layout.activity_new_survey);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new NewSurveyFragment();
            manager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    public Survey getCurrentSurvey() {
        return survey;
    }

}
