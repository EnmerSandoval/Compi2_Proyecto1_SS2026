package practica.compi2.riscv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.cuartetas.Cuarteta;

public class RiscVGenerator {

    public static final int STACK_SIZE = 100000;
    public static final int HEAP_SIZE = 1000000;

    public String generateCode(IntermediateCodeUtils u) {
        RiscVBuilder rb = new RiscVBuilder();
        // traducir cuartetas
        for (Cuarteta cuarteta : u.getMainFirst()) {
            cuarteta.toRiscV(rb);
        }
        StringBuilder sb = new StringBuilder();
        // seccion de datos
        sb.append(".data\n");
        sb.append("stack: .space ").append(STACK_SIZE * 8).append("\n");
        sb.append("heap: .space ").append(HEAP_SIZE * 8).append("\n");
        sb.append("temporales: .space ").append(Math.max(1, u.getCounter().getTemporalCount()) * 8).append("\n");
        rb.appendData(sb);
        // inicializar registros base
        sb.append("""

                .text
                .globl main
                main:
                    la s0, stack
                    la s1, heap
                    la s2, temporales
                    fcvt.d.l fs0, zero
                    li t0, 1
                    fcvt.d.l fs1, t0
                """);
        sb.append(rb.getText());
        return sb.toString();
    }

    // guardar programa.s
    public String generateFile(String code, Path folder) {
        Path file = folder.resolve("programa.s");
        try {
            Files.writeString(file, code);
        } catch (IOException e) {
            return "No se pudo guardar el archivo RISC-V: " + e.getMessage();
        }
        return "Archivo RISC-V generado en: " + file.toAbsolutePath();
    }
}
