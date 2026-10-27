package practica.compi2.c3d.cuartetas;

import java.util.Set;
import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class Operation3D extends Cuarteta {

    private MemoryAccess assignable;
    private MemoryAccess first;
    private MemoryAccess second;
    private String operation;

    @Override
    public void collectTemporals(Set<Integer> temporals) {
        assignable.collectTemporals(temporals);
        first.collectTemporals(temporals);
        second.collectTemporals(temporals);
    }

    @Override
    public void toCCode(StringBuilder sb) {
        assignable.toCCode(sb);
        sb.append(" = ");
        if (operation.equals("%")) {
            sb.append("(long long) ");
            first.toCCode(sb);
            sb.append(" % (long long) ");
            second.toCCode(sb);
        } else {
            first.toCCode(sb);
            sb.append(" ").append(operation).append(" ");
            second.toCCode(sb);
        }
        sb.append(";\n");
    }

    // operar dos valores
    @Override
    public void toRiscV(RiscVBuilder rb) {
        first.loadRiscV(rb, "ft0");
        second.loadRiscV(rb, "ft1");
        switch (operation) {
            case "+" -> rb.instruction("fadd.d ft0, ft0, ft1");
            case "-" -> rb.instruction("fsub.d ft0, ft0, ft1");
            case "*" -> rb.instruction("fmul.d ft0, ft0, ft1");
            case "/" -> rb.instruction("fdiv.d ft0, ft0, ft1");
            default -> {
                rb.instruction("fcvt.l.d t0, ft0, rtz");
                rb.instruction("fcvt.l.d t1, ft1, rtz");
                rb.instruction("rem t0, t0, t1");
                rb.instruction("fcvt.d.l ft0, t0");
            }
        }
        assignable.storeRiscV(rb, "ft0");
    }

    @Override
    public String[] toRow() {
        return new String[]{operation, first.toString(), second.toString(), assignable.toString()};
    }
}
