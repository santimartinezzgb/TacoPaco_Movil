# APP Móvil - Taco Paco

Aplicación Android para clientes del restaurante Taco Paco.

## 📱 Tecnologías

- Java 17
- Android SDK API 34 (mínimo API 26)
- Retrofit 2 (API REST)
- Gson (JSON)
- Material Design

## 🎯 Funcionalidades

### Selección de Mesa (EleccionMesa.java)
- Lista de 5 mesas (3 interiores + 2 terraza)
- Indicador visual: verde (disponible) / rojo (ocupada)
- Actualización en tiempo real desde servidor
- Botón actualizar y volver

### Carta y Pedidos (Carta.java)
- Menú con 5 productos: Tacos, Nachos, Quesadillas, Tamales, Burritos
- Contador de cantidad por producto (máx. 10 unidades)
- Total acumulado de pedidos en la misma mesa
- Botones:
  - **PEDIR**: Añade pedido al total acumulado
  - **CANCELAR**: Libera mesa y cancela todo
  - **PAGAR**: Finaliza y guarda pedido en BD

## 🔧 Componentes

### Activities
- `MainActivity`: Pantalla de inicio
- `EleccionMesa`: Selección de mesa disponible
- `Carta`: Realización de pedidos

### RetrofitClient.java
Cliente HTTP para comunicación con API:
```java
BASE_URL = "http://10.0.2.2:3000/"  // Emulador Android
```
> Nota: `10.0.2.2` es el localhost del ordenador desde el emulador

### Layouts XML
- `eleccion_mesa.xml`: Interfaz de selección de mesas
- `carta.xml`: Menú de productos con contadores

## 🎨 Características UI

- Fuente personalizada (Kat)
- Colores diferenciados para interior/terraza
- Contadores +/- para cada producto
- Total en tiempo real
- Botón dinámico CANCELAR/PAGAR

## 📡 Conexión API

Consume endpoints:
- `GET /mesas` - Listar mesas disponibles
- `PUT /mesas/:nombre` - Ocupar/liberar mesa
- `POST /pedidos` - Guardar pedido final

## ⚙️ Configuración

**Importante**: Para probar en dispositivo físico, cambiar IP en `RetrofitClient.java`:
```java
private static final String BASE_URL = "http://TU_IP_LOCAL:3000/";
```

---

**Autor:** Santi Martínez
