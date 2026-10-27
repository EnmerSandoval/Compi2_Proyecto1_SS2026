package practica.compi2.riscv.simulador;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

public class StreamIO implements SimulatorIO {

    private final InputStream input;
    private final OutputStream output;

    public StreamIO(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public int read() {
        try {
            output.flush();
            return input.read();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void write(int value) {
        try {
            output.write(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
