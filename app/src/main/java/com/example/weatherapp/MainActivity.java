package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final String CITY = BuildConfig.CITY;
    private final String API = BuildConfig.API_KEY;

    private ProgressBar loader;
    private RelativeLayout mainContainer;
    private TextView errorText;
    private TextView updatedAt;
    private TextView status;
    private TextView temp;
    private TextView wind;
    private TextView pressure;
    private TextView humidity;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loader = findViewById(R.id.loader);
        mainContainer = findViewById(R.id.mainContainer);
        errorText = findViewById(R.id.errorText);
        updatedAt = findViewById(R.id.updated_at);
        status = findViewById(R.id.status);
        temp = findViewById(R.id.temp);
        wind = findViewById(R.id.wind);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);

        TextView address = findViewById(R.id.address);
        address.setText(CITY);

        loader.setVisibility(View.VISIBLE);
        mainContainer.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);

        new WeatherTask().execute();

    }

    private class WeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("http://api.weatherapi.com/v1/current.json?" + "key=" + API + "&q=" + CITY);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Call call = client.newCall(request);
                Response response = call.execute();

                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                loader.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONObject currentWeather = jsonObj.getJSONObject("current");

                String updatedAt = currentWeather.getString("last_updated");
                String temp = currentWeather.getString("temp_c") + "°C";
                String pressure = currentWeather.getString("pressure_mb");
                String humidity = currentWeather.getString("humidity");
                String windSpeed = currentWeather.getString("wind_kph");

                String weatherDescription = currentWeather.getJSONObject("condition").getString("text");

                MainActivity.this.updatedAt.setText(updatedAt);
                MainActivity.this.status.setText(weatherDescription);
                MainActivity.this.temp.setText(temp);
                MainActivity.this.wind.setText(windSpeed);
                MainActivity.this.pressure.setText(pressure);
                MainActivity.this.humidity.setText(humidity);

                loader.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                loader.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
            }
        }
    }
}
