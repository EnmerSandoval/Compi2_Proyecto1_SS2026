package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import practica.compi2.riscv.RiscVBuilder;

@Getter
@AllArgsConstructor
public class FunctionStart3D extends Cuarteta {

    private String name;
    private boolean main;
    private int endLabel;

    @Override
    public void toCCode(StringBuilder sb) {
        if (!main) {
            sb.append("\n").append(name).append(":\n");
        }
    }

    // etiqueta de funcion
    @Override
    public void toRiscV(RiscVBuilder rb) {
        if (!main) {
            rb.label(name);
        }
    }

    @Override
    public String[] toRow() {
        return new String[]{"inicio", name, "", ""};
    }
}
