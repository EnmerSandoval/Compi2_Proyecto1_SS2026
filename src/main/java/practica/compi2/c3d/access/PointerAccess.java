package practica.compi2.c3d.access;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PointerAccess extends MemoryAccess {

    public static final PointerAccess STACK = new PointerAccess("P");
    public static final PointerAccess HEAP = new PointerAccess("H");

    private String name;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append(name);
    }
}
