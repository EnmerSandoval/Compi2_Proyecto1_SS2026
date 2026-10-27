package practica.compi2.c3d.cuartetas;

import java.util.Set;
import practica.compi2.c3d.CodeTransformable;
import practica.compi2.riscv.RiscVBuilder;

public abstract class Cuarteta implements CodeTransformable {

    public abstract String[] toRow();

    // traduccion a RISC-V
    public abstract void toRiscV(RiscVBuilder rb);

    public void collectTemporals(Set<Integer> temporals) {
    }
}
