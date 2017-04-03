package real_estate_tracker.realestatetrackerandroid;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sanhar on 2017-03-20.
 */

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.MyViewHolder> {

    private List<PropertyObject> mPropertiesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mAddress, mPrice, mProjectedIncrease;
        public ImageView mImage;

        public MyViewHolder(View view) {
            super(view);
            mAddress = (TextView) view.findViewById(R.id.address);
            mPrice = (TextView) view.findViewById(R.id.price);
            mProjectedIncrease = (TextView) view.findViewById(R.id.projected_increase);
            mImage = (ImageView) view.findViewById(R.id.image);
        }
    }


    public PropertyAdapter(List<PropertyObject> propertiesList) {
        this.mPropertiesList = propertiesList;
    }

    @Override
    public PropertyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_list_row, parent, false);

        return new PropertyAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PropertyAdapter.MyViewHolder holder, int position) {
        PropertyObject propertyObject = mPropertiesList.get(position);
        holder.mAddress.setText(propertyObject.getAddress());
        holder.mPrice.setText(propertyObject.getPrice());
        holder.mProjectedIncrease.setText(propertyObject.getProjectedIncrease().toString() + "%");
        if (holder.mImage != null) {
            new ImageDownloaderTask(holder.mImage).execute(propertyObject.getUrlThumbnail());
        }
    }

    @Override
    public int getItemCount() {
        return mPropertiesList.size();
    }

    public void swap(List<PropertyObject> propertiesList){
        mPropertiesList.clear();
        mPropertiesList.addAll(propertiesList);
        notifyDataSetChanged();
    }
}