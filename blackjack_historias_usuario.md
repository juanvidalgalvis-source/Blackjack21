# Mini proyecto 21 Black Jack: duelo contra el computador
**Curso:** 750014C FPOE, Universidad del Valle (Profesora Paola J. Rodríguez Carrillo)
**Tecnología:** Java + JavaFX, patrón Modelo–Vista–Controlador

> Este documento reúne (1) el entendimiento del proyecto, (2) las aclaraciones acordadas, (3) lo que NO se debe hacer y (4) el plan de trabajo en versiones, historias de usuario (HU) y tareas.
> La creación inicial de las carpetas del patrón MVC es el primer paso lógico y no se incluye en las HU.

---

## 1. Qué es el proyecto

Aplicación de escritorio donde una persona juega 21 Black Jack contra el computador, que actúa como la banca. El jugador apuesta fichas ficticias (arranca con 1.000), pide cartas o se planta, y el computador juega con reglas fijas. Las cartas se muestran como imágenes.

Lo que realmente se evalúa, además del juego:
- Uso correcto de **estructuras de datos**: el mazo es un **arreglo de 52 posiciones** y cada mano es una **lista enlazada (LinkedList)**. Ambas son obligatorias.
- **Separación de responsabilidades** en Modelo, Vista y Controlador.
- **Evidencias**: diagrama de clases UML y casos de prueba.

## 2. Reglas del juego (resumen)

**Mazo:** 52 cartas, sin comodines, compartido por ambos. Cada ronda empieza con el mazo completo y mezclado. Una carta no se reparte dos veces en la misma ronda.

**Valores:** 2 al 10 valen su número. J, Q y K valen 10. El As vale 11 mientras no haga pasar de 21; si se pasa, baja a 1 (se recalcula con cada carta). Los palos no cambian el puntaje.

**Apuestas:** entero, múltiplo de 10, mínimo 10, no mayor al saldo. Se descuenta al iniciar la ronda y no cambia durante ella.

| Resultado | Fichas que se acreditan al finalizar |
|---|---|
| Derrota | Ninguna (se pierde la apuesta descontada) |
| Empate | Se devuelve la apuesta |
| Victoria ordinaria | Se devuelve la apuesta y se gana una cantidad igual (apuesta ×2) |
| Victoria con Blackjack | Se devuelve la apuesta y se gana 1,5 veces su valor (apuesta ×2,5) |

Ejemplo con apuesta de 100: victoria ordinaria acredita 200; victoria con Blackjack acredita 250. El computador no maneja saldo.

**Desarrollo de la ronda:**
1. Jugador recibe dos cartas.
2. Se muestra su puntaje y las acciones disponibles.
3. El computador espera, sin cartas.
4. El jugador pide o se planta.
5. Al plantarse, el computador recibe dos cartas y ejecuta su turno.
6. Se determina el resultado y se actualiza el saldo una sola vez.

Si el jugador llega a 21, su turno termina solo y empieza el del computador. Si se pasa de 21, pierde de inmediato y el computador no juega. Si el jugador tiene Blackjack, el turno del computador empieza automáticamente: si este también saca Blackjack con sus dos primeras cartas hay empate, si no, gana el jugador.

**Computador:** recibe dos cartas al comenzar su turno, reconoce un Blackjack inicial, pide mientras su puntaje sea menor que 17, se planta con 17 a 21 (incluido el 17 blando), pierde si supera 21. Sus decisiones dependen solo de su propia mano.

## 3. Jugadas y jerarquía

| Situación | Condición | Tratamiento |
|---|---|---|
| Blackjack | As + carta de valor 10 (10, J, Q o K) **solo en las dos cartas iniciales** | Supera a cualquier mano que no sea Blackjack |
| Veintiuno | Puntaje 21 sin cumplir la condición de Blackjack | Se compara con la mano del rival |
| Veinte y medio | Las dos cartas iniciales son una J, Q o K y un 4 | Mensaje especial; el jugador puede pedir o plantarse. Al comparar, vence a manos válidas de hasta 20; pierde contra cualquier 21. No paga extra |
| Pasarse | Puntaje mayor a 21 tras ajustar ases | El participante pierde |
| Empate | Ambos con Blackjack, o el mismo puntaje válido sin prioridad de Blackjack | Se devuelve la apuesta |

Ejemplos de decisión del ganador:

