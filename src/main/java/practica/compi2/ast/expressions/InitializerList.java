package practica.compi2.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.InitializerHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
public class InitializerList extends Expression {

    private List<Expression> elements;

    public InitializerList(Position position, List<Expression> elements) {
        super(position);
        this.elements = elements;
    }

    @Override
    public Type validate(SemanticContext context) {
        context.error(position, "{", "Una lista {...} solo puede usarse para dar valor a un arreglo o estructura");
        type = Type.ERROR;
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        return InitializerHelper.build(utils, this, type, new ArrayList<>());
    }

    @Override
    public void print(AstPrinter printer) {
        printer.children("elementos", elements);
    }
}
