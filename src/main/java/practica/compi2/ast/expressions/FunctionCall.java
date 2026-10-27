package practica.compi2.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.semantic.CallMatcher;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.types.Type;

@Getter
public class FunctionCall extends Expression {

    private String name;
    private List<Expression> arguments;
    private FunctionSymbol function;
    private MethodCall methodCall;

    public FunctionCall(Position position, String name, List<Expression> arguments) {
        super(position);
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public Type validate(SemanticContext sc) {
        if (sc.getCurrentClass() != null && !sc.getCurrentClass().getMethodsNamed(name).isEmpty()) {
            methodCall = new MethodCall(position, new ThisAccess(position), name, arguments);
            type = methodCall.validate(sc);
            return type;
        }
        List<Type> argumentTypes = CallMatcher.validateArguments(arguments, sc);
        type = Type.ERROR;
        function = sc.getGlobals().getFunction(name);
        if (function == null) {
            sc.error(position, name, "La función '" + name + "' no existe o no fue importada");
            return type;
        }
        if (!CallMatcher.matches(function, argumentTypes, sc.getRules())) {
            sc.error(position, name, "Los argumentos no coinciden con la función " + function.getSignature());
            return type;
        }
        CallMatcher.checkReferences(function, arguments, sc);
        type = function.getReturnType();
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        if (methodCall != null) {
            return methodCall.toIntermediateCode(u);
        }
        List<MemoryAccess> values = new ArrayList<>();
        for (Expression argument : arguments) {
            values.add(argument.toIntermediateCode(u));
        }
        return CallHelper.call(u, function.getCName(), null, values);
    }

    @Override
    public boolean canBeStatement() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("funcion", name);
        printer.children("argumentos", arguments);
    }
}