| Jugador | Computador | Resultado |
|---|---|---|
| Blackjack | Sin Blackjack | Gana el jugador |
| Blackjack | Blackjack | Empate |
| 21 normal | 20 | Gana el jugador |
| J/Q/K + 4 (veinte y medio) | 20 | Gana el jugador |
| J/Q/K + 4 (veinte y medio) | 21 normal o Blackjack | Pierde el jugador |
| J/Q/K + 4 (veinte y medio) | J/Q/K + 4 (veinte y medio) | Empate |
| 18 | 17 | Gana el jugador |
| 17 | 19 | Pierde el jugador |
| 22 o más | Cualquiera | Pierde el jugador |

Veinte y medio no se determina solo por sumar 14: requiere que las dos cartas iniciales sean una J, Q o K y un 4, sin importar el palo. Vence a cualquier mano válida de hasta 20 puntos, pierde contra cualquier 21, sea normal o Blackjack, y empata contra otro Veinte y medio.

## 4. Aclaraciones acordadas (respuestas a las dudas)

1. **Veinte y medio solo es la combinación inicial de una figura (J, Q o K) y un 4.** Deben ser exactamente las dos cartas iniciales; el palo no importa. Otras manos que sumen 14, como 9 + 5, As + 3 o 10 + 4, no cuentan como Veinte y medio. Se informa con un mensaje y el jugador puede seguir jugando o plantarse. No recibe pago adicional: al resolver la ronda, vence a una mano válida de hasta 20, pierde contra cualquier 21 y empata contra otro Veinte y medio.
2. **La jugada se reconoce por las cartas, no solo por el puntaje.** Si el jugador pide otra carta, deja de tener Veinte y medio y el estado se actualiza según su nuevo puntaje.
3. **"Un diez" incluye 10, J, Q y K.** Un Blackjack es As + cualquiera de ellas en las dos cartas iniciales. Un As + 10 formado después de pedir más cartas (ejemplo: As + 5 + 5) es un 21 normal.
4. **El sistema distingue Blackjack de 21 normal** viendo cómo se llegó al 21, no solo el puntaje. Con Blackjack vs 21 de tres cartas, gana el Blackjack.
5. **El computador pide mientras su puntaje ajustado sea menor que 17.** Se planta con 17 o más, incluido el 17 blando (As + 6).
6. **Fin del juego:** si el saldo es menor a 10 se muestra «Fin del juego», se bloquean «Iniciar ronda» y el campo de apuesta, y «Cerrar juego» sigue activo. Con exactamente 10 todavía se puede apostar.
7. **Numeración del PDF:** el salto de la sección 8 a la 10 es un error de tipeo; no falta contenido.
8. **Imágenes de las cartas:** el equipo ya tiene el set y estará en una carpeta llamada `images/PNG-cards-1`. Cada carta debe vincularse con su imagen (ejemplo: 4P es 4 de Picas). Es una de las últimas preocupaciones y por eso va en la versión 5.
9. **Veinte y medio solo se anuncia al jugador** (suposición por defecto): el computador no decide con base en eso.
10. **Mano como LinkedList:** el PDF dice "lista enlazada" y el equipo decidió usar `LinkedList`. Si se quisiera una lista propia, habría que consultarlo con la profesora.

## 5. Alcance

**Dentro del alcance:** un jugador contra la banca, por rondas, mientras haya saldo suficiente; sin persistencia ni red.

**Lo que NO se debe hacer:**
- Agregar reglas del Blackjack real que no se piden: doblar, dividir pares, seguro, rendirse, varios mazos.
- Repartir cartas al computador antes de que el jugador se plante o termine su turno.
- Representar el mazo o las manos con estructuras distintas a las pedidas (arreglo de 52 y lista enlazada).
- Mezclar lógica del juego (puntajes, pagos, reglas) con la presentación; la interfaz solo muestra y recoge acciones.
- Actualizar el saldo más de una vez por ronda.
- Permitir acciones fuera de turno: pedir carta sin ronda activa, iniciar una ronda con otra en curso, cambiar la apuesta después del reparto.
- Dar pago extra por Veinte y medio.
- Reiniciar la partida al consultar las reglas o al cancelar el cierre.
- Guardar el saldo en archivos, o agregar multijugador, cuentas o conexión en red.

---

## 6. Cómo leer el plan

**Reglas de la planificación:**
- Mínimo 3 y máximo 7 versiones; máximo 7 HU por versión.
- Cada versión es **autocompleta**: la aplicación corre y se puede probar al terminar cada una.
- En cada versión se preparan y ejecutan pruebas de lo que se acaba de implementar y de sus casos límite. La HU-20 consolida los casos y resultados al final; las pruebas no se dejan para la última versión.
- Las HU siguen el formato **Como – Quiero – Para**. Los criterios de aceptación son las tareas.
- **Tarea corta:** la que se escribe en 3 líneas o menos. Las tareas cortas relacionadas se agrupan en una **super tarea**.
- Cada tarea (o super tarea) se evalúa con dos etiquetas:
  - **Necesidad:** Alta (sin esto la versión no funciona), Media (mejora claramente la versión), Baja (pulido).
  - **Obligatoriedad:** **PDF** (lo exige el enunciado), **Clase** (viene del ejercicio visto en clase o del patrón pedido), **Propuesta** (decisión del equipo, no exigida).
