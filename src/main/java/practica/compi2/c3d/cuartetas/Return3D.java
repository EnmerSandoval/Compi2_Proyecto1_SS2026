package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class Return3D extends Cuarteta {

    private Integer endLabel;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("goto et").append(endLabel).append(";\n");
    }

    // saltar al final
    @Override
    public void toRiscV(RiscVBuilder rb) {
        rb.instruction("j et" + endLabel);
    }

    @Override
    public String[] toRow() {
        return new String[]{"return", "", "", "et" + endLabel};
    }
}
