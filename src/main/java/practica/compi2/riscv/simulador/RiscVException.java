package practica.compi2.riscv.simulador;

public class RiscVException extends RuntimeException {

    public RiscVException(int line, String message) {
        super("Línea " + line + ": " + message);
    }

    public RiscVException(String message) {
        super(message);
    }
}