- Las tareas cortas dentro de una super tarea heredan su evaluación.

**Resumen de versiones:**

| Versión | Nombre | Qué logra | HU |
|---|---|---|---|
| 1 | Base | Mazo, mano inicial, pedir carta y ventana básica en texto | HU-01 a HU-04 |
| 2 | Jugable | Puntaje, ases, plantarse, jugadas especiales y nueva ronda | HU-05 a HU-08 |
| 3 | Rival | Turno del computador y resultado de la ronda | HU-09 a HU-11 |
| 4 | Economía | Apuestas, saldo, pagos y fin del juego | HU-12 a HU-14 |
| 5 | Completa | Imágenes, reglas, cierre con confirmación, estados de botones y mensajes | HU-15 a HU-19 |
| 6 | Entrega | Consolidación de las pruebas de cada versión y diagrama UML | HU-20 a HU-21 |

---

# VERSIÓN 1: Base
**Objetivo:** una ventana que reparte dos cartas al abrir, deja pedir más cartas con un botón y muestra la mano en texto hasta agotar el mazo. Aún no hay puntajes, rival ni apuestas.

## HU-01. Recibir mi mano inicial
**Como** jugador, **quiero** recibir dos cartas al abrir la aplicación, **para** tener una mano inicial sin solicitar las cartas individualmente.

**Criterios de aceptación / tareas:**
- **Super tarea A: Preparar el mazo** (Necesidad: Alta | Obligatoriedad: PDF)
  - Al iniciar, se crea un mazo de 52 cartas diferentes: 13 por cada palo, sin comodines.
  - El mazo se representa con un arreglo de 52 posiciones.
  - El mazo se mezcla antes de repartir.
- **Tarea B: Repartir la mano inicial** (Alta | PDF)
  - El jugador recibe automáticamente dos cartas diferentes, guardadas en una mano representada con una lista enlazada (LinkedList).
- **Tarea C: Mostrar la mano en texto** (Alta | Propuesta, como paso previo a las imágenes)
  - Las dos cartas se muestran en texto en la interfaz.
- **Tarea D: Estado tras el reparto** (Alta | PDF)
  - Tras el reparto quedan 50 cartas disponibles y el botón «Pedir carta» está habilitado.

## HU-02. Pedir una carta adicional
**Como** jugador, **quiero** pedir una carta mediante un botón, **para** agregarla a mi mano y ver las cartas que he recibido.

**Criterios de aceptación / tareas:**
- **Super tarea A: Botón «Pedir carta»** (Alta | PDF)
  - Existe el botón «Pedir carta».
  - Cada clic reparte exactamente una carta mientras haya cartas disponibles.
- **Super tarea B: Actualizar la mano** (Alta | PDF)
  - La carta recibida se agrega al final de la mano sin eliminar las anteriores.
  - La representación en texto se actualiza de inmediato y muestra todas las cartas.
  - Las cartas se muestran en el orden en que fueron recibidas.
- **Tarea C: Sin repeticiones** (Alta | PDF)
  - Una carta repartida deja de estar disponible y no vuelve a salir en esa ejecución.

## HU-03. Conocer cuándo se agota el mazo
**Como** jugador, **quiero** recibir un aviso cuando no queden cartas disponibles, **para** comprender por qué ya no puedo pedir más cartas.

**Criterios de aceptación / tareas:**
- **Tarea A: Última carta** (Alta | PDF)
  - Si queda una carta y el jugador la solicita, se agrega a su mano y se muestra.
- **Super tarea B: Aviso de mazo agotado** (Media | Propuesta)
  - Inmediatamente después de repartir la última carta se informa: «El mazo se ha agotado. No quedan cartas para repartir».
  - El botón «Pedir carta» queda deshabilitado.
  - La mano completa permanece visible.
- **Tarea C: Verificación del mazo completo** (Media | PDF, por la regla de no repetir cartas)
  - Al agotarse el mazo, la mano contiene las 52 cartas, sin repeticiones.

## HU-04. Ver el juego en una ventana
**Como** jugador, **quiero** una ventana básica con un área identificada para mi mano, **para** interactuar con el juego sin usar la consola.

