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
    public Type validate(SemanticContext context) {
        Type targetType = target.validate(context);
        List<Type> argumentTypes = CallMatcher.validateArguments(arguments, context);
        type = Type.ERROR;
        if (targetType.isError()) {
            return type;
        }
        if (!targetType.isClass()) {
            context.error(position, name, "Solo se pueden llamar métodos sobre objetos, no sobre " + targetType);
            return type;
        }
        ownerClass = context.getGlobals().getClass(targetType.getName());
        method = CallMatcher.findOverload(ownerClass.getMethodsNamed(name), argumentTypes, context.getRules());
        if (method == null) {
            context.error(position, name, "La clase " + ownerClass.getName() + " no tiene un método '" + name
                    + "' que reciba esos argumentos");
            return type;
        }
        context.checkAccess(position, name, method.getAccess(), method.getOwnerClass(),
                "El método " + method.getSignature());
        type = method.getReturnType();
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        MemoryAccess object = target.toIntermediateCode(utils);
        NullCheckHelper.check(utils, target, object, position);
        List<MemoryAccess> values = new ArrayList<>();
        for (Expression argument : arguments) {
            values.add(argument.toIntermediateCode(utils));
        }
        String callee = target instanceof SuperAccess ? method.getCName() : DispatchHelper.target(utils, ownerClass, method);
        return CallHelper.call(utils, callee, object, values);
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
