package practica.compi2.riscv.simulador;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Simulator {

    public static final long DEFAULT_STEP_LIMIT = 500_000_000L;

    private final AssembledProgram program;
    private final List<Instruction> instructions;
    private final SimulatorIO io;
    // registros enteros
    private final long[] x;
    // registros decimales
    private final double[] f;
    // memoria de datos
    private final ByteBuffer memory;
    private int pc;
    private boolean running;
    private long steps;

    public Simulator(AssembledProgram program, SimulatorIO io) {
        this.program = program;
        this.instructions = program.getInstructions();
        this.io = io;
        this.x = new long[32];
        this.f = new double[32];
        this.memory = ByteBuffer.wrap(program.getData()).order(ByteOrder.LITTLE_ENDIAN);
    }

    // ciclo principal
    public long run(long stepLimit) {
        pc = program.getEntryPoint();
        running = true;
        while (running) {
            // fuera del programa
            if (pc < 0 || pc >= instructions.size()) {
                throw new RiscVException("El programa terminó sin llamar a la salida (ecall 10)");
            }
            // posible ciclo infinito
            steps++;
            if (steps > stepLimit) {
                throw new RiscVException("Se superó el límite de " + stepLimit
                        + " instrucciones, puede haber un ciclo infinito");
            }
            execute(instructions.get(pc));
            // zero siempre 0
            x[0] = 0;
        }
        return steps;
    }

    // ejecutar instruccion
    private void execute(Instruction instruction) {
        int r0 = instruction.register(0);
        int r1 = instruction.register(1);
        int r2 = instruction.register(2);
        int r3 = instruction.register(3);
        long immediate = instruction.getImmediate();
        int next = pc + 1;
        switch (instruction.getOp()) {
            case ADD -> x[r0] = x[r1] + x[r2];
            case ADDI -> x[r0] = x[r1] + immediate;
            case SLLI -> x[r0] = x[r1] << immediate;
            case DIV -> x[r0] = x[r1] / divisor(x[r2], instruction);
            case REM -> x[r0] = x[r1] % divisor(x[r2], instruction);
            case LI, LA -> x[r0] = immediate;
            case MV -> x[r0] = x[r1];
            case BEQZ -> next = x[r0] == 0 ? instruction.getTarget() : next;
            case BNEZ -> next = x[r0] != 0 ? instruction.getTarget() : next;
            case BGE -> next = x[r0] >= x[r1] ? instruction.getTarget() : next;
            case J -> next = instruction.getTarget();
            case ECALL -> ecall();
            case FLD -> f[r0] = memory.getDouble(offset(x[r1] + immediate, 8, instruction));
            case FSD -> memory.putDouble(offset(x[r1] + immediate, 8, instruction), f[r0]);
            case FADD_D -> f[r0] = f[r1] + f[r2];
            case FSUB_D -> f[r0] = f[r1] - f[r2];
            case FMUL_D -> f[r0] = f[r1] * f[r2];
            case FDIV_D -> f[r0] = f[r1] / f[r2];
            case FMSUB_D -> f[r0] = Math.fma(f[r1], f[r2], -f[r3]);
            case FEQ_D -> x[r0] = f[r1] == f[r2] ? 1 : 0;
            case FLT_D -> x[r0] = f[r1] < f[r2] ? 1 : 0;
            case FLE_D -> x[r0] = f[r1] <= f[r2] ? 1 : 0;
            case FCVT_D_L -> f[r0] = x[r1];
            case FCVT_L_D -> x[r0] = instruction.getRounding().equals("rtz") ? (long) f[r1] : (long) Math.rint(f[r1]);
            case FMV_D -> f[r0] = f[r1];
            case FNEG_D -> f[r0] = -f[r1];
        }
        pc = next;
    }

    // division entre cero
    private long divisor(long value, Instruction instruction) {
        if (value == 0) {
            throw new RiscVException(instruction.getLine(), "División entre cero en '" + instruction.getSource() + "'");
        }
        return value;
    }

    // validar direccion
    private int offset(long address, int size, Instruction instruction) {
        long relative = address - AssembledProgram.DATA_BASE;
        if (relative < 0 || relative + size > memory.capacity()) {
            throw new RiscVException(instruction.getLine(), "Acceso a la dirección inválida 0x" + Long.toHexString(address)
                    + " en '" + instruction.getSource() + "'");
        }
        return (int) relative;
    }

    // llamadas al sistema
    private void ecall() {
        long a0 = x[10];
        switch ((int) x[17]) {
            case 1 -> write(Long.toString(a0));
            case 4 -> printString(a0);
            case 10 -> running = false;
            case 11 -> io.write((int) (a0 & 0xFF));
            case 12 -> x[10] = io.read();
            default -> throw new RiscVException("Llamada al sistema desconocida a7 = " + x[17]);
        }
    }

    // hasta el cero
    private void printString(long address) {
        int position = (int) (address - AssembledProgram.DATA_BASE);
        while (memory.get(position) != 0) {
            io.write(memory.get(position) & 0xFF);
            position++;
        }
    }

    // escribir en UTF-8
    private void write(String text) {
        for (byte value : text.getBytes(StandardCharsets.UTF_8)) {
            io.write(value & 0xFF);
        }
    }
}
