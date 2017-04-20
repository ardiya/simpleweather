package com.ardiya.simpleweather;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardiya.simpleweather.model.WeatherModel;
import com.ardiya.simpleweather.util.OpenWeatherSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class DailyFragment extends Fragment {
private static final String ARG_CITY = "param1";

    private String mCity;

    @BindView(R.id.daily_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.textCity)
    TextView textCity;

    @BindView(R.id.ivWeather)
    ImageView ivWeather;

    @BindView(R.id.textTemp)
    TextView textTemp;

    @BindView(R.id.textTempMin)
    TextView textTempMin;

    @BindView(R.id.textTempMax)
    TextView textTempMax;

    @BindView(R.id.textTempSeparator)
    TextView textTempSeparator;

    public DailyFragment() {
        // Required empty public constructor
    }

    public static DailyFragment newInstance(String city) {
        DailyFragment fragment = new DailyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = getArguments().getString(ARG_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        ButterKnife.bind(this, view);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        refreshData();

        return view;
    }

    public void changeCity(String city){
        mCity = city;
        refreshData();
    }

    private void refreshData(){
        mRefreshLayout.setRefreshing(true);
        Call apiCall = OpenWeatherSingleton.getInstance().getCurrentWeatherByCity(mCity);
        apiCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SimpleWeather", Log.getStackTraceString(e));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.oops, Snackbar.LENGTH_SHORT).show();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                final String result = response.body().string();
                Log.d("SimpleWeather[Request]", result);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if(obj.getInt("cod") == 401){
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        R.string.oops, Snackbar.LENGTH_SHORT).show();
                                Log.e("SimpleWeather", obj.toString());

                            }else{
                                textCity.setText(obj.getString("name"));
                                String weather = obj.getJSONArray("weather").
                                        getJSONObject(0).getString("main");
                                ivWeather.setImageResource(OpenWeatherSingleton.getInstance()
                                        .getIconFromWeather(weather));
                                JSONObject temp = obj.getJSONObject("main");
                                textTemp.setText(String.format("%s°C", temp.getString("temp")));
                                textTempMax.setText(String.format("%s°C", temp.getString("temp_max")));
                                textTempMin.setText(String.format("%s°C", temp.getString("temp_min")));
                                textTempSeparator.setText("~");

                            }
                        } catch (JSONException e) {
                            Log.e("SimpleWeather", Log.getStackTraceString(e));
                        }
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

    }

}
