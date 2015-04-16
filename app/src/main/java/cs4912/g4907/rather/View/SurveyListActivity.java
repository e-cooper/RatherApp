package cs4912.g4907.rather.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Date;

import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

/**
 * Created by Eli on 4/4/2015.
 */
public class SurveyListActivity extends Activity {
    private ParseQueryAdapter<Survey> mainAdapter;
    private ParseQueryAdapter<Survey> mySurveysAdapter;
    private ParseQueryAdapter<Survey> availableSurveysAdapter;
    private Menu myMenu;
    private String adapterFlag;
    private ListView listView1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        setContentView(R.layout.main);
        listView1 = (ListView)findViewById(R.id.SurveyListView);
        listView1.setClickable(true);
//        listView1.setText

        availableSurveysAdapter = new ParseQueryAdapter<>(this, new ParseQueryAdapter.QueryFactory<Survey>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Survey");
                query.whereEqualTo("published", true);
                query.whereGreaterThanOrEqualTo("expirationDate", new Date());
                return query;
            }
        });
        availableSurveysAdapter.setTextKey("title");

        ParseQuery query = new ParseQuery("Survey");
        query.whereEqualTo("author", ParseUser.getCurrentUser());

        mySurveysAdapter = new ParseQueryAdapter<>(this, new ParseQueryAdapter.QueryFactory<Survey>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Survey");
                query.whereEqualTo("author", ParseUser.getCurrentUser());
                return query;
            }
//            public View getView(int position, View convertView, ViewGroup parent) {
//                if (convertView == null) {
//                    // Inflate your view
//                    LayoutInflater inflater = getLayoutInflater();
//                    convertView = inflater.inflate(R.layout.row, parent, false);
//                } else {
//                }
//                TextView surveyLabel = (TextView)convertView.findViewById(R.id.Survey_List_Row);
//                surveyLabel.setText();
//                return convertView;
//            }
        });
        mySurveysAdapter.setTextKey("title");

        displayAvailableSurveys();
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick ( AdapterView<?> l, View v, int position, long id) {

                final Survey survey = mainAdapter.getItem(position);
                if(!survey.getPrivacy()) {
                    LayoutInflater li = LayoutInflater.from(SurveyListActivity.this);
                    View promptsView = li.inflate(R.layout.private_survey_password_prompt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SurveyListActivity.this);

                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(userInput.getText().toString().equals(survey.getPassword())){
                                                Intent i = new Intent(SurveyListActivity.this, SurveyDetailsActivity.class);
                                                i.putExtra("survey_id", survey.getObjectId());
                                                addAdapterInfo(i);
                                                startActivityForResult(i, 1);
                                            }
                                            else{
                                                Toast.makeText(SurveyListActivity.this, "Sorry, that's the wrong password" , Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
                else {
                    Intent i = new Intent(SurveyListActivity.this, SurveyDetailsActivity.class);
                    i.putExtra("survey_id", survey.getObjectId());
                    addAdapterInfo(i);
                    startActivityForResult(i, 1);
                }
            }
        });
//        listView1.performItemClick(listView1.getAdapter()
//                .getView(1, null, null), 0, listView1.getAdapter().getItemId(1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_survey_list, menu);
        myMenu = menu;
        if(adapterFlag.equals("available_surveys")){
            myMenu.findItem(R.id.action_available_surveys).setVisible(false);
            myMenu.findItem(R.id.action_available_surveys).setEnabled(false);
            myMenu.findItem(R.id.action_my_surveys).setVisible(true);
            myMenu.findItem(R.id.action_my_surveys).setEnabled(true);
        }
        else if(adapterFlag.equals("my_surveys")){
            myMenu.findItem(R.id.action_available_surveys).setVisible(true);
            myMenu.findItem(R.id.action_available_surveys).setEnabled(true);
            myMenu.findItem(R.id.action_my_surveys).setVisible(false);
            myMenu.findItem(R.id.action_my_surveys).setEnabled(false);
        }
        return true;
    }

    /*
	 * Posting surveys and refreshing the list will be controlled from the Action
	 * Bar.
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_refresh: {
                updateSurveyList();
                break;
            }

            case R.id.action_profile: {
                viewProfile();
                break;
            }

            case R.id.action_new: {
                newSurvey();
                break;
            }

            case R.id.action_available_surveys: {
                displayAvailableSurveys();
                break;
            }

            case R.id.action_my_surveys: {
                displayMySurveys();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSurveyList() {
        mainAdapter.loadObjects();
        listView1.setAdapter(mainAdapter);
    }

    private void newSurvey() {
        Intent i = new Intent(this, NewSurveyActivity.class);
        startActivity(i);
    }

    private void viewProfile() {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    private void displayAvailableSurveys() {
        adapterFlag = "available_surveys";
        setTitle(R.string.action_available_surveys);
        mainAdapter = availableSurveysAdapter;
        mainAdapter.loadObjects();
        listView1.setAdapter(mainAdapter);
        if(myMenu!=null) {
            myMenu.findItem(R.id.action_available_surveys).setVisible(false);
            myMenu.findItem(R.id.action_available_surveys).setEnabled(false);
            myMenu.findItem(R.id.action_my_surveys).setVisible(true);
            myMenu.findItem(R.id.action_my_surveys).setEnabled(true);
        }
    }

    private void displayMySurveys() {
        adapterFlag = "my_surveys";
        setTitle(R.string.action_my_surveys);
        mainAdapter = mySurveysAdapter;
        mainAdapter.loadObjects();
        listView1.setAdapter(mainAdapter);
        //to disable my surveys button in menu
        if(myMenu!=null) {
            myMenu.findItem(R.id.action_available_surveys).setVisible(true);
            myMenu.findItem(R.id.action_available_surveys).setEnabled(true);
            myMenu.findItem(R.id.action_my_surveys).setVisible(false);
            myMenu.findItem(R.id.action_my_surveys).setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if(resultCode == RESULT_OK){
                String adapterInfo = data.getStringExtra("adapter_info");
                if(adapterInfo.equals("available_surveys")){
                    displayAvailableSurveys();
                }
                else if(adapterInfo.equals("my_surveys")){
                    displayMySurveys();
                }
            }
        }

        //This doesn't seem to be working
        if (resultCode == Activity.RESULT_OK) {
            // If a new survey has been added, update
            // the list of surveys
            updateSurveyList();
        }
    }

    // this method helps ensure that returning from survey details to survey list is seamless,
    // i.e, if I return from details of my survey I and brought back to a list of my surveys
    // as opposed to available surveys
    private void addAdapterInfo(Intent intent){
        intent.putExtra("adapter_info",adapterFlag);
    }
}
