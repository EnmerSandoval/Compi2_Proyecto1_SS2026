package practica.compi2.ast.statements;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.CanAddStmt;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.semantic.SemanticContext;

@Getter
public class Block extends Statement implements CanAddStmt {

    private List<Statement> statements;

    public Block(Position position) {
        super(position);
        this.statements = new ArrayList<>();
    }

    @Override
    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    @Override
    public void validate(SemanticContext sc) {
        sc.enterScope();
        validateStatements(sc);
        sc.exitScope();
    }

    public void validateStatements(SemanticContext sc) {
        for (Statement statement : statements) {
            statement.validate(sc);
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        for (Statement statement : statements) {
            statement.toIntermediateCode(u);
        }
    }

    @Override
    public void print(AstPrinter printer) {
        printer.children("sentencias", statements);
    }
}
