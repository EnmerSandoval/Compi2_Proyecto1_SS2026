package practica.compi2.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

@Getter
@AllArgsConstructor
public class Position {

    private int line;
    private int column;
    private String lexeme;

    public static Position of(ParserRuleContext ctx) {
        Token start = ctx.getStart();
        return new Position(start.getLine(), start.getCharPositionInLine() + 1, lexemeOf(ctx));
    }

    private static String lexemeOf(ParserRuleContext ctx) {
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (!hasSourceText(start, stop)) {
            return start.getText();
        }
        int end = Math.min(stop.getStopIndex(), start.getStartIndex() + 29);
        String text = cleanText(start.getInputStream().getText(Interval.of(start.getStartIndex(), end)));
        if (end < stop.getStopIndex()) {
            text = text.substring(0, Math.min(27, text.length())) + "...";
        }
        return text;
    }

    private static boolean hasSourceText(Token start, Token stop) {
        if (stop == null || start.getInputStream() == null) {
            return false;
        }
        return start.getStartIndex() >= 0 && stop.getStopIndex() >= start.getStartIndex();
    }

    private static String cleanText(String text) {
        String clean = text.replace("\r", "").replace("\n", " ").replace("\t", " ").trim();
        if (clean.endsWith(";")) {
            clean = clean.substring(0, clean.length() - 1).trim();
        }
        return clean;
    }

    @Override
    public String toString() {
        return "(" + line + ":" + column + ")";
    }
}
