package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FunctionEnd3D extends Cuarteta {

    private String name;
    private boolean main;

    @Override
    public void toCCode(StringBuilder sb) {
        if (main) {
            sb.append("    return 0;\n}\n");
        } else {
            sb.append("    return;\n}\n\n");
        }
    }

    @Override
    public String[] toRow() {
        return new String[]{"fin", name, "", ""};
    }
}
