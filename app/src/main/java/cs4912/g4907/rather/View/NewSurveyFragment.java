package cs4912.g4907.rather.View;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

public class NewSurveyFragment extends Fragment {

    private EditText nameField;
    private CheckBox privacyCheckbox;
    private EditText passwordField;
    private Button createNewSurveyButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_survey, container, false);

        nameField = (EditText) v.findViewById(R.id.new_survey_name_input);
        privacyCheckbox = (CheckBox) v.findViewById(R.id.new_survey_privacy_input);
        passwordField = (EditText) v.findViewById(R.id.new_survey_password_input);
        createNewSurveyButton = (Button) v.findViewById(R.id.create_survey);

        createNewSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Survey survey = ((NewSurveyActivity) getActivity()).getCurrentSurvey();

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
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } else {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return v;
    }

}
