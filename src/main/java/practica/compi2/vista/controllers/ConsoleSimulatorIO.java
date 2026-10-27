package practica.compi2.vista.controllers;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import practica.compi2.riscv.simulador.SimulatorIO;
import practica.compi2.vista.IdeConsole;

public class ConsoleSimulatorIO implements SimulatorIO {

    private final JFrame frame;
    private final IdeConsole console;
    private final ByteArrayOutputStream output;
    private final Queue<Integer> input;
    private boolean endOfInput;

    public ConsoleSimulatorIO(JFrame frame, IdeConsole console) {
        this.frame = frame;
        this.console = console;
        this.output = new ByteArrayOutputStream();
        this.input = new ArrayDeque<>();
    }

    @Override
    public int read() {
        flush();
        if (input.isEmpty() && !endOfInput) {
            askLine();
        }
        return input.isEmpty() ? -1 : input.poll();
    }

    @Override
    public void write(int value) {
        output.write(value);
        if (value == '\n') {
            flush();
        }
    }

    public void flush() {
        if (output.size() == 0) {
            return;
        }
        String text = output.toString(StandardCharsets.UTF_8);
        output.reset();
        SwingUtilities.invokeLater(() -> console.print(text));
    }

    private void askLine() {
        String[] line = new String[1];
        try {
            SwingUtilities.invokeAndWait(() -> line[0] = JOptionPane.showInputDialog(frame,
                    "Entrada para el programa:", "Lectura", JOptionPane.QUESTION_MESSAGE));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            line[0] = null;
        }
        if (line[0] == null) {
            endOfInput = true;
            return;
        }
        SwingUtilities.invokeLater(() -> console.println(line[0]));
        for (byte value : (line[0] + "\n").getBytes(StandardCharsets.UTF_8)) {
            input.add(value & 0xFF);
        }
    }
}
