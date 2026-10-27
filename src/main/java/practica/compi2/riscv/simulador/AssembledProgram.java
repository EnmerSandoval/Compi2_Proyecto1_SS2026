package practica.compi2.riscv.simulador;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssembledProgram {

    // inicio de .data
    public static final long DATA_BASE = 0x10000000L;

    private List<Instruction> instructions;
    private byte[] data;
    private int entryPoint;
}
