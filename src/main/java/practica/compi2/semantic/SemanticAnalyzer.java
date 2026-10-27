package practica.compi2.semantic;

import java.util.Comparator;
import java.util.List;
import practica.compi2.ast.Ast;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.ast.programs.ZetarianoProgram;
import practica.compi2.errors.ErrorManager;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.TypeTable;

public class SemanticAnalyzer {

    private final SemanticContext sc;

    public SemanticAnalyzer(ErrorManager em) {
        this.sc = new SemanticContext(em);
    }

    public void analyze(Ast ast) {
        registerTypes(ast);
        linkClasses(ast);
        registerMembers(ast);
        validatePrograms(ast);
    }

    private void registerTypes(Ast ast) {
        for (YProgram program : ast.getYPrograms()) {
            program.registerTypes(sc);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            program.registerTypes(sc);
        }
    }

    private void linkClasses(Ast ast) {
        for (ZetarianoProgram program : ast.getClasses()) {
            program.linkParent(sc);
        }
        ast.getClasses().sort(Comparator.comparingInt(program -> program.getClassDefinition().depth()));
    }

    private void registerMembers(Ast ast) {
        for (YProgram program : ast.getYPrograms()) {
            program.registerMembers(sc);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            program.registerMembers(sc);
        }
    }

    private void validatePrograms(Ast ast) {
        for (YProgram program : ast.getYPrograms()) {
            program.validate(sc);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            program.validate(sc);
        }
        if (ast.getMain() != null) {
            ast.getMain().validate(sc);
        }
    }

    public TypeTable getTypeTable() {
        return sc.getTypeTable();
    }

    public List<Symbol> getSymbols() {
        return sc.getSymbols();
    }
}