**Criterios de aceptación / tareas:**
- **Super tarea A: Ventana inicial** (Alta | PDF)
  - Al ejecutar el programa se abre una ventana con título y tamaño definidos.
  - La ventana tiene una zona identificada como «Jugador».
  - La ventana muestra la cantidad de cartas restantes en el mazo.
- **Tarea B: Conexión de eventos** (Alta | Clase, patrón MVC)
  - Los clics sobre los botones se atienden y la ventana se refresca sin que la presentación contenga reglas del juego.

---

# VERSIÓN 2: Jugable
**Objetivo:** el jugador puede jugar su mano completa: ve su puntaje, se planta, se le reconocen las jugadas especiales y puede empezar otra ronda. Aún sin rival ni apuestas.

## HU-05. Ver mi puntaje
**Como** jugador, **quiero** ver el puntaje de mi mano, **para** decidir si pido otra carta o me planto.

**Criterios de aceptación / tareas:**
- **Super tarea A: Valor de las cartas** (Alta | PDF)
  - Las cartas del 2 al 10 valen su número.
  - J, Q y K valen 10.
  - El palo no modifica el puntaje.
- **Super tarea B: Cálculo con ases** (Alta | PDF)
  - Cada As cuenta como 11 mientras no haga superar 21; si lo hace, cuenta como 1.
  - Con varios ases, el cálculo busca el mayor puntaje sin pasarse de 21.
  - Si todas las combinaciones superan 21, todos los ases cuentan como 1.
- **Tarea C: Mostrar el puntaje** (Alta | PDF)
  - El puntaje se recalcula y se muestra tras cada carta recibida, incluida la mano inicial.

## HU-06. Plantarme y terminar mi turno
**Como** jugador, **quiero** plantarme con el botón correspondiente, **para** cerrar mi turno cuando estoy conforme con mi mano.

**Criterios de aceptación / tareas:**
- **Super tarea A: Botón «Plantarse»** (Alta | PDF)
  - Existe el botón «Plantarse».
  - Al plantarse, el jugador ya no puede pedir más cartas en esa ronda.
- **Super tarea B: Fin automático del turno** (Alta | PDF)
  - Si el puntaje llega a 21, el turno termina automáticamente.
  - Si el puntaje supera 21, el jugador pierde de inmediato y se informa.
- **Tarea C: Bloqueo de acciones** (Alta | PDF)
  - Terminado el turno, «Pedir carta» y «Plantarse» quedan deshabilitados.

## HU-07. Que se reconozcan mis jugadas especiales
**Como** jugador, **quiero** que el juego reconozca y me avise de mis jugadas especiales, **para** entender el estado de mi mano.

**Criterios de aceptación / tareas:**
- **Super tarea A: Blackjack** (Alta | PDF)
  - Se reconoce Blackjack solo cuando las dos cartas iniciales son un As y una carta de valor 10 (10, J, Q o K).
  - Un 21 formado con más de dos cartas se trata como Veintiuno, no como Blackjack.
  - Con Blackjack, el turno del jugador termina automáticamente y se muestra un mensaje.
- **Tarea B: Veintiuno** (Alta | PDF)
  - Un puntaje de 21 que no cumple la condición de Blackjack se identifica como Veintiuno.
- **Super tarea C: Veinte y medio** (Media | PDF)
  - Si las dos cartas iniciales son una J, Q o K y un 4, sin importar el palo, se muestra el mensaje especial «Veinte y medio».
  - Otras manos con puntaje 14 (por ejemplo, 9 + 5, As + 3 o 10 + 4) no se reconocen como Veinte y medio.
  - El jugador puede seguir pidiendo o plantarse.
  - Si el jugador pide otra carta, deja de ser Veinte y medio y se evalúa el nuevo estado de su mano.
  - La jugada no produce victoria automática ni pago adicional.
- **Tarea D: Pasarse** (Alta | PDF)
  - Superar 21 tras ajustar los ases se identifica como «Pasarse» con su mensaje.

## HU-08. Jugar otra mano
**Como** jugador, **quiero** iniciar una nueva ronda al terminar la actual, **para** seguir jugando sin cerrar la aplicación.

**Criterios de aceptación / tareas:**
- **Super tarea A: Botón «Iniciar ronda»** (Alta | PDF)
  - Existe el botón «Iniciar ronda», habilitado solo cuando no hay una ronda en curso.
  - Al iniciar, se vuelve a crear el mazo completo de 52 cartas y se mezcla.
  - La mano anterior se descarta y se reparten dos cartas nuevas.
- **Tarea B: Sin rondas superpuestas** (Alta | PDF)
  - No es posible iniciar otra ronda antes de que termine la actual.
