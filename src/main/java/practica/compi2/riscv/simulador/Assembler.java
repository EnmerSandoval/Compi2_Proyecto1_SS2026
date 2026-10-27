package practica.compi2.riscv.simulador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {

    private final List<Instruction> instructions;
    private final DataSection data;
    private final Map<String, Integer> textLabels;
    private final Map<String, Long> dataLabels;
    private boolean inText;
    private int line;

    public Assembler() {
        this.instructions = new ArrayList<>();
        this.data = new DataSection();
        this.textLabels = new HashMap<>();
        this.dataLabels = new HashMap<>();
        this.inText = true;
    }

    public AssembledProgram assemble(String source) {
        // primera pasada
        String[] lines = source.split("\r?\n");
        for (line = 1; line <= lines.length; line++) {
            processLine(lines[line - 1].trim());
        }
        // segunda pasada
        for (Instruction instruction : instructions) {
            resolveLabel(instruction);
        }
        return new AssembledProgram(instructions, data.toByteArray(), textLabels.getOrDefault("main", 0));
    }

    private void processLine(String text) {
        // etiqueta al inicio
        int colon = text.indexOf(':');
        if (colon > 0 && text.substring(0, colon).matches("[\\w.]+")) {
            defineLabel(text.substring(0, colon));
            text = text.substring(colon + 1).trim();
        }
        if (text.isEmpty()) {
            return;
        }
        if (text.startsWith(".")) {
            directive(text);
        } else if (inText) {
            instructions.add(parseInstruction(text));
        } else {
            throw new RiscVException(line, "Hay una instrucción dentro de la sección .data");
        }
    }

    // registrar etiqueta
    private void defineLabel(String name) {
        if (textLabels.containsKey(name) || dataLabels.containsKey(name)) {
            throw new RiscVException(line, "La etiqueta '" + name + "' ya fue definida");
        }
        if (inText) {
            textLabels.put(name, instructions.size());
        } else {
            dataLabels.put(name, data.currentAddress());
        }
    }

    // directivas .data/.text
    private void directive(String text) {
        String name = text.split("\\s+")[0];
        String argument = text.substring(name.length()).trim();
        switch (name) {
            case ".text" -> inText = true;
            case ".data" -> inText = false;
            case ".globl" -> {
            }
            default -> data.directive(name, argument, line);
        }
    }

    // leer instruccion
    private Instruction parseInstruction(String text) {
        int space = text.indexOf(' ');
        String mnemonic = space < 0 ? text : text.substring(0, space);
        Op op = Op.fromMnemonic(mnemonic);
        if (op == null) {
            throw new RiscVException(line, "Instrucción desconocida '" + mnemonic + "'");
        }
        String[] operands = space < 0 ? new String[0] : text.substring(space + 1).split(",");
        String[] kinds = op.getOperands();
        if (operands.length != kinds.length) {
            throw new RiscVException(line, "Se esperaban " + kinds.length + " operandos y se encontraron " + operands.length);
        }
        Instruction instruction = new Instruction(op, line, text);
        for (int i = 0; i < kinds.length; i++) {
            addOperand(instruction, kinds[i], operands[i].trim());
        }
        return instruction;
    }

    // operando segun tipo
    private void addOperand(Instruction instruction, String kind, String operand) {
        switch (kind) {
            case "x" -> instruction.addRegister(register(Registers.integer(operand), operand));
            case "f" -> instruction.addRegister(register(Registers.floating(operand), operand));
            case "n" -> instruction.setImmediate(parseNumber(operand));
            case "l" -> instruction.setLabel(operand);
            case "r" -> instruction.setRounding(operand);
            default -> memoryOperand(instruction, operand);
        }
    }

    // formato offset(registro)
    private void memoryOperand(Instruction instruction, String operand) {
        int open = operand.indexOf('(');
        if (open < 0 || !operand.endsWith(")")) {
            throw new RiscVException(line, "Se esperaba un operando de memoria offset(registro) y se encontró '"
                    + operand + "'");
        }
        String base = operand.substring(open + 1, operand.length() - 1);
        instruction.setImmediate(parseNumber(operand.substring(0, open)));
        instruction.addRegister(register(Registers.integer(base), base));
    }

    // resolver saltos
    private void resolveLabel(Instruction instruction) {
        String label = instruction.getLabel();
        if (label == null) {
            return;
        }
        if (instruction.getOp() == Op.LA && dataLabels.containsKey(label)) {
            instruction.setImmediate(dataLabels.get(label));
        } else if (instruction.getOp() != Op.LA && textLabels.containsKey(label)) {
            instruction.setTarget(textLabels.get(label));
        } else {
            throw new RiscVException(instruction.getLine(), "La etiqueta '" + label + "' no existe");
        }
    }

    // validar registro
    private int register(Integer number, String name) {
        if (number == null) {
            throw new RiscVException(line, "'" + name + "' no es un registro válido");
        }
        return number;
    }

    // validar numero
    private long parseNumber(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new RiscVException(line, "'" + text + "' no es un número válido");
        }
    }
}
