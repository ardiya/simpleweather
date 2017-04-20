package com.ardiya.simpleweather.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardiya.simpleweather.R;
import com.ardiya.simpleweather.util.OpenWeatherSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ardiya on 4/20/2017.
 */
public class WeatherRAdapter extends RecyclerView.Adapter<WeatherRAdapter.ListItemViewHolder> {

    ArrayList<WeatherModel> weatherModelArrayList;
    public WeatherRAdapter(ArrayList<WeatherModel> weatherModelArrayList){
        this.weatherModelArrayList = weatherModelArrayList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.weekly_weather_item, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        WeatherModel model = weatherModelArrayList.get(position);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        holder.ivWeather.setImageResource(OpenWeatherSingleton.getInstance()
                .getIconFromWeather(model.getWeather()));
        holder.textDate.setText(format.format(model.getDate()));
        holder.textTemp.setText(String.format("%.0f°C",
                Double.parseDouble(model.getTemp())));
    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList.size();
    }

    public class ListItemViewHolder  extends RecyclerView.ViewHolder{
        @BindView(R.id.textDate)
        TextView textDate;

        @BindView(R.id.ivWeather)
        ImageView ivWeather;

        @BindView(R.id.textTemp)
        TextView textTemp;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
