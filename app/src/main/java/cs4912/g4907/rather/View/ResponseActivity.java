package cs4912.g4907.rather.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.Response;
import cs4912.g4907.rather.R;

public class ResponseActivity extends FragmentActivity {

    private Response response;
    private boolean canAnswer = false;
    private boolean answered = false;
    private String questionType;

    private TextView questionLabel;
    private Button submitButton;
    private EditText responseText;
    private List<Fragment> fragments;
    private List<String> colors;
    private ParseObject[] choices = new ParseObject[2];

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colors = new ArrayList<>();
        colors.add("#84BD00");
        colors.add("#F44336");
        colors.add("#000000");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        responseText = (EditText) findViewById(R.id.response_textfield);
        questionLabel = (TextView) findViewById(R.id.response_question_label);
        submitButton = (Button) findViewById(R.id.response_button_answer);

        response = new Response();
        Intent g = getIntent();
        final String responseSetId = g.getStringExtra("response_set_id");
        ParseQuery responseSetQuery = new ParseQuery("ResponseSet");
        responseSetQuery.whereEqualTo("objectId", responseSetId);
        responseSetQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    finish();
                    Toast.makeText(
                            getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    response.setResponseSet(object);
                    setResponseQuestion();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionType.equals("Text")) {
                    String answer = responseText.getText().toString();
                    if (answer.matches("")) {
                        Toast.makeText(
                                getApplicationContext(),
                                R.string.response_text_answer,
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        response.setText(answer);
                        saveResponse();
                    }
                }
                else if (answered) {
                    saveResponse();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.response_select_answer,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private void setResponseQuestion() {
        Intent g = getIntent();
        final String surveyId = g.getStringExtra("survey_id");
        final Number questionOrder = g.getIntExtra("question_order", 1);
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
                    ParseQuery questionQuery = new ParseQuery("Question");
                    questionQuery.whereEqualTo("survey", object);
                    questionQuery.whereEqualTo("order", questionOrder);
                    questionQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                finish();
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                response.setQuestion(object);
                                questionLabel.setText(object.getString("content"));
                                setQuestionOptions(object);
                            }
                        }
                    });
                }
            }
        });
    }

    private void setQuestionOptions(ParseObject question) {
        final ParseObject q = question;
        ParseObject qt = question.getParseObject("questionType");
        qt.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    questionType = parseObject.getString("name");
                    if (questionType.equals("Yes/No")) {
                        fragments = new ArrayList<>();
                        fragments.add(ResponseFragment.newInstance("Yes", colors.get(0)));
                        fragments.add(ResponseFragment.newInstance("Swipe to choose", colors.get(2)));
                        fragments.add(ResponseFragment.newInstance("No", colors.get(1)));

                        mPager = (ViewPager) findViewById(R.id.pager);
                        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                        mPager.setAdapter(mPagerAdapter);
                        mPager.setCurrentItem(1);
                        canAnswer = true;
                        setAdapterListener();
                    } else if (questionType.equals("Single Choice")) {
                        fragments = new ArrayList<>();
                        ParseQuery choicesQuery = new ParseQuery("Choice");
                        choicesQuery.whereEqualTo("question", q);
                        choicesQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    for (int i = 0; i < list.size(); i++) {
                                        ParseObject o = list.get(i);
                                        choices[i] = o;
                                        String content = o.getString("content");
                                        fragments.add(ResponseFragment.newInstance(content, colors.get(i)));
                                    }
                                    fragments.add(1, ResponseFragment.newInstance("Swipe to choose", colors.get(2)));

                                    mPager = (ViewPager) findViewById(R.id.pager);
                                    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                                    mPager.setAdapter(mPagerAdapter);
                                    mPager.setCurrentItem(1);
                                    canAnswer = true;
                                    setAdapterListener();
                                } else {
                                    finish();
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else if (questionType.equals("Text")) {
                        responseText.setVisibility(View.VISIBLE);
                        responseText.setEnabled(true);
                        responseText.requestFocus();
                    }
                }
            }
        });

    }

    public void setAdapterListener() {
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (canAnswer) {
                    if (questionType.equals("Single Choice")) {
                        response.setChoice(choices[position]);
                    } else if (questionType.equals("Yes/No")) {
                        if (position == 0) {
                            response.setYesNo(true);
                        } else {
                            response.setYesNo(false);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE && !answered) {
                    int cPage = mPager.getCurrentItem();
                    fragments.remove(1);
                    answered = true;
                    mPagerAdapter.notifyDataSetChanged();
                    mPager.setAdapter(mPagerAdapter);
                    if (cPage != 0) {
                        mPager.setCurrentItem(1);
                    }
                }
            }
        });
    }

    private void saveResponse() {
        response.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent g = getIntent();
                    final String responseSetId = g.getStringExtra("response_set_id");
                    final String surveyId = g.getStringExtra("survey_id");
                    final int[] questionOrder = new int[1];
                    questionOrder[0] = g.getIntExtra("question_order", 1);
                    final int questionCount = g.getIntExtra("question_count", 0);
                    if (questionOrder[0] < questionCount) {
                        setResult(RESULT_OK);
                        Intent i = new Intent(ResponseActivity.this, ResponseActivity.class);
                        i.putExtra("response_set_id", responseSetId);
                        i.putExtra("survey_id", surveyId);
                        i.putExtra("question_order", questionOrder[0] + 1);
                        i.putExtra("question_count", questionCount);
                        startActivity(i);
                        finish();
                    } else {
                        ParseQuery responseSetQuery = new ParseQuery("ResponseSet");
                        responseSetQuery.whereEqualTo(
                                "objectId",
                                responseSetId
                        );
                        responseSetQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {
                                if (object == null) {
                                    finish();
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    object.put("complete", true);
                                    object.saveEventually();
                                    Toast.makeText(
                                            getApplicationContext(),
                                            R.string.response_set_complete,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        finish();
                    }
                }
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
