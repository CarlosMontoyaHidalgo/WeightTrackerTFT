# Subida de Datos a Firestore

Este proyecto contiene scripts en Node.js para subir datos a Firebase Firestore, incluyendo equipos (`equipment`), ejercicios (`exercises`), y músculos (`muscles`). Los scripts optimizan la carga de datos mediante operaciones por lotes y manejan referencias entre colecciones.

## ¿Qué hace?

- **Carga equipos**: Sube datos de equipos a la colección `equipment`.
- **Carga ejercicios**: Sube datos de ejercicios a la colección `exercises` con referencias a músculos.
- **Carga músculos**: Sube datos de músculos a la colección `muscles` con referencias a músculos secundarios.
- **Uso de batch**: Utiliza operaciones por lotes para subir datos de forma eficiente.

## Requisitos

- **Node.js**: Versión 14 o superior.
- **Cuenta de Firebase**: Proyecto con Firestore configurado.
- **Clave de servicio**: Archivo `service-account-key.json` descargado desde Firebase.
- **Archivos JSON**:
    - `equipment.json`: Datos de equipos.
    - `exercises.json`: Datos de ejercicios.
    - `muscles.json`: Datos de músculos.
- **Dependencias**: Instala las dependencias necesarias con:

  ```
  npm install firebase-admin
  ```

## Instalación

1. Clona el proyecto:

   ```
   git clone <url-del-repositorio>
   cd <carpeta-del-proyecto>
   ```

2. Instala las dependencias:

   ```
   npm install
   ```

3. Coloca la clave de servicio:

    - Guarda el archivo `service-account-key.json` en la raíz del proyecto.

4. Prepara los archivos JSON:

    - Asegúrate de que `equipment.json`, `exercises.json`, y `muscles.json` estén en la raíz del proyecto.
    - Verifica que tengan la estructura correcta (ver **Estructura de los datos**).

## Cómo usar

Cada script sube datos a una colección específica en Firestore. Ejecútalos individualmente según lo que desees cargar.

### 1. Subir equipamiento - `upload_equipment.js`

Sube datos de equipos a la colección `equipment`.

```
node upload_equipment.js
```

### 2. Subir ejercicios - `upload_exercises.js`

Sube ejercicios a la colección `exercises`, convirtiendo `primaryMuscle` y `secondaryMuscle` en referencias a documentos de la colección `muscles`.

```
node upload_exercises.js
```

**Nota**: La colección `muscles` debe estar poblada antes de ejecutar este script.

### 3. Subir músculos - `upload_muscles.js`

Sube músculos a la colección `muscles`, convirtiendo `secondaryMuscle` en referencias a otros documentos de la colección `muscles`.

```
node upload_muscles.js
```

## Estructura de los datos

### `equipment.json`

```json
[
  {
    "name": "Dumbbells",
    "description": "A pair of handheld weights used for strength training.",
    "imageUrl": "https://example.com/dumbbells.jpg"
  }
]
```

### `exercises.json`

```json
[
  {
    "id": "squat",
    "name": "Squat",
    "met": 5.5,
    "primaryMuscle": "legs",
    "secondaryMuscle": ["glutes", "hamstrings"],
    "imageUrl": "https://example.com/squat.jpg",
    "type": "strength",
    "requiresWeight": true
  }
]
```

### `muscles.json`

```json
[
  {
    "id": "chest",
    "name": "Chest",
    "primaryMuscle": "Chest",
    "secondaryMuscle": ["shoulders", "triceps"],
    "imageUrl": "https://example.com/chest.jpg"
  }
]
```

## Comandos útiles

- **Instalar dependencias**:

  ```
  npm install
  ```

- **Ejecutar un script específico**:

  ```
  node <nombre-del-script>.js
  ```

## Estructura del proyecto

- `upload_equipment.js`: Script para subir equipamiento.
- `upload_exercises.js`: Script para subir ejercicios con referencias a músculos.
- `upload_muscles.js`: Script para subir músculos con referencias a músculos secundarios.
- `service-account-key.json`: Clave de Firebase (no incluir en git).
- `equipment.json`: Datos de equipos.
- `exercises.json`: Datos de ejercicios.
- `muscles.json`: Datos de músculos.
- `package.json`: Dependencias y scripts.

## Dependencias

- `firebase-admin`: Conexión con Firestore.

## Notas

- **Orden de ejecución**: Sube primero `muscles` (`upload_muscles.js`), luego `exercises` (`upload_exercises.js`), y finalmente `equipment` (`upload_equipment.js`) para evitar errores de referencias.
- **IDs únicos**: Asegúrate de que los `id` en los JSON sean únicos para evitar conflictos en Firestore.
- **Estructura de JSON**: Los campos `primaryMuscle` y `secondaryMuscle` en `exercises.json` y `muscles.json` deben coincidir con los `id` de documentos en la colección `muscles`.

## Solución de problemas

- **Error de autenticación**: Verifica que `service-account-key.json` sea válido y esté en la raíz del proyecto.
- **Archivo JSON no encontrado**: Asegúrate de que `equipment.json`, `exercises.json`, y `muscles.json` estén en la raíz.
- **Referencias no válidas**: Comprueba que los `id` en `primaryMuscle` y `secondaryMuscle` correspondan a documentos existentes en `muscles`.
- **Error de colección vacía**: Pobla la colección `muscles` antes de ejecutar `upload_exercises.js`.