package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Call3D extends Cuarteta {

    private String name;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append(name).append("();\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{"call", name, "", ""};
    }
}
