package practica.compi2.c3d.access;

import practica.compi2.c3d.CodeTransformable;

public abstract class MemoryAccess implements CodeTransformable {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toCCode(sb);
        return sb.toString();
    }
}
