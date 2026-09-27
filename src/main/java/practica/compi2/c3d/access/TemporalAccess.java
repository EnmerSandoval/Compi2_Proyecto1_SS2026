package practica.compi2.c3d.access;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TemporalAccess extends MemoryAccess {

    private Integer temp;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("t").append(temp);
    }
}
