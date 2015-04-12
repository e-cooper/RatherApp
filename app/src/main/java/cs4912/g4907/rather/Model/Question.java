package cs4912.g4907.rather.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Eli on 4/9/2015.
 */
@ParseClassName("Question")
public class Question extends ParseObject {

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

    public ParseObject getQuestionType() {
        return getParseObject("questionType");
    }

    public void setQuestionType(ParseObject questionType) {
        put("questionType", questionType);
    }

    public ParseObject getSurvey() {
        return getParseObject("survey");
    }

    public void setSurvey(ParseObject survey) {
        put("survey", survey);
    }
}
