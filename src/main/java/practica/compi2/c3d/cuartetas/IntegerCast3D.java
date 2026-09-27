package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;

@AllArgsConstructor
public class IntegerCast3D extends Cuarteta {

    private MemoryAccess assignable;
    private MemoryAccess value;

    @Override
    public void toCCode(StringBuilder sb) {
        assignable.toCCode(sb);
        sb.append(" = (long long) ");
        value.toCCode(sb);
        sb.append(";\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{"(int)", value.toString(), "", assignable.toString()};
    }
}
