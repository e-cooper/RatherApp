package cs4912.g4907.rather.View;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

import cs4912.g4907.rather.Model.Choice;
import cs4912.g4907.rather.Model.Question;
import cs4912.g4907.rather.Model.QuestionType;
import cs4912.g4907.rather.Model.Response;
import cs4912.g4907.rather.Model.ResponseSet;
import cs4912.g4907.rather.Model.Survey;
import cs4912.g4907.rather.R;

/**
 * Created by Amit on 4/8/2015.
 */
public class RatherApp extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        ParseObject.registerSubclass(Survey.class);
        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(QuestionType.class);
        ParseObject.registerSubclass(Choice.class);
        ParseObject.registerSubclass(Response.class);
        ParseObject.registerSubclass(ResponseSet.class);
        Parse.initialize(this, getString(R.string.parse_app_id) , getString(R.string.parse_client_key));
    }
}
