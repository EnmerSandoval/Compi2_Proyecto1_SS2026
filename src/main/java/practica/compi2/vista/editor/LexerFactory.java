package practica.compi2.vista.editor;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import practica.compi2.antlr4.piglatin.PigLatinLexer;
import practica.compi2.antlr4.y.YLexer;
import practica.compi2.antlr4.zetariano.ZetarianoLexer;

public class LexerFactory {

    public static Lexer create(String extension, String text) {
        CharStream input = CharStreams.fromString(text);
        Lexer lexer;
        switch (extension) {
            case "pig" -> lexer = new PigLatinLexer(input);
            case "y" -> lexer = new YLexer(input);
            case "z" -> lexer = new ZetarianoLexer(input);
            default -> {
                return null;
            }
        }
        lexer.removeErrorListeners();
        return lexer;
    }
}
