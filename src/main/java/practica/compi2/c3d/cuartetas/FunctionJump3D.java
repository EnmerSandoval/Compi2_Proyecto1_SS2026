package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class FunctionJump3D extends Cuarteta {

    private String name;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("goto ").append(name).append(";\n");
    }

    // saltar al metodo
    @Override
    public void toRiscV(RiscVBuilder builder) {
        builder.instruction("j " + name);
    }

    @Override
    public String[] toRow() {
        return new String[]{"goto", "", "", name};
    }
}
