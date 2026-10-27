package practica.compi2.vista.editor;

import java.awt.Color;
import java.util.Set;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import org.antlr.v4.runtime.Vocabulary;

public class TokenStyles {

    private static final Set<String> TYPES = Set.of(
            "ENTERO", "FLOTANTE", "CADENA", "CARACTER", "BOOL",
            "INT", "DOUBLE", "CHAR", "BOOLEAN", "STRING", "VOID",
            "NUMERUS", "DECIMALIS", "TEXTUM", "LITTERA");

    private final JTextPane editor;
    private final Style normal;
    private final Style keyword;
    private final Style type;
    private final Style text;
    private final Style number;
    private final Style comment;
    private final Style operator;

    public TokenStyles(JTextPane editor) {
        this.editor = editor;
        normal = createStyle("normal", Color.BLACK, false);
        keyword = createStyle("palabraReservada", new Color(0, 51, 153), true);
        type = createStyle("tipo", new Color(0, 128, 128), true);
        text = createStyle("cadena", new Color(0, 128, 0), false);
        number = createStyle("numero", new Color(204, 102, 0), false);
        comment = createStyle("comentario", Color.GRAY, false);
        StyleConstants.setItalic(comment, true);
        operator = createStyle("operador", new Color(153, 0, 0), false);
    }

    public Style getNormal() {
        return normal;
    }

    public Style styleFor(Vocabulary vocabulary, int tokenType) {
        String name = vocabulary.getSymbolicName(tokenType);
        String literal = vocabulary.getLiteralName(tokenType);
        if (name == null) {
            return normal;
        }
        if (literal != null) {
            return styleForFixedText(name, literal);
        }
        return styleForVariableText(name);
    }

    private Style styleForFixedText(String name, String literal) {
        if (TYPES.contains(name)) {
            return type;
        }
        if (isWord(literal)) {
            return keyword;
        }
        return operator;
    }

    private Style styleForVariableText(String name) {
        if (name.endsWith("COMMENT")) {
            return comment;
        }
        if (name.equals("STRING_LIT") || name.equals("CHAR_LIT")) {
            return text;
        }
        if (name.equals("INT_LIT") || name.equals("DECIMAL_LIT")) {
            return number;
        }
        return normal;
    }

    private boolean isWord(String literal) {
        char first = literal.charAt(1);
        return Character.isLetter(first) || first == '%' || first == '@';
    }

    private Style createStyle(String name, Color color, boolean bold) {
        Style style = editor.addStyle(name, null);
        StyleConstants.setFontFamily(style, "Monospaced");
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        return style;
    }
}
