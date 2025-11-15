package com.example.tacopaco.clases;

import java.util.concurrent.atomic.AtomicInteger;

public class Mesa {
    private String nombre;
    private boolean ocupada;
    private boolean a_pagar;

    public Mesa(String nombre, boolean ocupada, boolean a_pagar) {
        this.nombre = nombre;
        this.ocupada = ocupada;
        this.a_pagar = a_pagar;
    }


    public boolean isOcupada() { return ocupada; }

    public boolean mesaPagada() { return a_pagar; }
    

    public String getNombre() { return nombre; }


}