- **Tarea C: Primera mano** (Media | Propuesta)
  - En esta etapa, la primera mano se sigue repartiendo al abrir la aplicación, como en la versión 1. Este comportamiento transitorio se reemplaza en HU-12: en la versión de Economía la ronda comienza solo después de ingresar una apuesta válida.

---

# VERSIÓN 3: Rival
**Objetivo:** juego completo de una ronda contra la banca, con ganador o empate informado. Aún sin apuestas ni saldo.

## HU-09. Que el computador espere y reciba su turno
**Como** jugador, **quiero** que el computador juegue después de mí, **para** enfrentarme a un rival con un turno propio.

**Criterios de aceptación / tareas:**
- **Super tarea A: Zona del computador** (Alta | PDF)
  - La ventana muestra una zona identificada como «Computador», con su propia mano y puntaje.
  - Mientras el jugador juega, el computador permanece sin cartas.
- **Super tarea B: Inicio del turno** (Alta | PDF)
  - Al plantarse el jugador, el computador recibe dos cartas del mismo mazo y comienza su turno.
  - Si el jugador llega a 21 o tiene Blackjack, el turno del computador comienza automáticamente.
  - Si el jugador se pasa de 21, el computador no juega.
- **Tarea C: Mano propia** (Alta | PDF)
  - La mano del computador también es una lista enlazada, con el mismo cálculo de puntaje y de ases del jugador.

## HU-10. Que el computador siga sus reglas
**Como** jugador, **quiero** que el computador actúe siempre con reglas fijas, **para** que el duelo sea predecible y justo.

**Criterios de aceptación / tareas:**
- **Super tarea A: Decisiones del computador** (Alta | PDF)
  - Pide carta mientras su puntaje ajustado sea menor que 17.
  - Se planta con cualquier puntaje de 17 a 21, incluido el 17 blando (As contado como 11).
  - Pierde si supera 21.
- **Tarea B: Blackjack inicial** (Alta | PDF)
  - Reconoce un Blackjack cuando sus dos primeras cartas son As + carta de valor 10.
- **Tarea C: Decisión independiente** (Media | PDF)
  - Sus decisiones dependen solo de su propia mano, sin considerar la del jugador.
- **Tarea D: Secuencia breve** (Media | PDF)
  - Su turno se resuelve con una secuencia breve de operaciones apenas termina el turno del jugador.
- **Tarea E: Mensaje de Veinte y medio** (Baja | Propuesta)
  - No se anuncia Veinte y medio para el computador.

## HU-11. Conocer quién ganó la ronda
**Como** jugador, **quiero** ver el resultado de la ronda, **para** saber si gané, perdí o empaté.

**Criterios de aceptación / tareas:**
- **Super tarea A: Jerarquía de manos** (Alta | PDF)
  - El Blackjack supera a cualquier mano que no sea Blackjack, incluso a un 21 de varias cartas.
  - Blackjack contra Blackjack es empate.
  - Sin Blackjack, gana el puntaje válido más alto; el mismo puntaje es empate.
  - La combinación inicial J/Q/K + 4 (Veinte y medio) vence a cualquier mano válida de hasta 20 puntos, pierde contra cualquier 21 (incluido el Blackjack) y empata contra otra combinación Veinte y medio.
  - El participante que supera 21 pierde; Veinte y medio no da una victoria automática ni un pago adicional.
- **Tarea B: Mensaje de resultado** (Alta | PDF)
  - Al terminar la ronda se informa victoria, derrota o empate, y la razón (ejemplo: «el computador se pasó»).
- **Tarea C: Resultado único** (Alta | PDF)
  - El resultado se determina una sola vez por ronda.
- **Tarea D: Bloqueo al terminar** (Alta | PDF)
  - Terminada la ronda, solo queda habilitado «Iniciar ronda».

---

# VERSIÓN 4: Economía
**Objetivo:** se juega con fichas ficticias: apuestas validadas, pagos según el resultado y fin del juego por saldo insuficiente.

## HU-12. Apostar antes de cada ronda
**Como** jugador, **quiero** ingresar una apuesta antes de repartir, **para** jugar con fichas en cada ronda.

**Criterios de aceptación / tareas:**
- **Tarea A: Saldo inicial** (Alta | PDF)
  - El jugador comienza con 1.000 fichas ficticias.
- **Super tarea B: Campo de apuesta** (Alta | PDF)
  - Existe un campo para ingresar la apuesta.
  - La apuesta debe ser un número entero, múltiplo de 10, de mínimo 10 fichas.
  - La apuesta no puede superar el saldo disponible.
