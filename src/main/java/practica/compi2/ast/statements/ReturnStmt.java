package practica.compi2.ast.statements;

import java.util.ArrayList;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Return3D;
import practica.compi2.semantic.AssignmentChecker;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.types.Type;

@Getter
public class ReturnStmt extends Statement {

    private Expression value;

    public ReturnStmt(Position position, Expression value) {
        super(position);
        this.value = value;
    }

    @Override
    public void validate(SemanticContext sc) {
        FunctionSymbol function = sc.getCurrentFunction();
        if (function == null) {
            sc.error(position, "retornar", "Solo se puede retornar dentro de una función o método");
            return;
        }
        sc.setReturnFound(true);
        Type expected = function.getReturnType();
        if (value == null) {
            if (!expected.isVoid()) {
                sc.error(position, function.getName(), "Se debe retornar un valor de tipo " + expected);
            }
            return;
        }
        if (expected.isVoid()) {
            value.validate(sc);
            sc.error(position, function.getName(), "'" + function.getName() + "' no retorna ningún valor");
            return;
        }
        new AssignmentChecker(sc).check(value, expected, new ArrayList<>());
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        if (value != null) {
            MemoryAccess access = value.toIntermediateCode(u);
            u.getCuartetas().add(new Assignation3D(new StackAccess(PointerAccess.STACK), access));
        }
        u.getCuartetas().add(new Return3D());
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("valor", value);
    }
}
