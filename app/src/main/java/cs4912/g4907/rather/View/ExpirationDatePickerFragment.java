package cs4912.g4907.rather.View;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import cs4912.g4907.rather.View.NewSurveyActivity;

import java.util.Calendar;

import cs4912.g4907.rather.View.NewSurveyActivity;

/**
 * Created by Amit on 4/14/2015.
 */
public class ExpirationDatePickerFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog expirationPickerDialog = new DatePickerDialog(getActivity(), (NewSurveyActivity)getActivity(), year, month, day);
        expirationPickerDialog.setTitle("Pick expiration date");
        return expirationPickerDialog;
    }

//    public void onDateSet(DatePicker view, int year, int month, int day) {
//        // Do something with the date chosen by the user
//    }
}