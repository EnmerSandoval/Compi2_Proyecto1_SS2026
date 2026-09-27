package practica.compi2.c3d.access;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HeapAccess extends MemoryAccess {

    private MemoryAccess internalAccess;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("heap[(int) ");
        internalAccess.toCCode(sb);
        sb.append("]");
    }
}