- **Tarea C: Estado previo a la primera ronda** (Alta | PDF)
  - Antes de iniciar la primera ronda no hay una apuesta vigente ni cartas repartidas.
  - Se informa que se debe ingresar una apuesta para comenzar; escribir o corregirla no descuenta fichas.
- **Tarea D: Mensajes de error** (Alta | PDF)
  - Una apuesta inválida o un saldo insuficiente se rechazan con un mensaje claro, sin repartir cartas y sin modificar el saldo.
- **Super tarea E: Descuento y bloqueo** (Alta | PDF)
  - Al validar la apuesta se descuenta del saldo y comienza la ronda.
  - Después del reparto el campo de apuesta queda bloqueado hasta terminar la ronda.
- **Tarea F: Nuevo inicio de ronda** (Alta | PDF)
  - El reparto inicial pasa a ocurrir al iniciar la ronda con una apuesta válida, y deja de ocurrir al abrir la aplicación.

## HU-13. Cobrar o perder según el resultado
**Como** jugador, **quiero** que mi saldo cambie según cómo termine la ronda, **para** ver el efecto real de mis apuestas.

**Criterios de aceptación / tareas:**
- **Super tarea A: Pagos** (Alta | PDF)
  - Derrota: no se acredita nada.
  - Empate: se devuelve la apuesta.
  - Victoria ordinaria: se devuelve la apuesta más una cantidad igual.
  - Victoria con Blackjack: se devuelve la apuesta más 1,5 veces su valor.
- **Tarea B: Actualización única** (Alta | PDF)
  - El saldo se actualiza una sola vez al final de cada ronda.
- **Super tarea C: Información de fichas** (Alta | PDF)
  - El saldo y la apuesta vigente se muestran y se actualizan en pantalla.
  - El resultado indica cuántas fichas se acreditaron.

## HU-14. Terminar el juego cuando me quede sin fichas
**Como** jugador, **quiero** que el juego me avise cuando ya no pueda apostar, **para** entender por qué no puedo continuar.

**Criterios de aceptación / tareas:**
- **Tarea A: Condición de fin** (Alta | PDF)
  - Si el saldo es menor a 10 fichas al terminar una ronda, se activa el fin del juego; con exactamente 10 aún se puede apostar.
- **Super tarea B: Interfaz en fin de juego** (Alta | Propuesta)
  - Se muestra el mensaje «Fin del juego» indicando que no hay fichas suficientes.
  - «Iniciar ronda» y el campo de apuesta quedan deshabilitados.
  - «Cerrar juego» permanece habilitado (el botón se completa en la versión 5).

---

# VERSIÓN 5: Completa
**Objetivo:** pulido y requisitos de interfaz del PDF: cartas con imágenes, reglas, cierre con confirmación, estados de botones coherentes y mensajes claros.

## HU-15. Ver las cartas como imágenes
**Como** jugador, **quiero** ver las cartas de cada mano como imágenes, **para** tener una experiencia más parecida a una mesa real.

**Criterios de aceptación / tareas:**
- **Super tarea A: Vincular cartas con imágenes** (Alta | PDF)
  - Cada una de las 52 cartas se asocia con su imagen de la carpeta `images`, mediante un código de carta (ejemplo: 4P).
  - Las imágenes se cargan una sola vez al abrir la aplicación.
- **Tarea B: Mostrar las manos** (Alta | PDF)
  - Las manos del jugador y del computador se muestran con imágenes, en el orden en que se recibieron.
- **Tarea C: Imagen faltante** (Media | Propuesta)
  - Si falta una imagen, la aplicación lo informa y muestra la carta en texto, en lugar de cerrarse.
- **Tarea D: Ajuste visual** (Baja | Propuesta)
  - Las imágenes mantienen un tamaño uniforme y caben en pantalla con manos largas.

## HU-16. Consultar las reglas sin perder la partida
**Como** jugador, **quiero** abrir las reglas desde un botón, **para** resolver dudas sin perder lo que llevo jugado.

**Criterios de aceptación / tareas:**
- **Super tarea A: Botón «Ver reglas»** (Alta | PDF)
  - Existe el botón «Ver reglas», que muestra una ventana con las reglas resumidas del juego.
  - Las reglas explican el objetivo, los valores de las cartas, el ajuste de ases, las apuestas y sus pagos, las jugadas especiales (incluidas la prioridad de Blackjack y la combinación J/Q/K + 4 para Veinte y medio) y las reglas del computador.
- **Tarea B: Conservar la partida** (Alta | PDF)
  - Al cerrar las reglas, manos, saldo, apuesta y estado de la ronda siguen intactos.

