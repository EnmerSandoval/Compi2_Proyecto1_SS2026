package practica.compi2.ast;

import java.util.List;

public class AstPrinter {

    private final StringBuilder sb = new StringBuilder();
    private int level = 0;

    public String print(AstNode root) {
        sb.setLength(0);
        level = 0;
        printNode("", root);
        return sb.toString();
    }

    public void value(String label, Object value) {
        if (value != null) {
            writeLine(label + ": " + value.toString().replace("\n", "\\n"));
        }
    }

    public void child(String label, AstNode node) {
        if (node != null) {
            printNode(label + ": ", node);
        }
    }

    public void children(String label, List<? extends AstNode> nodes) {
        if (nodes.isEmpty()) {
            return;
        }
        writeLine(label + ":");
        level++;
        for (AstNode node : nodes) {
            printNode("- ", node);
        }
        level--;
    }

    private void printNode(String prefix, AstNode node) {
        writeLine(prefix + node.getClass().getSimpleName() + " " + node.getPosition());
        level++;
        node.print(this);
        level--;
    }

    private void writeLine(String text) {
        sb.append("    ".repeat(level)).append(text).append("\n");
    }
}
