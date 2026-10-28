package practica.compi2.parsers;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import practica.compi2.antlr4.zetariano.ZetarianoLexer;
import practica.compi2.antlr4.zetariano.ZetarianoParser;
import practica.compi2.ast.programs.ZetarianoProgram;
import practica.compi2.errors.ErrorManager;
import practica.compi2.errors.SyntaxErrorListener;
import practica.compi2.visitors.zetariano.ZetarianoClassVisitor;

public class ZetarianoFileParser {

    private final ErrorManager errorManager;

    public ZetarianoFileParser(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    public ZetarianoProgram parse(String fileName, String code) {
        SyntaxErrorListener errorListener = new SyntaxErrorListener(errorManager, fileName);

        ZetarianoLexer lexer = new ZetarianoLexer(CharStreams.fromString(code));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ZetarianoParser parser = new ZetarianoParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        ParseTree tree = parser.compilationUnit();
        if (errorListener.hasErrors()) {
            return null;
        }

        ZetarianoClassVisitor visitor = new ZetarianoClassVisitor(fileName);
        return (ZetarianoProgram) visitor.visit(tree);
    }
}
