package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class Goto3D extends Cuarteta {

    private Integer label;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("goto et").append(label).append(";\n");
    }

    // salto directo
    @Override
    public void toRiscV(RiscVBuilder builder) {
        builder.instruction("j et" + label);
    }

    @Override
    public String[] toRow() {
        return new String[]{"goto", "", "", "et" + label};
    }
}
