package practica.compi2.parsers;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import practica.compi2.antlr4.y.YLexer;
import practica.compi2.antlr4.y.YParser;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.errors.ErrorManager;
import practica.compi2.errors.SyntaxErrorListener;
import practica.compi2.visitors.y.YProgramVisitor;

public class YFileParser {

    private final ErrorManager em;

    public YFileParser(ErrorManager em) {
        this.em = em;
    }

    public YProgram parse(String fileName, String code) {
        SyntaxErrorListener errorListener = new SyntaxErrorListener(em, fileName);

        YLexer lexer = new YLexer(CharStreams.fromString(code));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        YParser parser = new YParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        ParseTree tree = parser.program();
        if (errorListener.hasErrors()) {
            return null;
        }

        YProgramVisitor visitor = new YProgramVisitor(fileName);
        return (YProgram) visitor.visit(tree);
    }
}
