package practica.compi2.ast.definitions;

import java.util.ArrayList;
import lombok.Getter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.helpers.InitializerHelper;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.AssignmentChecker;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.symbols.SymbolKind;
import practica.compi2.semantic.types.Type;

@Getter
public class AttributeDefinition extends AstNode {

    private AccessModifier access;
    private TypeReference type;
    private String name;
    private Expression value;
    private Symbol symbol;

    public AttributeDefinition(Position position, AccessModifier access, TypeReference type, String name,
            Expression value) {
        super(position);
        this.access = access;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public void registerIn(ClassSymbol owner, SemanticContext sc) {
        Type resolved = sc.resolveType(type);
        symbol = new Symbol(name, resolved, SymbolKind.ATTRIBUTE, owner.getAttributes().size());
        symbol.setScope(owner.getName());
        if (!owner.addAttribute(symbol)) {
            sc.error(position, name, "El atributo '" + name + "' ya fue declarado en la clase " + owner.getName());
        }
        sc.report(symbol);
    }

    public void validate(SemanticContext sc) {
        if (value != null) {
            new AssignmentChecker(sc).check(value, symbol.getType(), new ArrayList<>());
        }
    }

    public void toIntermediateCode(IntermediateCodeUtils u) {
        if (value == null) {
            return;
        }
        MemoryAccess access;
        if (value instanceof InitializerList) {
            access = InitializerHelper.build(u, (InitializerList) value, symbol.getType(), new ArrayList<>());
        } else {
            access = value.toIntermediateCode(u);
        }
        MemoryAccess thisPointer = MemoryHelper.thisPointer(u);
        u.getCuartetas().add(new Assignation3D(MemoryHelper.heapSlot(u, thisPointer, new Literal3D(symbol.getOffset())), access));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("acceso", access);
        printer.value("tipo", type);
        printer.value("nombre", name);
        printer.child("valor", value);
    }
}
