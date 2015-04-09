package cs4912.g4907.rather.View;


import android.app.Activity;
import android.app.FragmentManager;
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

import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment {

    private Question question;
    private EditText contentField;
    private Button createNewQuestionButton;

    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_survey, container, false);
        question = new Question();

        contentField = (EditText) v.findViewById(R.id.new_question_content);
        createNewQuestionButton = (Button) v.findViewById(R.id.create_question);

        createNewQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String surveyID = ((NewSurveyActivity) getActivity()).getCurrentSurvey().getObjectId();

                question.setSurvey(surveyID);
                question.setContent(contentField.getText().toString());

                question.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            FragmentManager fm = getActivity().getFragmentManager();
                            fm.popBackStack("NewSurveyFragment",
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                    }
                });
            }
        });

        return v;
    }

}
