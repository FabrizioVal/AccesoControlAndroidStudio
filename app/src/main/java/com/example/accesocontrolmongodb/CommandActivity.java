package com.example.accesocontrolmongodb;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommandActivity extends AppCompatActivity {

    private EditText editTextSerialName;

    private Button btn_abrir, btn_cerrar, btnGoToPlanillas;

    // Retrieve email from SharedPreferences
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        editTextSerialName = findViewById(R.id.editTextSerialname);
        btn_abrir = findViewById(R.id.Abrir);
        btn_cerrar = findViewById(R.id.Cerrar);
        btnGoToPlanillas = findViewById(R.id.GoToPlanillas);

    // Retrieve the email from SharedPreferences
    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
    String email = sharedPreferences.getString("user_email", null);

        // Set OnClickListener for the "Abrir" button
        btn_abrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the "Abrir" action here
                String action = "1: abrir";
                String serialName = editTextSerialName.getText().toString();

                // Call a method to post data to the server
                postDataToServer(email, action, serialName);
            }
        });

        // Set OnClickListener for the "Cerrar" button
        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the "Cerrar" action here
                String action = "0: cerrar";
                String serialName = editTextSerialName.getText().toString();

                // Call a method to post data to the server
                postDataToServer(email, action, serialName);
            }
        });

        btnGoToPlanillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CommandActivity.this, PlanillasActivity.class);
                startActivity(intent);

            }
        });

}

    // Implement a method to post data to the server using Retrofit (similar to your sign-in activity)
    private void postDataToServer(String email, String action, String serialname) {

        RetrofitInstance.ApiService apiService = RetrofitInstance.getRetrofitInstance().create(RetrofitInstance.ApiService.class);

        Call<RetrofitInstance.ApiResponse> call = apiService.uploadCommand(new CommandActivity.CommandRequest(email, serialname, action));

        call.enqueue(new Callback<RetrofitInstance.ApiResponse>() {
            @Override
            public void onResponse(Call<RetrofitInstance.ApiResponse> call, Response<RetrofitInstance.ApiResponse> response) {

                int httpStatusCode = response.code();

                if (httpStatusCode == 200) {

                    // Lockreport done successfully
                    RetrofitInstance.ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.isSuccess()) { // Both factors are checked for added security.
                        // Handle the success scenario
                        Log.d(TAG, "LockReport successful");
                        Toast.makeText(CommandActivity.this, "Action successful", Toast.LENGTH_LONG).show();

                    } else {
                        // Handle the case where the server reports success as false
                        Log.d(TAG, "LockReport not successful");
                        Toast.makeText(CommandActivity.this, "Action not successful", Toast.LENGTH_LONG).show();
                    }
                } else if (httpStatusCode == 500) {
                    // Handle 500 (Internal Server Error) or other server errors
                    Log.d(TAG, "Server error");
                    Toast.makeText(CommandActivity.this, "Server error", Toast.LENGTH_LONG).show();
                } else {
                    // Handle other HTTP errors
                    Log.d(TAG, "HTTP error: " + httpStatusCode);
                    Toast.makeText(CommandActivity.this, "HTTP error: " + httpStatusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RetrofitInstance.ApiResponse> call, Throwable t) {
                // Handle network or other errors
                Toast.makeText(CommandActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

//Models

    public class CommandRequest {
        private String email;

        private String serialname;

        private String action;

        public CommandRequest(String email, String serialname, String action) {

            this.email = email;
            this.serialname = serialname;
            this.action = action;
        }
    }



}