## HU-17. Cerrar el juego con confirmación
**Como** jugador, **quiero** que el juego me pida confirmar antes de cerrarse, **para** no perder la partida por un clic accidental.

**Criterios de aceptación / tareas:**
- **Super tarea A: Botón «Cerrar juego»** (Alta | PDF)
  - Existe el botón «Cerrar juego».
  - Al pulsarlo se muestra una confirmación.
- **Tarea B: Cierre con la X** (Alta | PDF)
  - Cerrar con la X de la ventana muestra la misma confirmación.
- **Tarea C: Cancelar** (Alta | PDF)
  - Si se cancela, la ventana sigue abierta y el estado del juego no cambia.

## HU-18. Tener solo las acciones que corresponden
**Como** jugador, **quiero** que los botones se habiliten solo cuando tienen sentido, **para** no cometer jugadas fuera de turno.

**Criterios de aceptación / tareas:**
- **Super tarea A: Estados del juego** (Alta | PDF)
  - Antes de la primera ronda, las manos del jugador y del computador están vacías, no hay apuesta vigente y un mensaje indica que se debe ingresar una apuesta.
  - Sin ronda en curso: están disponibles «Iniciar ronda», el campo de apuesta, «Ver reglas» y «Cerrar juego»; «Pedir carta» y «Plantarse» permanecen deshabilitados.
  - Turno del jugador: solo «Pedir carta», «Plantarse», «Ver reglas» y «Cerrar juego».
  - Turno del computador y fin de ronda: no se puede pedir carta ni plantarse.
  - Fin del juego por saldo: solo «Ver reglas» y «Cerrar juego».
- **Tarea B: Reglas críticas** (Alta | PDF)
  - No se puede pedir carta fuera del turno del jugador.
  - No se puede cambiar la apuesta después del reparto.
  - No se puede iniciar otra ronda antes de terminar la actual.
- **Tarea C: Mazo agotado** (Media | Propuesta)
  - Si el mazo se agota durante una ronda, no se permite pedir más cartas y la ronda continúa con las reglas ya definidas.

## HU-19. Entender qué está pasando en la mesa
**Como** jugador, **quiero** mensajes claros y toda la información visible, **para** saber de quién es el turno y cómo va la ronda.

**Criterios de aceptación / tareas:**
- **Super tarea A: Información siempre visible** (Alta | PDF)
  - Zonas identificadas para jugador y computador, con espacio para mostrar las imágenes de sus cartas; antes de repartir, ambas zonas están vacías.
  - Puntajes de ambos, saldo y apuesta vigente a la vista.
- **Super tarea B: Mensajes de estado** (Alta | PDF)
  - Se indica el turno actual (apostar, turno del jugador, turno del computador).
  - Se informan las jugadas especiales: Blackjack, Veintiuno, Veinte y medio (solo con J/Q/K + 4 iniciales) y Pasarse.
  - Se informa el resultado final de la ronda.
- **Tarea C: Revisión de textos** (Baja | Propuesta)
  - Los mensajes están en español, son consistentes entre sí y no muestran errores técnicos al usuario.

---

# VERSIÓN 6: Entrega
**Objetivo:** reunir las evidencias de funcionamiento pedidas por el PDF.

## HU-20. Demostrar que el juego funciona
**Como** equipo, **quiero** crear y ejecutar pruebas a medida que se completa cada versión y reunir sus resultados, **para** detectar problemas durante el desarrollo y demostrar que el juego cumple las reglas.

**Criterios de aceptación / tareas:**
- **Super tarea A: Pruebas de la versión 1 — Base** (Alta | PDF)
  - Al crear el juego, el jugador recibe dos cartas y quedan 50 disponibles.
  - Verificar que el mazo usa un arreglo de 52 posiciones y la mano una `LinkedList`.
  - Pedir carta agrega exactamente una carta al final de la mano y reduce en uno las cartas disponibles.
  - Al recorrer el mazo completo no hay cartas repetidas y una extracción adicional se rechaza. Con las reglas de la versión Jugable, la mano no puede agotar el mazo después de que el turno termine automáticamente.
  - Verificar la representación textual de las cartas por valor y palo.
  - Comprobar desde la ventana que se muestran la mano y las cartas restantes, que el botón reparte una carta por clic y que al agotarse el mazo se informa y se deshabilita el botón. Registrar estos casos como pruebas de aceptación manuales mientras no haya pruebas automatizadas de la interfaz.
