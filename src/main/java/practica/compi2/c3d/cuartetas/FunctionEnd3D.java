package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import practica.compi2.riscv.RiscVBuilder;

@Getter
@AllArgsConstructor
public class FunctionEnd3D extends Cuarteta {

    private String name;
    private boolean main;

    @Override
    public void toCCode(StringBuilder sb) {
        if (main) {
            sb.append("    return 0;\n");
        }
    }

    // salir del programa
    @Override
    public void toRiscV(RiscVBuilder builder) {
        if (main) {
            builder.syscall(10);
        }
    }

    @Override
    public String[] toRow() {
        return new String[]{"fin", name, "", ""};
    }
}
