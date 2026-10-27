package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;
import practica.compi2.riscv.RiscVBuilder;

@AllArgsConstructor
public class PrintText3D extends Cuarteta {

    private String text;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("printf(\"").append(text).append("\");\n");
    }

    // imprimir texto
    @Override
    public void toRiscV(RiscVBuilder rb) {
        rb.instruction("la a0, " + rb.stringLabel(text));
        rb.syscall(4);
    }

    @Override
    public String[] toRow() {
        return new String[]{"print", "\"" + text + "\"", "", ""};
    }
}
