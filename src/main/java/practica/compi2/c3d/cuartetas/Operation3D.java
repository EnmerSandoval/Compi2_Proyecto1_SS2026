package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;

@AllArgsConstructor
public class Operation3D extends Cuarteta {

    private MemoryAccess assignable;
    private MemoryAccess first;
    private MemoryAccess second;
    private String operation;

    @Override
    public void toCCode(StringBuilder sb) {
        assignable.toCCode(sb);
        sb.append(" = ");
        if (operation.equals("%")) {
            sb.append("(long long) ");
            first.toCCode(sb);
            sb.append(" % (long long) ");
            second.toCCode(sb);
        } else {
            first.toCCode(sb);
            sb.append(" ").append(operation).append(" ");
            second.toCCode(sb);
        }
        sb.append(";\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{operation, first.toString(), second.toString(), assignable.toString()};
    }
}
