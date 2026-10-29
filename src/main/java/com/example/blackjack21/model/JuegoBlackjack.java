package com.example.blackjack21.model;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class JuegoBlackjack {
    public enum SituacionMano {
        EN_JUEGO,
        BLACKJACK,
        VEINTIUNO,
        VEINTE_Y_MEDIO,
        PASARSE
    }

    public enum EstadoRonda {
        ESPERANDO_APUESTA,
        TURNO_JUGADOR,
        TURNO_COMPUTADOR,
        TERMINADA
    }

    public enum ResultadoRonda {
        EN_CURSO,
        VICTORIA,
        DERROTA,
        EMPATE
    }

    private Mazo mazo;
    private final Supplier<Mazo> mazoFactory;
    private final LinkedList<Carta> mano = new LinkedList<>();
    private final LinkedList<Carta> manoComputador = new LinkedList<>();
    private boolean turnoJugadorActivo;
    private SituacionMano situacionMano;
    private SituacionMano situacionComputador;
    private EstadoRonda estadoRonda;
    private ResultadoRonda resultadoRonda;
    private String mensajeResultado;
    private long saldo = 1_000;
    private Long apuestaActual;
    private long fichasAcreditadas;
    private boolean juegoTerminado;

    // Crea una partida nueva usando mazos mezclados.
    public JuegoBlackjack() {
        this(Mazo::new);
    }

    // Prepara el estado inicial y define cómo se crearán los mazos.
    JuegoBlackjack(Supplier<Mazo> mazoFactory) {
        this.mazoFactory = mazoFactory;
        estadoRonda = EstadoRonda.ESPERANDO_APUESTA;
        resultadoRonda = ResultadoRonda.EN_CURSO;
        situacionMano = SituacionMano.EN_JUEGO;
        situacionComputador = SituacionMano.EN_JUEGO;
        mensajeResultado = "Ingresa una apuesta para comenzar.";
    }

    // Inicia una partida de prueba con un mazo indicado.
    JuegoBlackjack(Mazo mazo) {
        this(mazo, Mazo::new);
    }

    // Inicia una partida de prueba con un mazo y su proveedor para nuevas rondas.
    JuegoBlackjack(Mazo mazo, Supplier<Mazo> mazoFactory) {
        this(mazoFactory);
        this.mazo = mazo;
        repartirManoInicial();
    }

    // Valida el texto de la apuesta y comienza una ronda.
    public void iniciarRonda(String apuestaIngresada) {
        if (apuestaIngresada == null || apuestaIngresada.isBlank()) {
            throw new IllegalArgumentException("Ingresa una apuesta.");
        }
        if (!apuestaIngresada.matches("[0-9]+")) {
            throw new IllegalArgumentException("La apuesta debe ser un número entero.");
        }
        final long apuesta;
        try {
            apuesta = Long.parseLong(apuestaIngresada);
        } catch (NumberFormatException excepcion) {
            throw new IllegalArgumentException("La apuesta ingresada está fuera del rango permitido.",
                    excepcion);
        }
        iniciarRonda(apuesta);
    }

    // Valida la cantidad apostada, descuenta fichas y reparte la mano inicial.
    public void iniciarRonda(long apuesta) {
        if (juegoTerminado) {
            throw new IllegalStateException("El juego terminó: no hay fichas suficientes para apostar.");
        }
        if (estadoRonda != EstadoRonda.ESPERANDO_APUESTA
                && estadoRonda != EstadoRonda.TERMINADA) {
            throw new IllegalStateException("No se puede iniciar otra ronda antes de terminar la actual.");
        }
        if (apuesta < 10) {
            throw new IllegalArgumentException("La apuesta mínima es de 10 fichas.");
        }
        if (apuesta % 10 != 0) {
            throw new IllegalArgumentException("La apuesta debe ser múltiplo de 10.");
        }
        if (saldo < 10) {
            throw new IllegalStateException("No hay fichas suficientes para iniciar otra ronda.");
        }
        if (apuesta > saldo) {
            throw new IllegalArgumentException("La apuesta no puede superar el saldo disponible.");
        }
        if (apuesta > Long.MAX_VALUE / 5) {
            throw new IllegalArgumentException("La apuesta es demasiado alta para calcular el pago.");
        }
        long pagoMaximo = apuesta * 5 / 2;
        if (saldo - apuesta > Long.MAX_VALUE - pagoMaximo) {
            throw new IllegalArgumentException("La apuesta es demasiado alta para calcular el pago.");
        }

        apuestaActual = apuesta;
        saldo -= apuesta;
        fichasAcreditadas = 0;
        mazo = mazoFactory.get();
        mano.clear();
        manoComputador.clear();
        repartirManoInicial();
    }

    // Reparte las dos cartas iniciales del jugador y revisa su mano.
    private void repartirManoInicial() {
        mano.add(mazo.sacarCarta());
        mano.add(mazo.sacarCarta());
        turnoJugadorActivo = true;
        estadoRonda = EstadoRonda.TURNO_JUGADOR;
        resultadoRonda = ResultadoRonda.EN_CURSO;
        situacionComputador = SituacionMano.EN_JUEGO;
        mensajeResultado = "";
        actualizarSituacion(true);
        if (situacionMano == SituacionMano.BLACKJACK
                || situacionMano == SituacionMano.VEINTIUNO) {
            jugarTurnoComputador();
        }
    }

    // Añade una carta al jugador y actualiza el estado de la ronda.
    public Carta pedirCarta() {
        if (estadoRonda != EstadoRonda.TURNO_JUGADOR) {
            throw new IllegalStateException("El turno del jugador ya terminó.");
        }
        Carta carta = mazo.sacarCarta();
        mano.add(carta);
        actualizarSituacion(false);
        if (situacionMano == SituacionMano.PASARSE) {
            finalizarRonda(ResultadoRonda.DERROTA,
                    "Perdiste: te pasaste de 21; el computador no jugó.");
        } else if (situacionMano == SituacionMano.VEINTIUNO) {
            jugarTurnoComputador();
        }
        return carta;
    }

    // Termina el turno del jugador y permite jugar al computador.
    public void plantarse() {
        if (estadoRonda != EstadoRonda.TURNO_JUGADOR) {
            throw new IllegalStateException("El turno del jugador ya terminó.");
        }
        turnoJugadorActivo = false;
        jugarTurnoComputador();
    }

    // Actualiza la situación de la mano y si el jugador puede continuar.
    private void actualizarSituacion(boolean manoInicial) {
        situacionMano = evaluarMano(mano, manoInicial);
        turnoJugadorActivo = situacionMano == SituacionMano.EN_JUEGO
                || situacionMano == SituacionMano.VEINTE_Y_MEDIO;
    }

    // Reparte la mano del computador, juega según sus reglas y resuelve la ronda.
    private void jugarTurnoComputador() {
        if (estadoRonda != EstadoRonda.TURNO_JUGADOR) {
            return;
        }
        estadoRonda = EstadoRonda.TURNO_COMPUTADOR;
        turnoJugadorActivo = false;

        manoComputador.add(mazo.sacarCarta());
        manoComputador.add(mazo.sacarCarta());
        situacionComputador = evaluarMano(manoComputador, true);

        if (situacionMano == SituacionMano.BLACKJACK) {
            if (situacionComputador == SituacionMano.BLACKJACK) {
                finalizarRonda(ResultadoRonda.EMPATE,
                        "Empate: ambos tienen Blackjack.");
            } else {
                finalizarRonda(ResultadoRonda.VICTORIA,
                        "Ganaste: tienes Blackjack y el computador no.");
            }
            return;
        }
        if (situacionComputador == SituacionMano.BLACKJACK) {
            finalizarRonda(ResultadoRonda.DERROTA,
                    "Perdiste: el computador tiene Blackjack.");
            return;
        }

        while (calcularPuntaje(manoComputador) < 17) {
            manoComputador.add(mazo.sacarCarta());
            situacionComputador = evaluarMano(manoComputador, false);
        }
        determinarResultado();
    }

    // Compara las manos y define quién gana, pierde o empata.
    private void determinarResultado() {
        int puntajeJugador = calcularPuntaje(mano);
        int puntajeComputador = calcularPuntaje(manoComputador);

        if (situacionComputador == SituacionMano.PASARSE) {
            finalizarRonda(ResultadoRonda.VICTORIA,
                    "Ganaste: el computador se pasó de 21.");
            return;
        }

        if (situacionMano == SituacionMano.VEINTE_Y_MEDIO) {
            if (situacionComputador == SituacionMano.VEINTE_Y_MEDIO) {
                finalizarRonda(ResultadoRonda.EMPATE,
                        "Empate: ambos tienen Veinte y medio.");
            } else if (puntajeComputador == 21) {
                finalizarRonda(ResultadoRonda.DERROTA,
                        "Perdiste: el computador obtuvo 21.");
            } else {
                finalizarRonda(ResultadoRonda.VICTORIA,
                        "Ganaste: Veinte y medio vence a la mano del computador.");
            }
            return;
        }
        if (situacionComputador == SituacionMano.VEINTE_Y_MEDIO) {
            if (puntajeJugador == 21) {
                finalizarRonda(ResultadoRonda.VICTORIA,
                        "Ganaste: tu 21 vence al Veinte y medio del computador.");
            } else {
                finalizarRonda(ResultadoRonda.DERROTA,
                        "Perdiste: Veinte y medio vence a tu mano.");
            }
            return;
        }

        if (puntajeJugador > puntajeComputador) {
            finalizarRonda(ResultadoRonda.VICTORIA,
                    "Ganaste: tu puntaje es mayor que el del computador.");
        } else if (puntajeJugador < puntajeComputador) {
            finalizarRonda(ResultadoRonda.DERROTA,
                    "Perdiste: el puntaje del computador es mayor.");
        } else {
            finalizarRonda(ResultadoRonda.EMPATE,
                    "Empate: ambos tienen el mismo puntaje.");
        }
    }

    // Guarda el resultado, liquida la apuesta y cierra la ronda.
    private void finalizarRonda(ResultadoRonda resultado, String mensaje) {
        if (estadoRonda == EstadoRonda.TERMINADA) {
            return;
        }
        resultadoRonda = resultado;
        liquidarApuesta(resultado);
        mensajeResultado = mensaje + " Fichas acreditadas: " + fichasAcreditadas
                + ". Saldo disponible: " + saldo + ".";
        estadoRonda = EstadoRonda.TERMINADA;
        turnoJugadorActivo = false;
        juegoTerminado = saldo < 10;
        if (juegoTerminado) {
            mensajeResultado += " Fin del juego: no tienes fichas suficientes para otra ronda.";
        }
    }

    // Acredita las fichas correspondientes al resultado de la ronda.
    private void liquidarApuesta(ResultadoRonda resultado) {
        if (apuestaActual == null) {
            fichasAcreditadas = 0;
            return;
        }
        fichasAcreditadas = switch (resultado) {
            case EN_CURSO, DERROTA -> 0;
            case EMPATE -> apuestaActual;
            case VICTORIA -> situacionMano == SituacionMano.BLACKJACK
                    ? apuestaActual * 5 / 2
                    : apuestaActual * 2;
        };
        saldo += fichasAcreditadas;
        apuestaActual = null;
    }

    // Calcula el puntaje de una mano considerando el valor flexible de los ases.
    public static int calcularPuntaje(List<Carta> mano) {
        int puntaje = 0;
        int ases = 0;
        for (Carta carta : mano) {
            puntaje += carta.getPuntajeBase();
            if ("As".equals(carta.valor())) {
                ases++;
            }
        }
        while (puntaje > 21 && ases > 0) {
            puntaje -= 10;
            ases--;
        }
        return puntaje;
    }

    // Identifica la situación actual de una mano.
    public static SituacionMano evaluarMano(List<Carta> mano, boolean manoInicial) {
        int puntaje = calcularPuntaje(mano);
        if (puntaje > 21) {
            return SituacionMano.PASARSE;
        }
        if (manoInicial && mano.size() == 2 && esBlackjack(mano)) {
            return SituacionMano.BLACKJACK;
        }
        if (puntaje == 21) {
            return SituacionMano.VEINTIUNO;
        }
        if (manoInicial && esVeinteYMedio(mano)) {
            return SituacionMano.VEINTE_Y_MEDIO;
        }
        return SituacionMano.EN_JUEGO;
    }

    // Comprueba si la mano inicial contiene una figura y un cuatro.
    private static boolean esVeinteYMedio(List<Carta> mano) {
        if (mano.size() != 2) {
            return false;
        }
        boolean tieneCuatro = mano.stream().anyMatch(carta -> "4".equals(carta.valor()));
        boolean tieneFigura = mano.stream().anyMatch(carta ->
                "J".equals(carta.valor()) || "Q".equals(carta.valor()) || "K".equals(carta.valor()));
        return tieneCuatro && tieneFigura;
    }

    // Comprueba si las dos cartas iniciales forman Blackjack.
    private static boolean esBlackjack(List<Carta> mano) {
        return mano.stream().anyMatch(carta -> "As".equals(carta.valor()))
                && mano.stream().anyMatch(carta -> carta.getPuntajeBase() == 10);
    }

    // Devuelve una copia de la mano del jugador.
    public List<Carta> getMano() {
        return List.copyOf(mano);
    }

    // Devuelve una copia de la mano del computador.
    public List<Carta> getManoComputador() {
        return List.copyOf(manoComputador);
    }

    // Devuelve el puntaje actual del jugador.
    public int getPuntaje() {
        return calcularPuntaje(mano);
    }

    // Devuelve el puntaje actual del computador.
    public int getPuntajeComputador() {
        return calcularPuntaje(manoComputador);
    }

    // Devuelve el saldo disponible del jugador.
    public long getSaldo() {
        return saldo;
    }

    // Devuelve la apuesta de la ronda, si hay una activa.
    public Long getApuestaActual() {
        return apuestaActual;
    }

    // Devuelve cuántas fichas se acreditaron en la última ronda.
    public long getFichasAcreditadas() {
        return fichasAcreditadas;
    }

    // Indica si el saldo ya no permite iniciar otra ronda.
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    // Devuelve la situación actual de la mano del jugador.
    public SituacionMano getSituacionMano() {
        return situacionMano;
    }

    // Devuelve la situación actual de la mano del computador.
    public SituacionMano getSituacionComputador() {
        return situacionComputador;
    }

    // Devuelve el estado actual de la ronda.
    public EstadoRonda getEstadoRonda() {
        return estadoRonda;
    }

    // Devuelve el resultado de la ronda actual o anterior.
    public ResultadoRonda getResultadoRonda() {
        return resultadoRonda;
    }

    // Devuelve el mensaje final de resultado.
    public String getMensajeResultado() {
        return mensajeResultado;
    }

    // Indica si el turno del jugador sigue activo.
    public boolean isTurnoJugadorActivo() {
        return turnoJugadorActivo;
    }

    // Devuelve el mensaje que corresponde al estado actual del juego.
    public String getMensajeEstado() {
        if (estadoRonda == EstadoRonda.ESPERANDO_APUESTA) {
            return mensajeResultado;
        }
        if (estadoRonda == EstadoRonda.TERMINADA) {
            return mensajeResultado;
        }
        if (estadoRonda == EstadoRonda.TURNO_COMPUTADOR) {
            return "Turno del computador.";
        }
        return switch (situacionMano) {
            case BLACKJACK -> "Blackjack. Tu turno ha terminado.";
            case VEINTIUNO -> "Veintiuno. Tu turno ha terminado.";
            case VEINTE_Y_MEDIO -> "Veinte y medio. Puedes pedir carta o plantarte.";
            case PASARSE -> "Pasarse: superaste 21.";
            case EN_JUEGO -> turnoJugadorActivo
                    ? "Tu turno: puedes pedir carta o plantarte."
                    : "Tu turno ha terminado.";
        };
    }

    // Devuelve cuántas cartas quedan en el mazo.
    public int getCartasRestantes() {
        return mazo == null ? 0 : mazo.getCartasRestantes();
    }

    // Indica si el mazo actual está vacío o aún no se ha creado.
    public boolean mazoEstaVacio() {
        return mazo == null || mazo.estaVacio();
    }
}
