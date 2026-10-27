package practica.compi2.c3d.cuartetas;

import java.util.Set;
import lombok.AllArgsConstructor;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class Print3D extends Cuarteta {

    private MemoryAccess value;
    private String format;

    @Override
    public void collectTemporals(Set<Integer> temporals) {
        value.collectTemporals(temporals);
    }

    @Override
    public void toCCode(StringBuilder sb) {
        if (format.equals("%d")) {
            sb.append("printf(\"%lld\", (long long) ");
        } else if (format.equals("%c")) {
            sb.append("printf(\"%c\", (int) ");
        } else {
            sb.append("printf(\"").append(format).append("\", ");
        }
        value.toCCode(sb);
        sb.append(");\n");
    }

    // imprimir valor
    @Override
    public void toRiscV(RiscVBuilder rb) {
        value.loadRiscV(rb, "ft0");
        if (format.equals("%d")) {
            rb.instruction("fcvt.l.d a0, ft0, rtz");
            rb.syscall(1);
        } else if (format.equals("%c")) {
            rb.instruction("fcvt.l.d a0, ft0, rtz");
            rb.syscall(11);
        } else {
            printTwoDecimals(rb);
        }
    }

    // imprimir con %.2f
    private void printTwoDecimals(RiscVBuilder rb) {
        printSign(rb);
        roundToCents(rb);
        printCents(rb);
    }

    // signo negativo
    private void printSign(RiscVBuilder rb) {
        String positive = rb.newLabel();
        rb.instruction("fcvt.d.l ft2, zero");
        rb.instruction("flt.d t0, ft0, ft2");
        rb.instruction("beqz t0, " + positive);
        rb.printChar('-');
        rb.instruction("fneg.d ft0, ft0");
        rb.label(positive);
    }

    // redondear a centavos
    private void roundToCents(RiscVBuilder rb) {
        String notAbove = rb.newLabel();
        String rounded = rb.newLabel();
        rb.loadDouble("ft1", 100);
        rb.instruction("fmul.d ft2, ft0, ft1");
        rb.instruction("fcvt.l.d t2, ft2, rne");
        rb.instruction("fcvt.d.l ft2, t2");
        rb.instruction("fmsub.d ft2, ft0, ft1, ft2");
        rb.loadDouble("ft3", 0.5);
        rb.instruction("flt.d t0, ft3, ft2");
        rb.instruction("beqz t0, " + notAbove);
        rb.instruction("addi t2, t2, 1");
        rb.instruction("j " + rounded);
        rb.label(notAbove);
        rb.instruction("fneg.d ft3, ft3");
        rb.instruction("flt.d t0, ft2, ft3");
        rb.instruction("beqz t0, " + rounded);
        rb.instruction("addi t2, t2, -1");
        rb.label(rounded);
    }

    // parte entera y decimales
    private void printCents(RiscVBuilder rb) {
        String twoDigits = rb.newLabel();
        rb.loadInteger("t3", 100);
        rb.instruction("div a0, t2, t3");
        rb.syscall(1);
        rb.printChar('.');
        rb.instruction("rem t2, t2, t3");
        rb.loadInteger("t3", 10);
        rb.instruction("bge t2, t3, " + twoDigits);
        rb.printChar('0');
        rb.label(twoDigits);
        rb.instruction("mv a0, t2");
        rb.syscall(1);
    }

    @Override
    public String[] toRow() {
        return new String[]{"print", value.toString(), format, ""};
    }
}
