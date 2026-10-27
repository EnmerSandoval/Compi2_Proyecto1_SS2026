package practica.compi2.c3d.access;

import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class PointerAccess extends MemoryAccess {

    public static final PointerAccess STACK = new PointerAccess("P", RiscVBuilder.STACK_POINTER);
    public static final PointerAccess HEAP = new PointerAccess("H", RiscVBuilder.HEAP_POINTER);

    private String name;
    private String register;

    // leer P o H
    @Override
    public void loadRiscV(RiscVBuilder rb, String floatRegister) {
        rb.instruction("fmv.d " + floatRegister + ", " + register);
    }

    // cambiar P o H
    @Override
    public void storeRiscV(RiscVBuilder rb, String floatRegister) {
        rb.instruction("fmv.d " + register + ", " + floatRegister);
    }

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append(name);
    }
}
