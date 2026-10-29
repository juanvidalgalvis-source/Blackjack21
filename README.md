# Blackjack 21

Aplicación de escritorio para jugar Blackjack contra el computador. El jugador inicia con 1.000 fichas, apuesta antes de cada ronda y puede pedir cartas o plantarse. La banca juega con reglas fijas y la aplicación informa el resultado y actualiza el saldo.

## Información del proyecto

| Dato | Información |
|---|---|
| Nombre del estudiante | Juan Felipe Vidal |
| Código estudiantil | 2439854 |
| Docente | Paola Johana Rodriguez |
| Versión | 5 — Completa |

## Requisitos

- JDK 17.
- IntelliJ IDEA con soporte para proyectos Maven.
- No es necesario instalar JavaFX por separado; Maven administra las dependencias del proyecto.

## Cómo ejecutar

### Desde IntelliJ IDEA

1. Abrir la carpeta del proyecto o importar `pom.xml` como proyecto Maven.
2. Configurar el proyecto con JDK 17 y esperar a que Maven descargue las dependencias.
3. En la ventana **Maven**, abrir **Plugins → javafx → javafx:run**.

### Desde una terminal

Ejecutar el comando desde la carpeta raíz del repositorio:

**Windows PowerShell**

```powershell
.\mvnw.cmd javafx:run
```

**macOS o Linux**

```bash
./mvnw javafx:run
```

## Funcionalidades

- Mazo de 52 cartas y manos mostradas con imágenes.
- Puntajes con ajuste de As, Blackjack, Veintiuno y Veinte y medio.
- Turno automático del computador, que pide cartas hasta llegar a 17.
- Apuestas desde 10 fichas, pagos según el resultado y aviso de fin del juego si el saldo es insuficiente.
- Consulta de reglas y cierre con confirmación.

## Etapas implementadas

| Versión | Alcance |
|---|---|
| 1 — Base | Mazo, cartas y ventana inicial. |
| 2 — Jugable | Puntajes, turnos y jugadas especiales. |
| 3 — Rival | Turno del computador y resultado de la ronda. |
| 4 — Economía | Apuestas, saldo y pagos. |
| 5 — Completa | Imágenes, reglas y controles finales. |

## Estructura principal

| Archivo o carpeta | Rol |
|---|---|
| `src/main/java/.../model/` | Representa las cartas, el mazo y las reglas de la partida. |
| `src/main/java/.../controller/BlackjackController.java` | Conecta las acciones de la interfaz con la lógica del juego. |
| `src/main/java/.../BlackjackApplication.java` y `Launcher.java` | Inician la aplicación y cargan la ventana. |
| `src/main/java/module-info.java` | Declara los módulos y paquetes de JavaFX. |
| `src/main/resources/.../view/blackjack-view.fxml` | Define la distribución de la ventana. |
| `src/main/resources/.../images/PNG-cards-1/` | Contiene las imágenes de las cartas. |
| `src/test/java/` | Contiene las pruebas automatizadas del modelo y los recursos. |

## Pruebas automatizadas

Desde la raíz del proyecto:

```powershell
.\mvnw.cmd test
```

En macOS o Linux:

```bash
./mvnw test
```
