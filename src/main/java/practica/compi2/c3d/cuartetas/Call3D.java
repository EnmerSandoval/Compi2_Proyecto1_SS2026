package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class Call3D extends Cuarteta {

    private String name;
    private Integer returnLabel;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("goto ").append(name).append(";\n");
    }

    // saltar a funcion
    @Override
    public void toRiscV(RiscVBuilder rb) {
        rb.instruction("j " + name);
    }

    @Override
    public String[] toRow() {
        return new String[]{"call", name, "", "et" + returnLabel};
    }
}
