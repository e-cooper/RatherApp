package cs4912.g4907.rather.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Eli on 4/12/2015.
 */
@ParseClassName("ResponseSet")
public class ResponseSet extends ParseObject {
    
    public Boolean getComplete() {
        return getBoolean("complete");
    }

    public void setComplete(Boolean complete) {
        put("complete", complete);
    }

    public Date getDate() {
        return getDate("date");
    }

    public void setDate(Date date) {
        put("date", date);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseObject getSurvey() {
        return getParseObject("survey");
    }

    public void setSurvey(ParseObject survey) {
        put("survey", survey);
    }

}
