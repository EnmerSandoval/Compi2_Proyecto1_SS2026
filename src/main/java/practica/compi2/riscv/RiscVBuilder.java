package practica.compi2.riscv;

import java.util.LinkedHashMap;
import java.util.Map;

public class RiscVBuilder {

    // registros base
    public static final String STACK_BASE = "s0";
    public static final String HEAP_BASE = "s1";
    public static final String TEMPORALS_BASE = "s2";
    // punteros P y H
    public static final String STACK_POINTER = "fs0";
    public static final String HEAP_POINTER = "fs1";

    private final StringBuilder text;
    private final Map<String, String> strings;
    private final Map<Double, String> doubles;
    private int labelCount;

    public RiscVBuilder() {
        this.text = new StringBuilder();
        this.strings = new LinkedHashMap<>();
        this.doubles = new LinkedHashMap<>();
    }

    // agregar instruccion
    public void instruction(String instruction) {
        text.append("    ").append(instruction).append("\n");
    }

    // agregar etiqueta
    public void label(String name) {
        text.append(name).append(":\n");
    }

    // etiqueta nueva
    public String newLabel() {
        labelCount++;
        return "rv" + labelCount;
    }

    // etiqueta de cadena
    public String stringLabel(String escapedText) {
        return strings.computeIfAbsent(escapedText, key -> "str" + strings.size());
    }

    // etiqueta de decimal
    public String doubleLabel(double value) {
        return doubles.computeIfAbsent(value, key -> "dbl" + doubles.size());
    }

    // cargar entero
    public void loadInteger(String register, long value) {
        instruction("li " + register + ", " + value);
    }

    // cargar decimal
    public void loadDouble(String floatRegister, double value) {
        // entero exacto
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            if (value == 0) {
                instruction("fcvt.d.l " + floatRegister + ", zero");
                return;
            }
            loadInteger("t1", (long) value);
            instruction("fcvt.d.l " + floatRegister + ", t1");
            return;
        }
        // desde memoria
        instruction("la t1, " + doubleLabel(value));
        instruction("fld " + floatRegister + ", 0(t1)");
    }

    // direccion del temporal
    public String temporalOperand(int temporal) {
        long offset = temporal * 8L;
        if (offset < 2048) {
            return offset + "(" + TEMPORALS_BASE + ")";
        }
        // offset grande
        loadInteger("t1", offset);
        instruction("add t1, " + TEMPORALS_BASE + ", t1");
        return "0(t1)";
    }

    // imprimir caracter
    public void printChar(int character) {
        loadInteger("a0", character);
        syscall(11);
    }

    // llamada al sistema
    public void syscall(int number) {
        loadInteger("a7", number);
        instruction("ecall");
    }

    public String getText() {
        return text.toString();
    }

    // cadenas y decimales
    public void appendData(StringBuilder sb) {
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            sb.append(entry.getValue()).append(": .string \"").append(entry.getKey()).append("\"\n");
        }
        sb.append(".align 3\n");
        for (Map.Entry<Double, String> entry : doubles.entrySet()) {
            sb.append(entry.getValue()).append(": .double ").append(entry.getKey()).append("\n");
        }
    }
}
