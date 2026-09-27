package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PrintText3D extends Cuarteta {

    private String text;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("printf(\"").append(text).append("\");\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{"print", "\"" + text + "\"", "", ""};
    }
}
