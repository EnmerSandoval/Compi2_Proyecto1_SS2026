package practica.compi2.visitors;

import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Literal;
import practica.compi2.ast.types.PrimitiveType;

public class LiteralFactory {

    public static Literal integer(Position position, String text) {
        try {
            return new Literal(position, PrimitiveType.INTEGER, Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return new Literal(position, PrimitiveType.INTEGER, text);
        }
    }

    public static Literal decimal(Position position, String text) {
        return new Literal(position, PrimitiveType.FLOAT, Double.parseDouble(text));
    }

    public static Literal string(Position position, String text) {
        String content = text.substring(1, text.length() - 1);
        return new Literal(position, PrimitiveType.STRING, unescape(content));
    }

    public static Literal character(Position position, String text) {
        String content = text.substring(1, text.length() - 1);
        return new Literal(position, PrimitiveType.CHAR, unescape(content).charAt(0));
    }

    public static Literal bool(Position position, boolean value) {
        return new Literal(position, PrimitiveType.BOOLEAN, value);
    }

    private static String unescape(String text) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                i++;
                sb.append(escapedChar(text.charAt(i)));
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    private static char escapedChar(char c) {
        switch (c) {
            case 'n' -> {
                return '\n';
            }
            case 't' -> {
                return '\t';
            }
            case 'r' -> {
                return '\r';
            }
            default -> {
                return c;
            }
        }
    }
}
