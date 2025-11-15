package com.example.tacopaco.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tacopaco.R;
import com.example.tacopaco.clases.Mesa;
import com.example.tacopaco.servicios.Api;
import com.example.tacopaco.servicios.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EleccionMesa extends AppCompatActivity {

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.eleccion_mesa);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.eleccionmesa), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instancia de la API
        Api api = RetrofitClient.getInstance().getApi();


        // Referencias a los botones del xml
        Button boton_volver = findViewById(R.id.volver);
        Button btn_actualizar = findViewById(R.id.actualizar);

        // Actualizar el activity
        btn_actualizar.setOnClickListener(v -> recreate());

        // Volver al activity anterior
        boton_volver.setOnClickListener(v -> {
            Intent intent = new Intent(EleccionMesa.this, MainActivity.class);
            startActivity(intent);
        });

        // Lista de botones de mesas
        ArrayList<Button> BOTONES = new ArrayList<>();
        Button boton_mesa1 = findViewById(R.id.mesa1);
        Button boton_mesa2 = findViewById(R.id.mesa2);
        Button boton_mesa3 = findViewById(R.id.mesa3);
        Button boton_mesa4 = findViewById(R.id.mesa4);
        Button boton_mesa5 = findViewById(R.id.mesa5);

        BOTONES.add(boton_mesa1);
        BOTONES.add(boton_mesa2);
        BOTONES.add(boton_mesa3);
        BOTONES.add(boton_mesa4);
        BOTONES.add(boton_mesa5);

        // Obtener mesas de la base de datos de Mongo Atlas
        Call<List<Mesa>> call = api.getMesas();

        // Interfaz gráfica de los botones según el estado de las mesas
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Mesa>> call, Response<List<Mesa>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Mesa> mesas = response.body();

                    for (int i = 0; i < mesas.size() && i < BOTONES.size(); i++) {
                        Mesa mesa = mesas.get(i);
                        Button boton = BOTONES.get(i);

                        // Nombre de la mesa tal cual viene en la base de datos (Ej: "Mesa1")
                        String nombreMesa = mesa.getNombre();

                        // Formatea el nombre para poner un espacio
                        String nombreFormateado = nombreMesa.replaceAll("(?i)^(mesa)\\s*(\\d+)$", "$1 $2")
                                .replaceAll("(?<=\\D)(?=\\d)", " ");

                        boton.setText(nombreFormateado); // enseña en la UI el formateado
                        boton.setTag(nombreMesa); // guarda original para la API


                        if (mesa.isOcupada()) {
                            boton.setBackground(getResources().getDrawable(R.drawable.mesa_ocupada));
                            boton.setTextColor(getResources().getColor(R.color.white));
                            boton.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Mesa>> call, Throwable t) {
                t.printStackTrace();
            }
        });


        for (Button boton : BOTONES) {
            boton.setOnClickListener(v -> {
                String nombreMesaParaApi = (String) boton.getTag(); // nombre original ("Mesa1")
                Mesa nuevaMesa = new Mesa(nombreMesaParaApi, true, false);

                api.ocuparMesa(nombreMesaParaApi, nuevaMesa).enqueue(new Callback<Mesa>() {
                    @Override
                    public void onResponse(Call<Mesa> call, Response<Mesa> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(EleccionMesa.this, Carta.class);
                            intent.putExtra("nombreMesa", nombreMesaParaApi);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Mesa> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            });
        }

    }

}
