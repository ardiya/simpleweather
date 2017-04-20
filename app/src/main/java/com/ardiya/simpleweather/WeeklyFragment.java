package com.ardiya.simpleweather;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ardiya.simpleweather.model.WeatherModel;
import com.ardiya.simpleweather.model.WeatherRAdapter;
import com.ardiya.simpleweather.util.OpenWeatherSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeeklyFragment extends Fragment {

    private static final String ARG_CITY = "param1";

    private String mCity;

    @BindView(R.id.weekly_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.textCity)
    TextView textCity;

    @BindView(R.id.viewWeather)
    RecyclerView viewWeather;

    public WeeklyFragment() {
        // Required empty public constructor
    }

    public static WeeklyFragment newInstance(String location) {
        WeeklyFragment fragment = new WeeklyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, location);
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
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        ButterKnife.bind(this, view);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        refreshData();
        return view;
    }

    private void refreshData(){
        mRefreshLayout.setRefreshing(true);
        Call apiCall = OpenWeatherSingleton.getInstance().getForecastByCity(mCity);
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
                                textCity.setText(obj.getJSONObject("city").getString("name"));
                                JSONArray weathers = obj.getJSONArray("list");
                                ArrayList<WeatherModel> weatherModelArrayList = new ArrayList<WeatherModel>();
                                for(int i = 0; i<weathers.length(); i++){
                                    JSONObject weather = weathers.getJSONObject(i);

                                    String objWeather = weather.getJSONArray("weather").getJSONObject(0).getString("main");
                                    String objTemp = weather.getJSONObject("temp").getString("day");
                                    long objDateMS = weather.getInt("dt");
                                    Date objDate = new Date(objDateMS * 1000); // Convert OpenWeather s to Java's millisecond

                                    weatherModelArrayList.add(new WeatherModel(objWeather, objTemp, objDate));
                                }

                                WeatherRAdapter weatherAdapter = new WeatherRAdapter(weatherModelArrayList);
                                viewWeather.setLayoutManager(new LinearLayoutManager(getActivity(),
                                        LinearLayoutManager.HORIZONTAL, false));
                                viewWeather.setAdapter(weatherAdapter);

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
