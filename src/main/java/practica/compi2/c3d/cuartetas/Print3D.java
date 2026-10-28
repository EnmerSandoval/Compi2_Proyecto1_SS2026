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
    public void toRiscV(RiscVBuilder builder) {
        value.loadRiscV(builder, "ft0");
        if (format.equals("%d")) {
            builder.instruction("fcvt.l.d a0, ft0, rtz");
            builder.syscall(1);
        } else if (format.equals("%c")) {
            builder.instruction("fcvt.l.d a0, ft0, rtz");
            builder.syscall(11);
        } else {
            printTwoDecimals(builder);
        }
    }

    // imprimir con %.2f
    private void printTwoDecimals(RiscVBuilder builder) {
        printSign(builder);
        roundToCents(builder);
        printCents(builder);
    }

    // signo negativo
    private void printSign(RiscVBuilder builder) {
        String positive = builder.newLabel();
        builder.instruction("fcvt.d.l ft2, zero");
        builder.instruction("flt.d t0, ft0, ft2");
        builder.instruction("beqz t0, " + positive);
        builder.printChar('-');
        builder.instruction("fneg.d ft0, ft0");
        builder.label(positive);
    }

    // redondear a centavos
    private void roundToCents(RiscVBuilder builder) {
        String notAbove = builder.newLabel();
        String rounded = builder.newLabel();
        builder.loadDouble("ft1", 100);
        builder.instruction("fmul.d ft2, ft0, ft1");
        builder.instruction("fcvt.l.d t2, ft2, rne");
        builder.instruction("fcvt.d.l ft2, t2");
        builder.instruction("fmsub.d ft2, ft0, ft1, ft2");
        builder.loadDouble("ft3", 0.5);
        builder.instruction("flt.d t0, ft3, ft2");
        builder.instruction("beqz t0, " + notAbove);
        builder.instruction("addi t2, t2, 1");
        builder.instruction("j " + rounded);
        builder.label(notAbove);
        builder.instruction("fneg.d ft3, ft3");
        builder.instruction("flt.d t0, ft2, ft3");
        builder.instruction("beqz t0, " + rounded);
        builder.instruction("addi t2, t2, -1");
        builder.label(rounded);
    }

    // parte entera y decimales
    private void printCents(RiscVBuilder builder) {
        String twoDigits = builder.newLabel();
        builder.loadInteger("t3", 100);
        builder.instruction("div a0, t2, t3");
        builder.syscall(1);
        builder.printChar('.');
        builder.instruction("rem t2, t2, t3");
        builder.loadInteger("t3", 10);
        builder.instruction("bge t2, t3, " + twoDigits);
        builder.printChar('0');
        builder.label(twoDigits);
        builder.instruction("mv a0, t2");
        builder.syscall(1);
    }

    @Override
    public String[] toRow() {
        return new String[]{"print", value.toString(), format, ""};
    }
}
