package practica.compi2.ast.expressions;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.symbols.SymbolKind;
import practica.compi2.semantic.types.Type;

@Getter
public class VariableAccess extends Expression {

    private String id;
    private Symbol symbol;

    public VariableAccess(Position position, String id) {
        super(position);
        this.id = id;
    }

    @Override
    public Type validate(SemanticContext sc) {
        symbol = sc.lookupVariable(id);
        if (symbol == null) {
            sc.error(position, id, "La variable '" + id + "' no ha sido declarada");
            type = Type.ERROR;
            return type;
        }
        type = symbol.getType();
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        return MemoryHelper.load(u, toAddress(u));
    }

    @Override
    public MemoryAccess toAddress(IntermediateCodeUtils u) {
        if (symbol.getKind() == SymbolKind.ATTRIBUTE) {
            MemoryAccess thisPointer = MemoryHelper.thisPointer(u);
            return MemoryHelper.heapSlot(u, thisPointer, new Literal3D(symbol.getOffset()));
        }
        return MemoryHelper.stackSlot(u, symbol.getOffset());
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("id", id);
    }
}
