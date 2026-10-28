package practica.compi2.c3d.cuartetas;

import java.util.Set;
import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class Conditional3D extends Cuarteta {

    private MemoryAccess left;
    private MemoryAccess right;
    private Integer label;
    private String operator;

    @Override
    public void collectTemporals(Set<Integer> temporals) {
        left.collectTemporals(temporals);
        right.collectTemporals(temporals);
    }

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("if (");
        left.toCCode(sb);
        sb.append(" ").append(operator).append(" ");
        right.toCCode(sb);
        sb.append(") goto et").append(label).append(";\n");
    }

    // comparar y saltar
    @Override
    public void toRiscV(RiscVBuilder builder) {
        left.loadRiscV(builder, "ft0");
        right.loadRiscV(builder, "ft1");
        String target = "et" + label;
        switch (operator) {
            case "==" -> branch(builder, "feq.d t0, ft0, ft1", "bnez", target);
            case "!=" -> branch(builder, "feq.d t0, ft0, ft1", "beqz", target);
            case "<" -> branch(builder, "flt.d t0, ft0, ft1", "bnez", target);
            case "<=" -> branch(builder, "fle.d t0, ft0, ft1", "bnez", target);
            case ">" -> branch(builder, "flt.d t0, ft1, ft0", "bnez", target);
            default -> branch(builder, "fle.d t0, ft1, ft0", "bnez", target);
        }
    }

    // salto condicional
    private void branch(RiscVBuilder builder, String comparison, String jump, String target) {
        builder.instruction(comparison);
        builder.instruction(jump + " t0, " + target);
    }

    @Override
    public String[] toRow() {
        return new String[]{"if " + operator, left.toString(), right.toString(), "et" + label};
    }
}
