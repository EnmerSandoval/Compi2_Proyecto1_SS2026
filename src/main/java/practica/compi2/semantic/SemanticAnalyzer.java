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

    private final SemanticContext context;

    public SemanticAnalyzer(ErrorManager errorManager) {
        this.context = new SemanticContext(errorManager);
    }

    public void analyze(Ast ast) {
        registerTypes(ast);
        linkClasses(ast);
        registerMembers(ast);
        validatePrograms(ast);
    }

    private void registerTypes(Ast ast) {
        for (YProgram program : ast.getYPrograms()) {
            program.registerTypes(context);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            program.registerTypes(context);
        }
    }

    private void linkClasses(Ast ast) {
        for (ZetarianoProgram program : ast.getClasses()) {
            program.linkParent(context);
        }
        ast.getClasses().sort(Comparator.comparingInt(program -> program.getClassDefinition().depth()));
    }

    private void registerMembers(Ast ast) {
        for (YProgram program : ast.getYPrograms()) {
            program.registerMembers(context);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            program.registerMembers(context);
        }
    }

    private void validatePrograms(Ast ast) {
        for (YProgram program : ast.getYPrograms()) {
            program.validate(context);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            program.validate(context);
        }
        if (ast.getMain() != null) {
            ast.getMain().validate(context);
        }
    }

    public TypeTable getTypeTable() {
        return context.getTypeTable();
    }

    public List<Symbol> getSymbols() {
        return context.getSymbols();
    }
}
