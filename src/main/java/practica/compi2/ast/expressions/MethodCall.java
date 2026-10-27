package practica.compi2.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.helpers.DispatchHelper;
import practica.compi2.c3d.helpers.NullCheckHelper;
import practica.compi2.semantic.CallMatcher;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.types.Type;

@Getter
public class MethodCall extends Expression {

    private Expression target;
    private String name;
    private List<Expression> arguments;
    private FunctionSymbol method;
    private ClassSymbol ownerClass;

    public MethodCall(Position position, Expression target, String name, List<Expression> arguments) {
        super(position);
        this.target = target;
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public Type validate(SemanticContext sc) {
        Type targetType = target.validate(sc);
        List<Type> argumentTypes = CallMatcher.validateArguments(arguments, sc);
        type = Type.ERROR;
        if (targetType.isError()) {
            return type;
        }
        if (!targetType.isClass()) {
            sc.error(position, name, "Solo se pueden llamar métodos sobre objetos, no sobre " + targetType);
            return type;
        }
        ownerClass = sc.getGlobals().getClass(targetType.getName());
        method = CallMatcher.findOverload(ownerClass.getMethodsNamed(name), argumentTypes, sc.getRules());
        if (method == null) {
            sc.error(position, name, "La clase " + ownerClass.getName() + " no tiene un método '" + name
                    + "' que reciba esos argumentos");
            return type;
        }
        sc.checkAccess(position, name, method.getAccess(), method.getOwnerClass(),
                "El método " + method.getSignature());
        type = method.getReturnType();
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        MemoryAccess object = target.toIntermediateCode(u);
        NullCheckHelper.check(u, target, object, position);
        List<MemoryAccess> values = new ArrayList<>();
        for (Expression argument : arguments) {
            values.add(argument.toIntermediateCode(u));
        }
        String callee = target instanceof SuperAccess ? method.getCName() : DispatchHelper.target(u, ownerClass, method);
        return CallHelper.call(u, callee, object, values);
    }

    @Override
    public boolean canBeStatement() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("objeto", target);
        printer.value("metodo", name);
        printer.children("argumentos", arguments);
    }
}
