package practica.compi2.c3d.access;

import java.util.Set;
import practica.compi2.c3d.CodeTransformable;
import practica.compi2.riscv.RiscVBuilder;

public abstract class MemoryAccess implements CodeTransformable {

    public void collectTemporals(Set<Integer> temporals) {
    }

    // cargar a registro
    public abstract void loadRiscV(RiscVBuilder rb, String floatRegister);

    // guardar desde registro
    public void storeRiscV(RiscVBuilder rb, String floatRegister) {
        throw new IllegalStateException("No se puede asignar un valor a " + this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toCCode(sb);
        return sb.toString();
    }
}
