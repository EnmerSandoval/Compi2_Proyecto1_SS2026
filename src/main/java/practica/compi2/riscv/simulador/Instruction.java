package practica.compi2.riscv.simulador;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Instruction {

    private Op op;
    private int line;
    private String source;
    private int[] registers;
    private int registerCount;
    private long immediate;
    private String label;
    // indice del salto
    private int target;
    private String rounding;

    public Instruction(Op op, int line, String source) {
        this.op = op;
        this.line = line;
        this.source = source;
        this.registers = new int[4];
    }

    public void addRegister(int register) {
        registers[registerCount] = register;
        registerCount++;
    }

    public int register(int position) {
        return registers[position];
    }
}
