package practica.compi2.c3d.access;

import java.util.Set;
import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class StackAccess extends MemoryAccess {

    private MemoryAccess internalAccess;

    @Override
    public void collectTemporals(Set<Integer> temporals) {
        internalAccess.collectTemporals(temporals);
    }

    // leer del stack
    @Override
    public void loadRiscV(RiscVBuilder builder, String floatRegister) {
        address(builder);
        builder.instruction("fld " + floatRegister + ", 0(t0)");
    }

    // escribir en stack
    @Override
    public void storeRiscV(RiscVBuilder builder, String floatRegister) {
        address(builder);
        builder.instruction("fsd " + floatRegister + ", 0(t0)");
    }

    // direccion en stack
    private void address(RiscVBuilder builder) {
        internalAccess.loadRiscV(builder, "ft3");
        builder.instruction("fcvt.l.d t0, ft3, rtz");
        builder.instruction("slli t0, t0, 3");
        builder.instruction("add t0, " + RiscVBuilder.STACK_BASE + ", t0");
    }

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("stack[(int) ");
        internalAccess.toCCode(sb);
        sb.append("]");
    }
}
