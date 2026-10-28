package practica.compi2.c3d.cuartetas;

import java.util.Set;
import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class ReadChar3D extends Cuarteta {

    private MemoryAccess assignable;

    @Override
    public void collectTemporals(Set<Integer> temporals) {
        assignable.collectTemporals(temporals);
    }

    @Override
    public void toCCode(StringBuilder sb) {
        assignable.toCCode(sb);
        sb.append(" = getchar();\n");
    }

    // leer caracter
    @Override
    public void toRiscV(RiscVBuilder builder) {
        builder.syscall(12);
        builder.instruction("fcvt.d.l ft0, a0");
        assignable.storeRiscV(builder, "ft0");
    }

    @Override
    public String[] toRow() {
        return new String[]{"read", "", "", assignable.toString()};
    }
}
