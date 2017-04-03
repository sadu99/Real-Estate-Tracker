package real_estate_tracker.realestatetrackerandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Sanhar on 2017-03-20.
 */

public class SearchFragment extends DialogFragment {
    private EditText mArea,  mPrice, mBedrooms, mProjectedYears;
    private Spinner mFurnished;
    Context context;
    private static final String[] FURNISHED_ITEMS = {"all","furnished", "unfurnished", "part-furnished"};
    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface Listener {
        void onDialogPositiveClick(DialogInterface dialog, String area, String price, String bedroom,
                                   String furnished, String projectedYears);
        void onDialogNegativeClick(DialogInterface dialog);
    }

    // Use this instance of the interface to deliver action events
    Listener mListener;

    //    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.search_form,null);
        initializeView(layout);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(layout)
                // Add action buttons
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(dialog,mArea.getText().toString(),
                                mPrice.getText().toString(),mBedrooms.getText().toString(),
                                mFurnished.getSelectedItem().toString(),mProjectedYears.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(dialog);
                    }
                });

        builder.setCancelable(true);
        return builder.create();
    }

    private void initializeView(View layout){
        mArea = (EditText)layout.findViewById(R.id.area);
        mPrice = (EditText)layout.findViewById(R.id.price);
        mBedrooms = (EditText)layout.findViewById(R.id.bedroom);
        mFurnished = (Spinner)layout.findViewById(R.id.furnished);
        mProjectedYears = (EditText)layout.findViewById(R.id.projected_years);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,FURNISHED_ITEMS);
        mFurnished.setAdapter(adapter);
    }
}
