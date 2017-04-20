package com.ardiya.simpleweather.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ardiya on 4/20/2017.
 */
public class WeatherModel {
    String mWeather;
    String mTemp;
    Date mDate;

    public WeatherModel(String weather, String temp, Date date) {
        mWeather = weather;
        mTemp=temp;
        mDate = date;
    }

    String getWeather(){
        return mWeather;
    }
    String getTemp(){
        return mTemp;
    }
    Date getDate(){
        return mDate;
    }
}
