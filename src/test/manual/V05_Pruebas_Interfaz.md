# Pruebas manuales de aceptación — Versión 5

Estos casos verifican la interfaz JavaFX de HU-15 a HU-19. Son pruebas pendientes de ejecución: completar «Resultado obtenido» y «Estado» durante la sesión de prueba. No implican que la interfaz ya haya sido probada.

## Preparación

1. Desde la raíz del proyecto, ejecutar `.\mvnw.cmd javafx:run`.
2. Confirmar que la ventana abre sin errores y que se puede interactuar con ella. Esta comprobación es solo una prueba de humo.
3. Reducir la ventana hasta su tamaño mínimo y comprobar que las acciones principales sigan visibles sin desplazamiento vertical.
4. Para comprobar que abrir reglas o cancelar el cierre no altera el juego, anotar antes de la acción las cartas visibles, puntajes, saldo, apuesta y mensaje/estado.
5. No borrar ni modificar recursos del proyecto para probar la imagen faltante. Usar una copia desechable del proyecto o de los recursos.

| ID | HU | Pasos y datos | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|---|
| V05-HU15-01 | HU-15 | Ejecutar `BJTest5VRecursosImagenes` con `.\mvnw.cmd test`. | La prueba confirma que los recursos de las 52 cartas están disponibles en la ruta esperada. | Pendiente | Pendiente |
| V05-HU15-02 | HU-15 | Iniciar rondas y observar las manos del jugador y del computador. Seguir las cartas recibidas y reducir el ancho visible de la ventana hasta que alguna mano exceda el área. | Cada carta se muestra con su imagen correcta; las cartas mantienen el orden de reparto y tamaño uniforme; el desplazamiento horizontal permite recorrer la mano sin ocultar permanentemente cartas. | Pendiente | Pendiente |
| V05-HU15-03 | HU-15 | En una copia de prueba, hacer que falte un recurso PNG de una carta que llegue a mostrarse y abrir la aplicación desde esa copia. | La aplicación no se cierra; informa qué recurso falta y presenta el código de esa carta como alternativa. Los recursos del proyecto original permanecen intactos. | Pendiente | Pendiente |
| V05-HU16-01 | HU-16 | Abrir «Ver reglas» antes de iniciar una ronda; revisar el resumen y cerrarlo. | Se abre un diálogo modal no editable con objetivo, valores, ases, apuestas y pagos, jugadas especiales/prioridad de Blackjack y reglas del computador. La ventana principal sigue disponible. | Pendiente | Pendiente |
| V05-HU16-02 | HU-16 | Iniciar una ronda, anotar el estado visible, abrir y cerrar «Ver reglas». Comparar con lo anotado. | Manos, puntajes, cartas restantes, turno/mensaje, apuesta y saldo permanecen sin cambios al cerrar el diálogo. | Pendiente | Pendiente |
| V05-HU17-01 | HU-17 | Antes de una ronda, pulsar «Cerrar juego» y elegir «Cancelar»; repetir y elegir «Sí, cerrar». | «Cancelar» deja la ventana abierta sin cambiar la partida; «Sí, cerrar» cierra la aplicación. | Pendiente | Pendiente |
| V05-HU17-02 | HU-17 | Reabrir la aplicación. Usar la X de la ventana, primero cancelar y luego repetir confirmando el cierre. | La X presenta la misma confirmación que el botón. Cancelar conserva ventana y estado; confirmar cierra la aplicación. | Pendiente | Pendiente |
| V05-HU17-03 | HU-17 | Repetir V05-HU17-01 y V05-HU17-02 durante una ronda y después de su resultado. | Cancelar conserva las cartas, puntajes, saldo, apuesta y estado que había antes de abrir la confirmación; confirmar cierra. | Pendiente | Pendiente |
| V05-HU18-01 | HU-18 | Al abrir la aplicación, observar las manos, apuesta, mensaje y controles. | Manos vacías, sin apuesta, instrucción para apostar; habilitados «Iniciar ronda», apuesta, reglas y cierre; deshabilitados pedir y plantarse. | Pendiente | Pendiente |
| V05-HU18-02 | HU-18 | Iniciar una ronda con una apuesta válida; probar visualmente los controles y tratar de editar la apuesta o iniciar otra ronda. | En el turno del jugador están habilitados pedir, plantarse, reglas y cierre. Apuesta e iniciar ronda están bloqueados; no se aceptan acciones fuera de turno. | Pendiente | Pendiente |
| V05-HU18-03 | HU-18 | Terminar una ronda sin agotar el saldo y observar los controles; iniciar otra ronda válida. | Pedir y plantarse quedan bloqueados. Iniciar ronda, campo de apuesta, reglas y cierre quedan disponibles para la siguiente ronda. No se puede iniciar la siguiente antes de que termine la actual. | Pendiente | Pendiente |
| V05-HU18-04 | HU-18 | Preparar una ejecución con saldo menor a 10 fichas, llegar al fin del juego y observar los controles. | Se muestra «Fin del juego»; quedan deshabilitados iniciar ronda y apuesta; reglas y cierre permanecen habilitados. | Pendiente: requiere una configuración de saldo de prueba | Pendiente |
| V05-HU19-01 | HU-19 | Revisar la interfaz antes de la primera ronda, durante el turno del jugador y al terminar una ronda. | Se identifican ambas zonas; se muestran cartas, puntajes, saldo, apuesta y mensaje correspondiente al estado; al terminar se informa resultado, motivo y fichas acreditadas. | Pendiente | Pendiente |
| V05-HU19-02 | HU-19 | Observar el mensaje de turno del computador al plantarse o al terminar automáticamente el turno del jugador. | El turno del computador se comunica en pantalla antes de presentar el resultado final. | Pendiente: verificar si el turno síncrono llega a renderizarse | Pendiente |
| V05-HU19-03 | HU-19 | Observar mensajes de Blackjack, Veintiuno, Veinte y medio y Pasarse cuando se presenten durante el juego. | Cada situación se comunica en español y corresponde a la mano; Veinte y medio solo se anuncia para J/Q/K + 4 iniciales. | Pendiente | Pendiente |
| V05-HU19-04 | HU-19 | Reducir la ventana hasta el tamaño mínimo configurado y comprobar el tablero y los controles en cada estado de ronda. | Sin scroll vertical se ven las dos manos, su puntaje, el estado, saldo, apuesta y todos los botones. Solo las cartas pueden requerir desplazamiento horizontal dentro de su mano. | Pendiente | Pendiente |

## Registro de ejecución

Completar una fila por ejecución y anotar entorno, fecha, versión/commit, resultado y evidencia (por ejemplo, captura de pantalla cuando sea útil). Para casos no reproducibles desde la interfaz, registrar el bloqueo y no inferir un resultado aprobado a partir de una prueba del modelo.
