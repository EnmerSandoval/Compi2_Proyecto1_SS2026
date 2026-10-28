package practica.compi2.c3d.cuartetas;

import practica.compi2.riscv.RiscVBuilder;

public class Halt3D extends Cuarteta {

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("return 1;\n");
    }

    // terminar programa
    @Override
    public void toRiscV(RiscVBuilder builder) {
        builder.syscall(10);
    }

    @Override
    public String[] toRow() {
        return new String[]{"halt", "", "", ""};
    }
}
