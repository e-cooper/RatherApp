package cs4912.g4907.rather.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Eli on 4/8/2015.
 */
@ParseClassName("Survey")
public class Survey extends ParseObject {

    public Survey() {
        // A default constructor is required.
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser user) {
        put("author", user);
    }

    public Boolean getPrivacy() {
        return getBoolean("public");
    }

    public void setPrivacy(Boolean privacy) {
        put("public", privacy);
    }

    public String getPassword() {
        return getString("password");
    }

    public void setPassword(String password) {
        put("password", password);
    }

    public Boolean getPublished() {
        return getBoolean("published");
    }

    public void setPublished(Boolean published) {
        put("published", published);
    }

    public Date getPublicationDate() {
        return getDate("publicationDate");
    }

    public void setPublicationDate(Date date) {
        put("publicationDate", date);
    }

    public Date getExpirationDate() {
        return getDate("expirationDate");
    }

    public void setExpirationDate(Date date) {
        put("expirationDate", date);
    }

    public ParseQuery<ParseObject> getQuestions() {
        ParseQuery<ParseObject> questionsQuery = ParseQuery.getQuery("Question");
        return questionsQuery.whereEqualTo("survey", this);
    }

}
