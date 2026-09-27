package practica.compi2.errors;

public class AntlrMessageTranslator {

    public static String translate(String message, String lexeme) {
        if (message.startsWith("no viable alternative")) {
            return "Instrucción no válida cerca de '" + lexeme + "'";
        }
        if (message.startsWith("token recognition error") && isUnclosedText(lexeme)) {
            return "Cadena o carácter sin cerrar";
        }
        return translate(message);
    }

    private static boolean isUnclosedText(String lexeme) {
        return lexeme.startsWith("\"") || lexeme.startsWith("“") || lexeme.startsWith("'");
    }

    public static String translate(String message) {
        String text = message;
        if (text.startsWith("missing ")) {
            text = text.replaceFirst("missing ", "Falta ").replace(" at ", " antes de ");
        }
        text = text.replace("token recognition error at:", "Símbolo no reconocido:");
        text = text.replace("mismatched input", "Se encontró");
        text = text.replace("extraneous input", "Sobra");
        text = text.replace("expecting", "cuando se esperaba");
        text = text.replace("<EOF>", "fin de archivo");
        text = text.replaceAll("\\bNEWLINE\\b", "salto de línea");
        text = text.replaceAll("\\bINDENT\\b", "indentación");
        text = text.replaceAll("\\bDEDENT\\b", "fin de bloque");
        text = text.replaceAll("\\bID\\b", "identificador");
        text = text.replace("DECIMAL_LIT", "número decimal");
        text = text.replace("INT_LIT", "número entero");
        text = text.replace("CHAR_LIT", "carácter");
        text = text.replace("STRING_LIT", "cadena");
        return shortenExpected(text);
    }

    private static String shortenExpected(String text) {
        int open = text.indexOf("esperaba {");
        int close = text.lastIndexOf('}');
        if (open == -1 || close < open) {
            return text;
        }
        open += "esperaba ".length();
        String[] items = text.substring(open + 1, close).split(", ");
        if (items.length <= 6) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text.substring(0, open + 1));
        for (int i = 0; i < 5; i++) {
            sb.append(items[i]).append(", ");
        }
        sb.append("...").append(text.substring(close));
        return sb.toString();
    }

    public static String lexemeOf(String message) {
        int start = message.indexOf('\'');
        int end = message.lastIndexOf('\'');
        if (start >= 0 && end > start) {
            return message.substring(start + 1, end).replace("\\n", "").replace("\\r", "");
        }
        return "";
    }
}
