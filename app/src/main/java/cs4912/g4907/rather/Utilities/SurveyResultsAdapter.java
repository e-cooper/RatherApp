package cs4912.g4907.rather.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;

import cs4912.g4907.rather.Model.Choice;
import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

/**
 * Created by Amit on 4/16/2015.
 */
public class SurveyResultsAdapter extends ParseQueryAdapter<ParseObject>{

    public SurveyResultsAdapter(Context context, final Survey survey) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Question");
                query.whereEqualTo("survey", survey);
                query.addAscendingOrder("order");
                return query;
            }
        });
    }
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        Question question = (Question)object;
        TextView questionLabel, leftGraphLabel, rightGraphLabel, leftGraphShare, rightGraphShare, countBreakdownLabel, choiceALabel, choiceBLabel;
        Button viewTextResultsButton;
        TextView questionIdField;

        String questionType = "123";
        try {
            questionType = question.getQuestionType().fetchIfNeeded().getString("name");
        }
        catch (ParseException e) {
            Log.d("getItemView","questionType fetch failed");
        }


        switch (questionType){
            case "Yes/No":
                v = View.inflate(getContext(), R.layout.survey_result_row, null);
                break;
            case "Single Choice":
                v = View.inflate(getContext(), R.layout.survey_result_row_sc, null);
                break;
            case "Text":
                v = View.inflate(getContext(), R.layout.survey_result_row_t, null);
                break;
        }

        super.getItemView(object, v, parent);

        ParseQuery ResponseQuery = new ParseQuery("Response");
        ResponseQuery.whereEqualTo("question", question);
        int ResponseCount = getQueryCount(ResponseQuery);

        switch (questionType){
            case "Yes/No":
                questionLabel = (TextView) v.findViewById(R.id.question_label);
                leftGraphLabel = (TextView) v.findViewById(R.id.left_label);
                rightGraphLabel = (TextView) v.findViewById(R.id.right_label);
                leftGraphShare = (TextView) v.findViewById(R.id.left_share);
                rightGraphShare = (TextView) v.findViewById(R.id.right_share);
                countBreakdownLabel = (TextView) v.findViewById(R.id.count_breakdown_label);

                questionLabel.setText(String.valueOf(question.getOrder())+". "+question.getContent());
                leftGraphLabel.setText("Yes");
                rightGraphLabel.setText("No");
                int yesCount = getQueryCount(ResponseQuery.whereEqualTo("yesNo", true));
                int noCount = getQueryCount(ResponseQuery.whereEqualTo("yesNo",false));
                setupGraph(leftGraphShare, rightGraphShare, yesCount, noCount, ResponseCount);
                setCountBreakdownLabel(countBreakdownLabel,ResponseCount,yesCount,noCount);
                break;

            case "Single Choice":
                questionLabel = (TextView) v.findViewById(R.id.question_label_sc);
                leftGraphLabel = (TextView) v.findViewById(R.id.left_label_sc);
                rightGraphLabel = (TextView) v.findViewById(R.id.right_label_sc);
                leftGraphShare = (TextView) v.findViewById(R.id.left_share_sc);
                rightGraphShare = (TextView) v.findViewById(R.id.right_share_sc);
                countBreakdownLabel = (TextView) v.findViewById(R.id.count_breakdown_label_sc);
                choiceALabel = (TextView) v.findViewById(R.id.survey_results_row_choiceA_label_sc);
                choiceBLabel = (TextView) v.findViewById(R.id.survey_results_row_choiceB_label_sc);

                questionLabel.setText(String.valueOf(question.getOrder())+". "+question.getContent());
                leftGraphLabel.setText("A");
                rightGraphLabel.setText("B");
                List<Choice> choiceList = getChoices(question);
                setChoiceLabels(choiceALabel, choiceBLabel, choiceList);
                int choiceACount = getQueryCount(ResponseQuery.whereEqualTo("choice", choiceList.get(0)));
                int choiceBCount = getQueryCount(ResponseQuery.whereEqualTo("choice", choiceList.get(1)));
                setupGraph(leftGraphShare, rightGraphShare, choiceACount, choiceBCount, ResponseCount);
                setCountBreakdownLabel(countBreakdownLabel,ResponseCount,choiceACount,choiceBCount);
                break;

            case "Text":
                questionLabel = (TextView) v.findViewById(R.id.question_label_t);
                viewTextResultsButton = (Button) v.findViewById(R.id.survey_result_view_text_results_button);
                questionIdField = (TextView) v.findViewById(R.id.question_id_hack_t);

                questionLabel.setText(String.valueOf(question.getOrder())+". "+question.getContent());
                viewTextResultsButton.setText("View " + ResponseCount + " Results");
                questionIdField.setText(question.getObjectId());

                break;
        }

        return v;
    }

    private TextView createChoiceLabel(Context context, int viewId, boolean isB){
        TextView choiceLabel = new TextView(context);
        choiceLabel.setId(viewId);
        choiceLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        TableRow.LayoutParams choiceParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        if(isB){ choiceParams.setMargins(0,0,0,R.dimen.survey_result_row_choiceB_bottom_margin);}
        choiceLabel.setLayoutParams(choiceParams);

//        Log.d("createChoiceLabel",choiceLabel.toString());
        return choiceLabel;
    }

    private int getQueryCount(ParseQuery query){
        int count = -1;
        try {
            count=query.count();
            return count;

        }
        catch (ParseException e){
            Log.d("getCount","count failed");
        }
        return count;
    }

    private void setWeight(TextView tv, int weight){
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float)weight);
        tv.setLayoutParams(params);
    }

    private void setCountBreakdownLabel(TextView tv, int totalCount, int leftCount, int rightCount){
        tv.setText(String.valueOf(leftCount) + ":" + String.valueOf(rightCount) + " (" + String.valueOf(totalCount) + " total responses)");
    }

    private List<Choice> getChoices(Question SingleChoiceQuestion){
        List<Choice> choiceList = null;
        ParseQuery ChoiceQuery = new ParseQuery("Choice");
        ChoiceQuery.whereEqualTo("question", SingleChoiceQuestion);
        ChoiceQuery.addAscendingOrder("order");
        try {
            choiceList = ChoiceQuery.find();
            return choiceList;
        }
        catch (ParseException e){
            Log.d("setChoices","retrieving choices failed");
        }
        return choiceList;
    }

    private void setChoiceLabels(TextView aLabel, TextView bLabel, List<Choice> choiceList){
        aLabel.setText("A. " + choiceList.get(0).getContent());
        bLabel.setText("B. " + choiceList.get(1).getContent());
    }

    private void setupGraph(TextView leftShare,TextView rightShare, int lCount, int rCount, int tCount ){
        leftShare.setText(String.valueOf(Math.round((lCount / (float) tCount) * 100)) + "%");
        rightShare.setText(String.valueOf(Math.round((rCount /(float) tCount)*100))+"%");
        setWeight(leftShare,lCount);
        setWeight(rightShare,rCount);
    }
}
