package practica.compi2.c3d.access;

import lombok.Getter;
import lombok.Setter;
import practica.compi2.riscv.RiscVBuilder;

@Getter
@Setter
public class FrameOffset3D extends MemoryAccess {

    private int value;

    public FrameOffset3D(int value) {
        this.value = value;
    }

    // cargar desplazamiento
    @Override
    public void loadRiscV(RiscVBuilder rb, String floatRegister) {
        rb.loadDouble(floatRegister, value);
    }

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append(value);
    }
}
