package real_estate_tracker.realestatetrackerandroid;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sanhar on 2017-03-20.
 */

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.MyViewHolder> {

    private List<PropertyObject> mPropertiesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView area, bedroom, price;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            area = (TextView) view.findViewById(R.id.area);
            bedroom = (TextView) view.findViewById(R.id.bedroom);
            price = (TextView) view.findViewById(R.id.price);
            image = (ImageView) view.findViewById(R.id.image);
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
        holder.area.setText(propertyObject.getArea());
        holder.bedroom.setText(propertyObject.getBedroom());
        holder.price.setText(propertyObject.getPrice());
        if (holder.image != null) {
            new ImageDownloaderTask(holder.image).execute(propertyObject.getUrl());
        }
    }

    @Override
    public int getItemCount() {
        return mPropertiesList.size();
    }
}