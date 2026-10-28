package practica.compi2.semantic;

import practica.compi2.ast.types.TypeReference;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.StructSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

public class TypeResolver {

    private final SemanticContext context;

    public TypeResolver(SemanticContext context) {
        this.context = context;
    }

    public Type resolve(TypeReference reference) {
        int dimensions = reference.getDimensions();
        if (reference.isPrimitive()) {
            return Type.fromPrimitive(reference.getPrimitive()).withDimensions(dimensions);
        }
        String name = reference.getName();
        if (context.lookupStruct(name) != null) {
            return new Type(TypeKind.STRUCT, name, dimensions);
        }
        if (context.getGlobals().getClass(name) != null) {
            return new Type(TypeKind.CLASS, name, dimensions);
        }
        context.error(reference.getPosition(), name, "El tipo '" + name + "' no existe o no fue importado");
        return Type.ERROR;
    }

    public Symbol findMember(Type owner, String member) {
        if (owner.isStruct()) {
            StructSymbol struct = context.lookupStruct(owner.getName());
            if (struct != null) {
                return struct.getField(member);
            }
        }
        if (owner.isClass()) {
            ClassSymbol classSymbol = context.getGlobals().getClass(owner.getName());
            if (classSymbol != null) {
                return classSymbol.getAttribute(member);
            }
        }
        return null;
    }
}
