package cs4912.g4907.rather.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Eli on 4/12/2015.
 */
@ParseClassName("Choice")
public class Choice extends ParseObject {

    public ParseObject getQuestion() {
        return getParseObject("question");
    }

    public void setQuestion(ParseObject question) {
        put("question", question);
    }

    public String getContent() {
        return getString("content");
    }

    public void setContent(String content) {
        put("content", content);
    }

    public Number getOrder() {
        return getNumber("order");
    }

    public void setOrder(Number order) {
        put("order", order);
    }

}
