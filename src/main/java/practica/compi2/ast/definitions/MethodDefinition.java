package practica.compi2.ast.definitions;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.Frame;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.symbols.SymbolKind;
import practica.compi2.semantic.types.Type;

@Getter
public class MethodDefinition extends FunctionDefinition {

    private AccessModifier access;
    @Setter
    private boolean override;

    public MethodDefinition(Position position, AccessModifier access, String name, List<Parameter> parameters,
            TypeReference returnType, Block body) {
        super(position, name, parameters, returnType, body);
        this.access = access;
    }

    public void registerIn(ClassSymbol owner, SemanticContext context) {
        symbol = createSymbol(context, owner.getName(), SymbolKind.METHOD);
        symbol.setAccess(access);
        symbol.setOwnerClass(owner);
        if (!owner.addMethod(symbol)) {
            context.error(position, name, "El método " + symbol.getSignature()
                    + " ya existe en la clase " + owner.getName());
        }
        checkOverride(owner, context);
    }

    private void checkOverride(ClassSymbol owner, SemanticContext context) {
        String method = "El método " + symbol.getSignature();
        FunctionSymbol inherited = findInherited(owner, context);
        if (inherited == null) {
            if (override) {
                context.error(position, name, method + " tiene @Override pero no sobrescribe ningún método de una clase padre");
            }
            return;
        }
        String parent = inherited.getOwner();
        if (!override) {
            context.error(position, name, method + " sobrescribe al de la clase " + parent + " y debe llevar la anotación @Override");
        }
        if (!isCovariant(inherited.getReturnType(), symbol.getReturnType(), context)) {
            context.error(position, name, method + " debe retornar " + inherited.getReturnType()
                    + " (o una subclase) como en la clase " + parent);
        }
        if (visibility(access) < visibility(inherited.getAccess())) {
            context.error(position, name, method + " no puede ser menos visible que en la clase " + parent);
        }
    }

    private FunctionSymbol findInherited(ClassSymbol owner, SemanticContext context) {
        if (owner.getParent() == null) {
            return null;
        }
        FunctionSymbol inherited = owner.getParent().findImplementation(symbol);
        if (inherited == symbol || !context.canAccess(inherited.getAccess(), inherited.getOwnerClass())) {
            return null;
        }
        return inherited;
    }

    private boolean isCovariant(Type parentReturn, Type childReturn, SemanticContext context) {
        if (parentReturn.sameAs(childReturn)) {
            return true;
        }
        return parentReturn.isClass() && childReturn.isClass()
                && parentReturn.getDimensions() == 0 && childReturn.getDimensions() == 0
                && context.getGlobals().isSubclass(childReturn.getName(), parentReturn.getName());
    }

    private int visibility(AccessModifier modifier) {
        return switch (modifier) {
            case PRIVATE, DEFAULT -> 0;
            case PROTECTED -> 1;
            case PUBLIC -> 2;
        };
    }

    @Override
    public void validate(SemanticContext context) {
        validateBody(context, new Frame(symbol.getOwner() + "." + name, 2));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("acceso", access);
        if (override) {
            printer.value("anotacion", "@Override");
        }
        super.print(printer);
    }
}
