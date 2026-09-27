package practica.compi2.c3d.access;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StackAccess extends MemoryAccess {

    private MemoryAccess internalAccess;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("stack[(int) ");
        internalAccess.toCCode(sb);
        sb.append("]");
    }
}
