package practica.compi2.c3d.access;

import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class Literal3D extends MemoryAccess {

    private Object literal;

    // cargar constante
    @Override
    public void loadRiscV(RiscVBuilder rb, String floatRegister) {
        if (literal instanceof Number number) {
            rb.loadDouble(floatRegister, number.doubleValue());
            return;
        }
        rb.loadDouble(floatRegister, Double.parseDouble(literal.toString()));
    }

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append(literal);
    }
}
