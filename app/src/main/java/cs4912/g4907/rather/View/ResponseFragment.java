package cs4912.g4907.rather.View;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cs4912.g4907.rather.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResponseFragment extends Fragment {

    public ResponseFragment() {
        // Required empty public constructor
    }

    public static final ResponseFragment newInstance(String message, String colorHex) {
        ResponseFragment f = new ResponseFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString("content", message);
        bdl.putString("color", colorHex);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_response, container, false);
        TextView responseLabel = (TextView) rootView.findViewById(R.id.response_label);
        responseLabel.setText(getArguments().getString("content"));
        responseLabel.setTextSize(25);
        responseLabel.setTextColor(Color.parseColor("#FFFFFF"));
        int color = Color.parseColor(getArguments().getString("color"));
        rootView.setBackgroundColor(color);
        return rootView;
    }

}
