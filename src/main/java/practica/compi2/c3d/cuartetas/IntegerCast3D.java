package practica.compi2.c3d.cuartetas;

import java.util.Set;
import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class IntegerCast3D extends Cuarteta {

    private MemoryAccess assignable;
    private MemoryAccess value;

    @Override
    public void collectTemporals(Set<Integer> temporals) {
        assignable.collectTemporals(temporals);
        value.collectTemporals(temporals);
    }

    @Override
    public void toCCode(StringBuilder sb) {
        assignable.toCCode(sb);
        sb.append(" = (long long) ");
        value.toCCode(sb);
        sb.append(";\n");
    }

    // truncar a entero
    @Override
    public void toRiscV(RiscVBuilder builder) {
        value.loadRiscV(builder, "ft0");
        builder.instruction("fcvt.l.d t0, ft0, rtz");
        builder.instruction("fcvt.d.l ft0, t0");
        assignable.storeRiscV(builder, "ft0");
    }

    @Override
    public String[] toRow() {
        return new String[]{"(int)", value.toString(), "", assignable.toString()};
    }
}
