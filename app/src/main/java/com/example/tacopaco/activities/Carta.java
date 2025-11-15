package com.example.tacopaco.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tacopaco.R;
import com.example.tacopaco.clases.Mesa;
import com.example.tacopaco.clases.Pedido;
import com.example.tacopaco.servicios.Api;
import com.example.tacopaco.servicios.RetrofitClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Carta extends AppCompatActivity {

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.carta);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.carta), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener instancia de la API
        Api api = RetrofitClient.getInstance().getApi();

        // Obtener la mesa seleccionada
        String nombreMesa = getIntent().getStringExtra("nombreMesa");

        // Acumulador del total de la mesa, inicializado a 0; este se mantiene mientras el cliente está viendo la carta
        AtomicReference<Double> acumuladoMesa = new AtomicReference<>(0.0);

        // Botones de pedir y cancelar/pagar
        Button pedir = findViewById(R.id.pagar);
        Button btn_cancelar_pagar = findViewById(R.id.cancelar);

        // Precio del pedido actual
        TextView precio = findViewById(R.id.precio);

        // Precio acumulado del global de pedidos
        TextView totalAcumulado = findViewById(R.id.totalAcumulado);

        // Mostrar el total de pedidos acumulados en pantalla ( formateando decimales )
        totalAcumulado.setText(String.format("Total: %.2f €", acumuladoMesa.get()));

        // Precios de los productos
        double precioTacos = 7.99;
        double precioNachos = 6.99;
        double precioQuesadillas = 6.50;
        double precioTamales = 7.50;
        double precioBurritos = 9.99;


        // Botones de sumar y restar productos
        Button sumarTacos = findViewById(R.id.sumarTacos);
        Button sumarNachos = findViewById(R.id.sumarNachos);
        Button sumarQuesadillas = findViewById(R.id.sumarQuesadillas);
        Button sumarTamal = findViewById(R.id.sumarTamal);
        Button sumarBurrito = findViewById(R.id.sumarBurrito);

        Button restarTacos = findViewById(R.id.restarTacos);
        Button restarNachos = findViewById(R.id.restarNachos);
        Button restarQuesadillas = findViewById(R.id.restarQuesadillas);
        Button restarTamal = findViewById(R.id.restarTamal);
        Button restarBurrito = findViewById(R.id.restarBurrito);

        // Cantidad productos
        TextView cantidadTacos = findViewById(R.id.cantidadTacos);
        TextView cantidadNachos = findViewById(R.id.cantidadNachos);
        TextView cantidadQuesadillas = findViewById(R.id.cantidadQuesadillas);
        TextView cantidadTamal = findViewById(R.id.cantidadTamal);
        TextView cantidadBurrito = findViewById(R.id.cantidadBurrito);

        // Inicializar cantidades a 0
        cantidadTacos.setText("0");
        cantidadNachos.setText("0");
        cantidadQuesadillas.setText("0");
        cantidadTamal.setText("0");
        cantidadBurrito.setText("0");

        // Variables atómicas para las cantidades de cada producto
        AtomicInteger totalTacos = new AtomicInteger();
        AtomicInteger totalNachos = new AtomicInteger();
        AtomicInteger totalQuesadillas = new AtomicInteger();
        AtomicInteger totalTamales = new AtomicInteger();
        AtomicInteger totalBurritos = new AtomicInteger();

        /*
        * Variable atómica: Clase que permite manejar
        * variables entre varios hilos sin usar synchronized.
        *
        * El incremento es .getAndIncrement() y el decremento es .getAndDecrement()
        * */

        // Variable atómica para el total actual del pedido
        final AtomicReference<Double>[] totalActual = new AtomicReference[]{new AtomicReference<>(0.0)};

        // Runnable para actualizar el total del pedido
        // Runnable: Sirve para definir un bloque de código que se puede ejecutar en cualquier momento. (Análogo a una función)
        Runnable actualizarTotal = () -> {
            double total = (totalTacos.get() * precioTacos)
                    + (totalNachos.get() * precioNachos)
                    + (totalQuesadillas.get() * precioQuesadillas)
                    + (totalTamales.get() * precioTamales)
                    + (totalBurritos.get() * precioBurritos);

            // Muestra en pantalla el precio de lo que está costando el pedido actual
            totalActual[0].set(total);
            precio.setText(String.format("Precio: %.2f €", total));
        };

        // Tacos
        sumarTacos.setOnClickListener(v -> { // Añadir tacos al pedido, con máximo de 10
            if (totalTacos.get() < 10) {
                totalTacos.getAndIncrement();
                cantidadTacos.setText(String.valueOf(totalTacos.get()));
                actualizarTotal.run();
            } else {
                Toast.makeText(getApplicationContext(), "Cantidad máxima de tacos alcanzada", Toast.LENGTH_SHORT).show();
            }
        });
        restarTacos.setOnClickListener(v -> { // Restar tacos del pedido, mínimo de 0
            if (totalTacos.get() > 0) {
                totalTacos.getAndDecrement();
                cantidadTacos.setText(String.valueOf(totalTacos.get()));
                actualizarTotal.run();
            }
        });

        // Nachos
        sumarNachos.setOnClickListener(v -> {
            if (totalNachos.get() < 10) {
                totalNachos.getAndIncrement();
                cantidadNachos.setText(String.valueOf(totalNachos.get()));
                actualizarTotal.run();
            } else {
                Toast.makeText(getApplicationContext(), "Cantidad máxima de nachos alcanzada", Toast.LENGTH_SHORT).show();
            }
        });
        restarNachos.setOnClickListener(v -> {
            if (totalNachos.get() > 0) {
                totalNachos.getAndDecrement();
                cantidadNachos.setText(String.valueOf(totalNachos.get()));
                actualizarTotal.run();
            }
        });

        // Quesadillas
        sumarQuesadillas.setOnClickListener(v -> {
            if (totalQuesadillas.get() < 10) {
                totalQuesadillas.getAndIncrement();
                cantidadQuesadillas.setText(String.valueOf(totalQuesadillas.get()));
                actualizarTotal.run();
            } else {
                Toast.makeText(getApplicationContext(), "Cantidad máxima de quesadillas alcanzada", Toast.LENGTH_SHORT).show();
            }
        });
        restarQuesadillas.setOnClickListener(v -> {
            if (totalQuesadillas.get() > 0) {
                totalQuesadillas.getAndDecrement();
                cantidadQuesadillas.setText(String.valueOf(totalQuesadillas.get()));
                actualizarTotal.run();
            }
        });

        // Tamales
        sumarTamal.setOnClickListener(v -> {
            if (totalTamales.get() < 10) {
                totalTamales.getAndIncrement();
                cantidadTamal.setText(String.valueOf(totalTamales.get()));
                actualizarTotal.run();
            } else {
                Toast.makeText(getApplicationContext(), "Cantidad máxima de tamales alcanzada", Toast.LENGTH_SHORT).show();
            }
        });
        restarTamal.setOnClickListener(v -> {
            if (totalTamales.get() > 0) {
                totalTamales.getAndDecrement();
                cantidadTamal.setText(String.valueOf(totalTamales.get()));
                actualizarTotal.run();
            }
        });

        // Burritos
        sumarBurrito.setOnClickListener(v -> {
            if (totalBurritos.get() < 10) {
                totalBurritos.getAndIncrement();
                cantidadBurrito.setText(String.valueOf(totalBurritos.get()));
                actualizarTotal.run();
            } else {
                Toast.makeText(getApplicationContext(), "Cantidad máxima de burritos alcanzada", Toast.LENGTH_SHORT).show();
            }
        });
        restarBurrito.setOnClickListener(v -> {
            if (totalBurritos.get() > 0) {
                totalBurritos.getAndDecrement();
                cantidadBurrito.setText(String.valueOf(totalBurritos.get()));
                actualizarTotal.run();
            }
        });

        // Cargar el total del pedido
        actualizarTotal.run();

        // PULSAR PEDIR: añadir el total al acumulado de la mesa
        pedir.setOnClickListener(v -> {

            // Extrae el valor del pedido actual en un double (número con 2 decimales)
            double totalPedido = totalActual[0].get();

            if (totalPedido > 0) { // Control de pedido válido

                // Añadir solo una vez el total del pedido al acumulado de la mesa
                acumuladoMesa.updateAndGet(v1 -> v1 + totalPedido);

                // actualizar la UI del total acumulado
                totalAcumulado.setText(String.format("Total: %.2f €", acumuladoMesa.get()));

                // Limpiar cantidades en UI y resetar el pedido actual
                precio.setText(String.format("Precio: %.2f €", 0.0));
                totalBurritos.set(0);
                cantidadBurrito.setText("0");
                totalNachos.set(0);
                cantidadNachos.setText("0");
                totalQuesadillas.set(0);
                cantidadQuesadillas.setText("0");
                totalTacos.set(0);
                cantidadTacos.setText("0");
                totalTamales.set(0);
                cantidadTamal.setText("0");
                totalActual[0].set(0.0);

                // Cambiar el botón de CANCELAR a PAGAR
                btn_cancelar_pagar.setText("PAGAR");
                btn_cancelar_pagar.setBackgroundColor(getResources().getColor(R.color.blue));
                btn_cancelar_pagar.setTextColor(getResources().getColor(R.color.white));
            } else {
                // Avisa de pedido vacío
                Toast.makeText(getApplicationContext(), "Pedido vacío", Toast.LENGTH_SHORT).show();
            }
        });


        // PULSAR CANCELAR/PAGAR: Un mismo botón cambia sus propiedades
        btn_cancelar_pagar.setOnClickListener(v -> {

            // Ver si el botón está en modo PAGAR o CANCELAR
            String texto = btn_cancelar_pagar.getText().toString().trim();

            // Si el texto del botón es "pagar", este guarda el pedido
            if ("PAGAR".equals(texto)) {

                // Guarda el acumulado total de los pedidos de una misma mesa
                double totalComanda = acumuladoMesa.get();

                // Si hay algo que pagar
                if (totalComanda > 0) {
                    // La mesa ocupada que tiene algo que pagar
                    Mesa mesaActual = new Mesa(nombreMesa, true, true);

                    // Llama a la API interfaz de Android para marcar la mesa
                    api.ocuparMesa(nombreMesa, mesaActual).enqueue(new Callback<Mesa>() {
                        @Override
                        public void onResponse(Call<Mesa> call, Response<Mesa> response) {
                            System.out.println("Mesa marcada como a pagar");
                        }

                        @Override
                        public void onFailure(Call<Mesa> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                    // Crea el pedido con los datos finales d ela mesa (pedido total y nombre de la mesa)
                    Pedido pedidoFinal = new Pedido(totalComanda, mesaActual);

                    // Guarda el pedido en la API, que lo pasa a la db
                    api.guardarPedido(pedidoFinal).enqueue(new Callback<Pedido>() {
                        @Override
                        public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                            if (response.isSuccessful()) {

                                // Mensaje de pago realizado con el precio total formateado
                                Toast.makeText(getApplicationContext(), "Pago realizado: " + String.format("%.2f", totalComanda) + " €", Toast.LENGTH_SHORT).show();

                                // Resetea el acumulador y vuelve a la elección de mesa
                                acumuladoMesa.set(0.0);

                                // Resetea también el precio que ve el usuario en pantalla
                                totalAcumulado.setText(String.format("Total: %.2f €", acumuladoMesa.get()));
                                startActivity(new Intent(Carta.this, EleccionMesa.class));
                            }
                        }

                        @Override
                        public void onFailure(Call<Pedido> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } else {

                }
            } else {
                // CANCELAR: liberar mesa y resetear acumulador
                if (nombreMesa != null) {
                    Mesa mesaLibre = new Mesa(nombreMesa, false, false);

                    // Liberar la mesa en la API
                    api.ocuparMesa(nombreMesa, mesaLibre).enqueue(new Callback<Mesa>() {
                        @Override
                        public void onResponse(Call<Mesa> call, Response<Mesa> response) { }

                        @Override
                        public void onFailure(Call<Mesa> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
                } else {

                }

                // Resetea el acumulador y vuelve a elección de mesa
                acumuladoMesa.set(0.0);
                Toast.makeText(getApplicationContext(), "Pedido cancelado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Carta.this, EleccionMesa.class));
            }
        });
    }
}
