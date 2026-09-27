package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;

@AllArgsConstructor
public class Conditional3D extends Cuarteta {

    private MemoryAccess left;
    private MemoryAccess right;
    private Integer label;
    private String operator;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("if (");
        left.toCCode(sb);
        sb.append(" ").append(operator).append(" ");
        right.toCCode(sb);
        sb.append(") goto et").append(label).append(";\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{"if " + operator, left.toString(), right.toString(), "et" + label};
    }
}
