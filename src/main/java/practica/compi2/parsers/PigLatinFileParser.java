package practica.compi2.parsers;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import practica.compi2.antlr4.piglatin.PigLatinLexer;
import practica.compi2.antlr4.piglatin.PigLatinParser;
import practica.compi2.ast.programs.PigLatinProgram;
import practica.compi2.errors.ErrorManager;
import practica.compi2.errors.SyntaxErrorListener;
import practica.compi2.visitors.piglatin.PigLatinProgramVisitor;

public class PigLatinFileParser {

    private final ErrorManager em;

    public PigLatinFileParser(ErrorManager em) {
        this.em = em;
    }

    public PigLatinProgram parse(String fileName, String code) {
        SyntaxErrorListener errorListener = new SyntaxErrorListener(em, fileName);

        PigLatinLexer lexer = new PigLatinLexer(CharStreams.fromString(code));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PigLatinParser parser = new PigLatinParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        PigLatinParser.ProgramContext tree = parser.program();
        PigLatinProgramVisitor visitor = new PigLatinProgramVisitor(fileName);
        if (errorListener.hasErrors()) {
            return visitor.visitImports(tree);
        }
        return visitor.visitProgram(tree);
    }
}
