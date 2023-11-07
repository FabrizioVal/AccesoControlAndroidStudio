package com.example.accesocontrolmongodb;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class PlanillasActivity extends AppCompatActivity {

    private EditText editTextField1, editTextField2, editTextField3;
    private Button buttonSubmitPlanilla, buttonGoToCommands;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planillas);

        editTextField1 = findViewById(R.id.edit_text_username);
        editTextField2 = findViewById(R.id.edit_text_password);
        editTextField3 = findViewById(R.id.edit_text_additionaldata);
        buttonSubmitPlanilla = findViewById(R.id.button_submit_planilla);

        buttonGoToCommands = findViewById(R.id.GoToComandos);

        buttonSubmitPlanilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input from EditText widgets
                String email = editTextField1.getText().toString();
                String password = editTextField2.getText().toString();
                String additionalData = editTextField3.getText().toString();

                // Create a data object for "Planillas" data
                PlanillaData planillaData = new PlanillaData(email, password, additionalData);

                // Send "Planillas" data to the server
                sendPlanillaDataToServer(planillaData); // estas llamando una funcion para enviar otra
            }
        });

        buttonGoToCommands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PlanillasActivity.this, CommandActivity.class);
                startActivity(intent);

            }
        });

    }

    public class PlanillaData {
        private String email;
        private String password;
        private String additionalData;

        public PlanillaData(String email, String password, String additionalData) {
            this.email = email;
            this.password = password;
            this.additionalData = additionalData;
        }
    }

    private void sendPlanillaDataToServer(PlanillaData planillaData) {
        RetrofitInstance.ApiService apiService = RetrofitInstance.getRetrofitInstance().create(RetrofitInstance.ApiService.class);
        //Llamo a apiservice que esta en retrofitclientinstance

        Call<RetrofitInstance.ApiResponse> call = apiService.uploadForm(planillaData); //Llamo a apiresponse que esta en retrofitclientinstance

        // Call<RetrofitInstance.ApiResponse> call = apiService.signIn(new SignInRequest(email, password))

        call.enqueue(new Callback<RetrofitInstance.ApiResponse>() {
            @Override
            public void onResponse(Call<RetrofitInstance.ApiResponse> call, Response<RetrofitInstance.ApiResponse> response) {
                int httpStatusCode = response.code();

                if (httpStatusCode == 200) {
                    RetrofitInstance.ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.isSuccess()) {
                        // Handle success scenario
                        Log.d(TAG, "Planilla data uploaded successfully");
                        Toast.makeText(PlanillasActivity.this, "Planilla data uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        // Handle the case where the server reports success as false
                        Log.d(TAG, "Planilla data upload failed");
                        Toast.makeText(PlanillasActivity.this, "Planilla data upload failed", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle other HTTP errors
                    Log.d(TAG, "HTTP error: " + httpStatusCode);
                    Toast.makeText(PlanillasActivity.this, "HTTP error: " + httpStatusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RetrofitInstance.ApiResponse> call, Throwable t) {
                // Handle network or other errors
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(PlanillasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
