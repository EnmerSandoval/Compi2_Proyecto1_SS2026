package practica.compi2.antlr4.y;

import java.util.LinkedList;
import java.util.Stack;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

public abstract class YLexerBase extends Lexer {

    private static final int TAB_SIZE = 4;

    private final LinkedList<Token> pendingTokens = new LinkedList<>();
    private final Stack<Integer> indents = new Stack<>();
    private int openBrackets = 0;
    private int lastTokenType = Token.INVALID_TYPE;

    protected YLexerBase(CharStream input) {
        super(input);
    }

    @Override
    public Token nextToken() {
        if (pendingTokens.isEmpty()) {
            Token token = super.nextToken();
            if (token.getType() == Token.EOF) {
                closeIndentation(token);
            }
            addToken(token);
        }
        return pendingTokens.removeFirst();
    }

    protected void openBracket() {
        openBrackets++;
    }

    protected void closeBracket() {
        if (openBrackets > 0) {
            openBrackets--;
        }
    }

    protected void onNewLine() {
        skip();
        if (openBrackets > 0 || isBlankLine() || !lineHasCode()) {
            return;
        }
        addToken(createToken(YLexer.NEWLINE, "<salto de línea>", _tokenStartLine, _tokenStartCharPositionInLine));
        checkIndentation(measureIndent(getText()));
    }

    private void checkIndentation(int indent) {
        int line = getLine();
        int column = getCharPositionInLine();
        if (indent > currentIndent()) {
            indents.push(indent);
            addToken(createToken(YLexer.INDENT, "<indentación>", line, column));
            return;
        }
        while (indent < currentIndent()) {
            indents.pop();
            addToken(createToken(YLexer.DEDENT, "<fin de bloque>", line, column));
        }
        if (indent != currentIndent()) {
            getErrorListenerDispatch().syntaxError(this, null, line, column, "Indentación inconsistente", null);
        }
    }

    private void closeIndentation(Token eof) {
        if (lineHasCode()) {
            addToken(createToken(YLexer.NEWLINE, "<salto de línea>", eof.getLine(), eof.getCharPositionInLine()));
        }
        while (!indents.isEmpty()) {
            indents.pop();
            addToken(createToken(YLexer.DEDENT, "<fin de bloque>", eof.getLine(), eof.getCharPositionInLine()));
        }
    }

    private boolean isBlankLine() {
        int next = _input.LA(1);
        int afterNext = _input.LA(2);
        boolean startsComment = next == '/' && (afterNext == '/' || afterNext == '*');
        return next == '\n' || next == '\r' || next == EOF || startsComment;
    }

    private boolean lineHasCode() {
        return lastTokenType != Token.INVALID_TYPE
                && lastTokenType != Token.EOF
                && lastTokenType != YLexer.NEWLINE
                && lastTokenType != YLexer.INDENT
                && lastTokenType != YLexer.DEDENT;
    }

    private int currentIndent() {
        if (indents.isEmpty()) {
            return 0;
        }
        return indents.peek();
    }

    private int measureIndent(String text) {
        int indent = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                indent++;
            } else if (c == '\t') {
                indent += TAB_SIZE - indent % TAB_SIZE;
            }
        }
        return indent;
    }

    private void addToken(Token token) {
        pendingTokens.addLast(token);
        if (token.getChannel() == Token.DEFAULT_CHANNEL) {
            lastTokenType = token.getType();
        }
    }

    private Token createToken(int type, String text, int line, int column) {
        CommonToken token = new CommonToken(_tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL,
                _tokenStartCharIndex, _tokenStartCharIndex - 1);
        token.setText(text);
        token.setLine(line);
        token.setCharPositionInLine(column);
        return token;
    }
}
