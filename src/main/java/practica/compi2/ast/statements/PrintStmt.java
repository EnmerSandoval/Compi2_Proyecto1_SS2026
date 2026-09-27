package practica.compi2.ast.statements;

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
    public void validate(SemanticContext sc) {
        for (Expression value : values) {
            Type valueType = value.validate(sc);
            if (!valueType.isError() && !valueType.isPrimitive()) {
                sc.error(value.getPosition(), "", "No se puede imprimir un valor de tipo " + valueType);
            }
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        for (Expression value : values) {
            MemoryAccess access = value.toIntermediateCode(u);
            PrintHelper.print(u, access, value.getType());
        }
        if (newLine) {
            PrintHelper.printNewLine(u);
        }
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("saltoDeLinea", newLine);
        printer.children("valores", values);
    }
}
