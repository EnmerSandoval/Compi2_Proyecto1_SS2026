package practica.compi2.c3d.cuartetas;

import java.util.Set;
import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class Assignation3D extends Cuarteta {

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
        sb.append(" = ");
        value.toCCode(sb);
        sb.append(";\n");
    }

    // copiar valor
    @Override
    public void toRiscV(RiscVBuilder builder) {
        value.loadRiscV(builder, "ft0");
        assignable.storeRiscV(builder, "ft0");
    }

    @Override
    public String[] toRow() {
        return new String[]{"=", value.toString(), "", assignable.toString()};
    }
}
