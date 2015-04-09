package cs4912.g4907.rather.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Eli on 4/8/2015.
 */
@ParseClassName("Question")
public class Question extends ParseObject {

    public Question() {
        // A default constructor is required.
    }

    public String getContent() {
        return getString("content");
    }

    public void setContent(String content) {
        put("content", content);
    }

    public void setSurvey(String surveyID) {
        put("parent", surveyID);
    }

}
