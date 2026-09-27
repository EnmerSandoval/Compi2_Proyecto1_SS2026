package practica.compi2.ast.statements;

import java.util.ArrayList;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.helpers.StringHelper;
import practica.compi2.c3d.natives.ReadLineNative;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
public class ReadStmt extends Statement {

    private Expression target;

    public ReadStmt(Position position, Expression target) {
        super(position);
        this.target = target;
    }

    @Override
    public void validate(SemanticContext sc) {
        if (target == null) {
            return;
        }
        Type targetType = target.validate(sc);
        if (!target.isAssignable()) {
            sc.error(position, "<<", "Lo que se lee solo puede guardarse en una variable");
        } else if (!targetType.isError() && !targetType.isPrimitive()) {
            sc.error(position, "<<", "No se puede leer un valor de tipo " + targetType);
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        MemoryAccess text = CallHelper.call(u, ReadLineNative.NAME, null, new ArrayList<>());
        if (target == null) {
            return;
        }
        MemoryAccess converted = StringHelper.fromText(u, text, target.getType());
        u.getCuartetas().add(new Assignation3D(target.toAddress(u), converted));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("destino", target);
    }
}
