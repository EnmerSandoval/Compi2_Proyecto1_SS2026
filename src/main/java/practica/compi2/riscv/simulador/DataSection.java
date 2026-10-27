package practica.compi2.riscv.simulador;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DataSection {

    private final ByteArrayOutputStream bytes;

    public DataSection() {
        this.bytes = new ByteArrayOutputStream();
    }

    public long currentAddress() {
        return AssembledProgram.DATA_BASE + bytes.size();
    }

    public byte[] toByteArray() {
        return bytes.toByteArray();
    }

    // directivas de datos
    public void directive(String name, String argument, int line) {
        switch (name) {
            case ".space" -> bytes.writeBytes(new byte[Integer.parseInt(argument)]);
            case ".align" -> align(1 << Integer.parseInt(argument));
            case ".double" -> writeDouble(Double.parseDouble(argument));
            case ".string" -> writeString(argument, line);
            default -> throw new RiscVException(line, "Directiva desconocida " + name);
        }
    }

    private void align(int size) {
        while (bytes.size() % size != 0) {
            bytes.write(0);
        }
    }

    private void writeDouble(double value) {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putDouble(value);
        bytes.writeBytes(buffer.array());
    }

    private void writeString(String argument, int line) {
        if (argument.length() < 2 || !argument.startsWith("\"") || !argument.endsWith("\"")) {
            throw new RiscVException(line, "Se esperaba una cadena entre comillas");
        }
        String content = argument.substring(1, argument.length() - 1).replace("\\n", "\n");
        bytes.writeBytes(content.getBytes(StandardCharsets.UTF_8));
        // fin de cadena
        bytes.write(0);
    }
}
