package practica.compi2.ast.statements;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.semantic.SemanticContext;

@Getter
public class ExpressionStmt extends Statement {

    private Expression expression;

    public ExpressionStmt(Position position, Expression expression) {
        super(position);
        this.expression = expression;
    }

    @Override
    public void validate(SemanticContext sc) {
        expression.validate(sc);
        if (!expression.canBeStatement()) {
            sc.error(position, "", "Esta expresión no hace nada por sí sola, no puede usarse como instrucción");
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        expression.toIntermediateCode(u);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("expresion", expression);
    }
}
