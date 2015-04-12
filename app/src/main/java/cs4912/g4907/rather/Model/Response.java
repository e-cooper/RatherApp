package cs4912.g4907.rather.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by Eli on 4/12/2015.
 */
@ParseClassName("Response")
public class Response extends ParseObject {

    public ParseObject getResponseSet() {
        return getParseObject("responseSet");
    }

    public void setResponseSet(ParseObject responseSet) {
        put("responseSet", responseSet);
    }

    public ParseObject getQuestion() {
        return getParseObject("question");
    }

    public void setQuestion(ParseObject question) {
        put("question", question);
    }

    public ParseObject getChoice() {
        return getParseObject("choice");
    }

    public void setChoice(ParseObject choice) {
        put("choice", choice);
    }

    public Date getDate() {
        return getDate("date");
    }

    public void setDate(Date date) {
        put("date", date);
    }

    public Boolean getYesNo() {
        return getBoolean("yesNo");
    }

    public void setYesNo(Boolean yesNo) {
        put("yesNo", yesNo);
    }

    public String getText() {
        return getString("text");
    }

    public void setText(String text) {
        put("text", text);
    }

}
