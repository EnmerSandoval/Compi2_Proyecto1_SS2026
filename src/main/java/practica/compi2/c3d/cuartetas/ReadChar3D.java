package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;

@AllArgsConstructor
public class ReadChar3D extends Cuarteta {

    private MemoryAccess assignable;

    @Override
    public void toCCode(StringBuilder sb) {
        assignable.toCCode(sb);
        sb.append(" = getchar();\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{"read", "", "", assignable.toString()};
    }
}