- **Super tarea B: Pruebas de la versión 2 — Jugable** (Alta | PDF)
  - Verificar valores de cartas, cálculo con As + 9 y luego 5, y manos con varios ases: As + As + 9, As + As + K y As + As + K + K.
  - Distinguir Blackjack inicial (As + 10) de un 21 formado con tres o más cartas; probar Veintiuno y Veinte y medio con J/Q/K + 4 iniciales, con palos iguales y distintos. Confirmar que 9 + 5, As + 3 y 10 + 4 no son Veinte y medio, y que pedir otra carta termina esa jugada especial.
  - Comprobar que al llegar a 21 termina el turno, al superar 21 se pierde y se bloquean las acciones, y que una nueva ronda renueva el mazo y la mano.
- **Super tarea C: Pruebas de la versión 3 — Rival** (Alta | PDF)
  - Comprobar que el computador no recibe cartas antes de su turno, recibe dos al iniciarlo, pide con 16 y se planta con 17 y con As + 6.
  - Comprobar que el computador reconoce Blackjack inicial, que Blackjack vence a un 21 del jugador formado con más de dos cartas y que dos Blackjack empatan sin repetir cartas físicas.
  - Probar que el jugador que se pasa pierde sin turno del computador, que el computador pierde al pasarse y que las combinaciones iniciales J/Q/K + 4 empatan entre sí, vencen a manos válidas de hasta 20 y pierden contra cualquier 21.
- **Super tarea D: Pruebas de la versión 4 — Economía** (Alta | PDF)
  - Rechazar campo vacío, texto, decimales, negativos, cero, valores menores de 10, no múltiplos de 10 y apuestas superiores al saldo, sin repartir ni modificar el saldo.
  - Comprobar que antes de la primera apuesta las manos están vacías y el saldo inicial es 1.000; apostar el saldo completo es válido y el descuento solo ocurre al iniciar la ronda.
  - Con apuesta de 100, validar saldos finales de 900 en derrota, 1.000 en empate, 1.100 en victoria ordinaria y 1.150 en victoria con Blackjack; el empate con Blackjack devuelve solo la apuesta.
  - Comprobar que cada resultado liquida una sola vez, que con menos de 10 fichas no se inicia otra ronda y que con exactamente 10 sí se puede apostar.
- **Super tarea E: Pruebas de la versión 5 — Completa** (Alta | PDF)
  - Verificar correspondencia entre cartas e imágenes y, si se implementa el manejo opcional de imágenes faltantes, comprobar que se informa sin cerrar la aplicación.
  - Abrir y cerrar las reglas antes de una ronda y durante una ronda; confirmar que no cambian manos, mazo, turno, apuesta, saldo ni resultado.
  - Probar el cierre con el botón y con la X antes de una ronda, durante el turno del jugador y después del resultado; al cancelar, conservar la partida, y al confirmar, cerrar.
  - Verificar las acciones habilitadas antes de la primera ronda, durante cada turno, al terminar la ronda y al acabarse las fichas; comprobar que no se aceptan acciones fuera de turno.
- **Tarea F: Ejecución y registro progresivos** (Alta | PDF)
  - Preparar y ejecutar las pruebas correspondientes al completar cada versión; al avanzar, conservar y volver a ejecutar las pruebas anteriores que sigan aplicando.
  - Mantener un formato de nombres consistente (por ejemplo, `V01_HU01_MazoSinRepeticiones`) y registrar para cada caso la versión, la entrada o acción, el resultado esperado y el resultado obtenido.
  - Reunir en esta HU el registro final de las pruebas realizadas durante las versiones 1 a 5.

## HU-21. Documentar el diseño de clases
**Como** equipo, **quiero** un diagrama de clases UML, **para** mostrar cómo está organizada la solución en MVC.

**Criterios de aceptación / tareas:**
- **Super tarea A: Diagrama** (Alta | PDF)
  - Incluye las clases del juego, sus atributos, métodos y relaciones.
  - Refleja el uso del arreglo de 52 posiciones para el mazo y de la lista enlazada para las manos.
  - Distingue claramente lo que pertenece a Modelo, Vista y Controlador.
- **Tarea B: Ajuste frente a la clase** (Media | PDF)
  - Se indica qué responsabilidades del ejercicio visto en clase se ajustaron para incorporar las nuevas reglas.
- **Tarea C: Coherencia con el código** (Alta | PDF)
  - El diagrama coincide con el código final entregado.

---

## 7. Fuera del plan (ideas opcionales, solo si sobra tiempo)
- Animaciones o sonidos al repartir cartas.
- Historial de rondas jugadas.
- Reiniciar el saldo a 1.000 tras un fin de juego.
- Dorso de carta para efectos visuales.

Ninguna de estas se exige en el PDF y no deben afectar la entrega de las versiones 1 a 6.
