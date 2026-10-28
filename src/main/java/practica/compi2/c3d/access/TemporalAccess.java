package practica.compi2.c3d.access;

import java.util.Set;
import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class TemporalAccess extends MemoryAccess {

    private Integer temp;

    @Override
    public void collectTemporals(Set<Integer> temporals) {
        temporals.add(temp);
    }

    // leer temporal
    @Override
    public void loadRiscV(RiscVBuilder builder, String floatRegister) {
        builder.instruction("fld " + floatRegister + ", " + builder.temporalOperand(temp));
    }

    // guardar temporal
    @Override
    public void storeRiscV(RiscVBuilder builder, String floatRegister) {
        builder.instruction("fsd " + floatRegister + ", " + builder.temporalOperand(temp));
    }

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("t").append(temp);
    }
}
