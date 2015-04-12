package cs4912.g4907.rather.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Eli on 4/12/2015.
 */
@ParseClassName("QuestionType")
public class QuestionType extends ParseObject {

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public Boolean getHasChoices() {
        return getBoolean("hasChoices");
    }

    public void setHasChoices(Boolean hasChoices) {
        put("hasChoices", hasChoices);
    }

}
