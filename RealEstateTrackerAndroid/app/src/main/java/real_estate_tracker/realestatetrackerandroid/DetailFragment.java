package real_estate_tracker.realestatetrackerandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.Serializable;

/**
 * Created by Sanhar on 2017-03-27.
 */

public class DetailFragment extends DialogFragment implements Serializable {
    public interface Listener {
        void onFavouriteClick(DialogInterface dialog);
    }

    // Use this instance of the interface to deliver action events
    Listener mListener;
    private TextView mCategory,mPrice,mBedrooms,mBathrooms,mDescription,mTitle;
    private ImageView mImage,mImageCategory,mImagePrice,mImageBath,mImageBedroom;
    private ToggleButton mFavouriteButton;

    public static DetailFragment newInstance(PropertyObject property) {
        DetailFragment frag = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("Property",property);
        frag.setArguments(args);
        return frag;
    }


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
        PropertyObject property = (PropertyObject) getArguments().getSerializable("Property");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.detailed_form,null);
        initText(property,layout);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(layout);

        builder.setCancelable(true);
        return builder.create();
    }

    private void initText(PropertyObject property, View layout){
        mCategory = (TextView) layout.findViewById(R.id.category);
        mPrice = (TextView)layout.findViewById(R.id.price);
        mBedrooms = (TextView)layout.findViewById(R.id.bedroom);
        mBathrooms = (TextView)layout.findViewById(R.id.bath);
        mDescription = (TextView)layout.findViewById(R.id.description);
        mTitle = (TextView)layout.findViewById(R.id.title);

        mCategory.setText(property.getCategory());
        mPrice.setText(property.getPrice());
        mBedrooms.setText(property.getBedrooms());
        mBathrooms.setText(property.getBathrooms());
        mDescription.setText(property.getDescription());
        mTitle.setText(property.getAddress());

        mImage = (ImageView) layout.findViewById(R.id.image);
        mImageCategory = (ImageView) layout.findViewById(R.id.image_category);
        mImageBath = (ImageView) layout.findViewById(R.id.image_bath);
        mImageBedroom = (ImageView) layout.findViewById(R.id.image_bedroom);
        mImagePrice = (ImageView) layout.findViewById(R.id.image_price);
        mImagePrice.setImageDrawable(getResources().getDrawable(R.drawable.ic_attach_money_black_24dp));
        mImageCategory.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_black_24dp));
        mImageBath.setImageDrawable(getResources().getDrawable(R.drawable.ic_hot_tub_black_24dp));
        mImageBedroom.setImageDrawable(getResources().getDrawable(R.drawable.ic_airline_seat_individual_suite_black_24dp));


        new ImageDownloaderTask(mImage).execute(property.getUrl());
        mFavouriteButton = (ToggleButton) layout.findViewById(R.id.favourite_button);
        mFavouriteButton.setChecked(false);
        Drawable star = getResources().getDrawable(R.drawable.ic_star_black_24dp);
        star.setColorFilter(getResources().getColor(R.color.year), PorterDuff.Mode.SRC_IN);
        mFavouriteButton.setBackgroundDrawable(star);
        mFavouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Drawable star = getResources().getDrawable(R.drawable.ic_star_black_24dp);
                if (isChecked) {
                    star.setColorFilter(getResources().getColor(R.color.year), PorterDuff.Mode.SRC_IN);
                    mFavouriteButton.setBackgroundDrawable(star);
                }else{
                    star.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                    mFavouriteButton.setBackgroundDrawable(star);
                }
            }
        });
    }
}
