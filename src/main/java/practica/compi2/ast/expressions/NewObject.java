package practica.compi2.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.CallMatcher;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

@Getter
public class NewObject extends Expression {

    private String className;
    private List<Expression> arguments;
    private ClassSymbol classSymbol;
    private FunctionSymbol constructor;

    public NewObject(Position position, String className, List<Expression> arguments) {
        super(position);
        this.className = className;
        this.arguments = arguments;
    }

    @Override
    public Type validate(SemanticContext sc) {
        List<Type> argumentTypes = CallMatcher.validateArguments(arguments, sc);
        type = Type.ERROR;
        classSymbol = sc.getGlobals().getClass(className);
        if (classSymbol == null) {
            sc.error(position, className, "La clase '" + className + "' no existe o no fue importada");
            return type;
        }
        constructor = CallMatcher.findOverload(classSymbol.getConstructors(), argumentTypes, sc.getRules());
        boolean usesDefaultConstructor = classSymbol.getConstructors().isEmpty() && arguments.isEmpty();
        if (constructor == null && !usesDefaultConstructor) {
            sc.error(position, className, "La clase " + className + " no tiene un constructor que reciba esos argumentos");
            return type;
        }
        if (constructor != null) {
            sc.checkAccess(position, className, constructor.getAccess(), classSymbol,
                    "El constructor " + constructor.getSignature());
        }
        type = new Type(TypeKind.CLASS, className, 0);
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        List<MemoryAccess> values = new ArrayList<>();
        for (Expression argument : arguments) {
            values.add(argument.toIntermediateCode(u));
        }
        TemporalAccess object = MemoryHelper.allocate(u, new Literal3D(classSymbol.getSize()));
        u.getCuartetas().add(new Assignation3D(new HeapAccess(object), new Literal3D(classSymbol.getId())));
        CallHelper.call(u, classSymbol.getInitName(), object, new ArrayList<>());
        FunctionSymbol called = constructor;
        if (called == null) {
            called = classSymbol.getImplicitConstructor();
        }
        if (called != null) {
            CallHelper.call(u, called.getCName(), object, values);
        }
        return object;
    }

    @Override
    public boolean canBeStatement() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("clase", className);
        printer.children("argumentos", arguments);
    }
}
