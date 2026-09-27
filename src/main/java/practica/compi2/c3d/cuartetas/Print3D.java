package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;

@AllArgsConstructor
public class Print3D extends Cuarteta {

    private MemoryAccess value;
    private String format;

    @Override
    public void toCCode(StringBuilder sb) {
        if (format.equals("%d")) {
            sb.append("printf(\"%lld\", (long long) ");
        } else if (format.equals("%c")) {
            sb.append("printf(\"%c\", (int) ");
        } else {
            sb.append("printf(\"").append(format).append("\", ");
        }
        value.toCCode(sb);
        sb.append(");\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{"print", value.toString(), format, ""};
    }
}
