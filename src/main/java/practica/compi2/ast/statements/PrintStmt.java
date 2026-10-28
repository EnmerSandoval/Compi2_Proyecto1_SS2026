package practica.compi2.ast.statements;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.PrintHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
public class PrintStmt extends Statement {

    private List<Expression> values;
    private boolean newLine;

    public PrintStmt(Position position, List<Expression> values, boolean newLine) {
        super(position);
        this.values = values;
        this.newLine = newLine;
    }

    @Override
    public void validate(SemanticContext context) {
        for (Expression value : values) {
            Type valueType = value.validate(context);
            if (!valueType.isError() && !valueType.isPrimitive()) {
                context.error(value.getPosition(), "", "No se puede imprimir un valor de tipo " + valueType);
            }
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        List<MemoryAccess> accesses = new ArrayList<>();
        for (Expression value : values) {
            accesses.add(value.toIntermediateCode(utils));
        }
        for (int i = 0; i < values.size(); i++) {
            PrintHelper.print(utils, accesses.get(i), values.get(i).getType());
        }
        if (newLine) {
            PrintHelper.printNewLine(utils);
        }
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("saltoDeLinea", newLine);
        printer.children("valores", values);
    }
}
