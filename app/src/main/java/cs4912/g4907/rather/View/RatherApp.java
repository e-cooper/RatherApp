package cs4912.g4907.rather.View;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

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
        Parse.initialize(this, getString(R.string.parse_app_id) , getString(R.string.parse_client_key));
    }
}
