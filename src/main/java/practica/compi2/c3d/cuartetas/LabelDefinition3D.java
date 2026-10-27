package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class LabelDefinition3D extends Cuarteta {

    private Integer label;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("et").append(label).append(":\n");
    }

    // definir etiqueta
    @Override
    public void toRiscV(RiscVBuilder rb) {
        rb.label("et" + label);
    }

    @Override
    public String[] toRow() {
        return new String[]{"etiqueta", "", "", "et" + label};
    }
}
