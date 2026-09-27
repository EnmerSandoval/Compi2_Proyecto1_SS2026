package practica.compi2.c3d.access;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Literal3D extends MemoryAccess {

    private Object literal;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append(literal);
    }
}
