package practica.compi2.errors;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class SyntaxErrorListener extends BaseErrorListener {

    private final ErrorManager em;
    private final String fileName;
    private boolean foundErrors;

    public SyntaxErrorListener(ErrorManager em, String fileName) {
        this.em = em;
        this.fileName = fileName;
        this.foundErrors = false;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
            int charPositionInLine, String msg, RecognitionException e) {
        ErrorType type = ErrorType.SINTACTICO;
        String lexeme = "";
        if (recognizer instanceof Lexer) {
            type = ErrorType.LEXICO;
            msg = msg.replaceAll("(\\s|\\\\[nrt])+'$", "'");
            lexeme = AntlrMessageTranslator.lexemeOf(msg);
        } else if (offendingSymbol instanceof Token) {
            lexeme = AntlrMessageTranslator.translate(((Token) offendingSymbol).getText());
        }
        String description = AntlrMessageTranslator.translate(msg, lexeme);
        int errorLine = line;
        int errorColumn = charPositionInLine + 1;
        if (recognizer instanceof Parser && msg.startsWith("missing")) {
            Token previous = ((Parser) recognizer).getTokenStream().LT(-1);
            if (previous != null && previous.getType() != Token.EOF) {
                errorLine = previous.getLine();
                errorColumn = previous.getCharPositionInLine() + previous.getText().length() + 1;
            }
        }
        em.addError(new CompilationError(type, lexeme, fileName, errorLine, errorColumn, description));
        foundErrors = true;
    }

    public boolean hasErrors() {
        return foundErrors;
    }
}
