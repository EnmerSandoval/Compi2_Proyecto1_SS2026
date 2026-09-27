package practica.compi2.semantic;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;

public class CallMatcher {

    public static List<Type> validateArguments(List<Expression> arguments, SemanticContext sc) {
        List<Type> types = new ArrayList<>();
        for (Expression argument : arguments) {
            types.add(argument.validate(sc));
        }
        return types;
    }

    public static boolean matches(FunctionSymbol function, List<Type> argumentTypes, TypeRules rules) {
        List<Symbol> parameters = function.getParameters();
        if (parameters.size() != argumentTypes.size()) {
            return false;
        }
        for (int i = 0; i < parameters.size(); i++) {
            if (!rules.canAssign(parameters.get(i).getType(), argumentTypes.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static FunctionSymbol findOverload(List<FunctionSymbol> candidates, List<Type> argumentTypes,
            TypeRules rules) {
        for (FunctionSymbol candidate : candidates) {
            if (hasExactTypes(candidate, argumentTypes)) {
                return candidate;
            }
        }
        for (FunctionSymbol candidate : candidates) {
            if (matches(candidate, argumentTypes, rules)) {
                return candidate;
            }
        }
        return null;
    }

    public static void checkReferences(FunctionSymbol function, List<Expression> arguments,
            SemanticContext sc) {
        List<Symbol> parameters = function.getParameters();
        for (int i = 0; i < parameters.size() && i < arguments.size(); i++) {
            Type parameterType = parameters.get(i).getType();
            boolean byReference = parameterType.isArray() || parameterType.isStruct();
            if (byReference && !arguments.get(i).isAssignable()) {
                sc.error(arguments.get(i).getPosition(), parameters.get(i).getName(),
                        "El parámetro '" + parameters.get(i).getName() + "' se pasa por referencia, se debe enviar una variable");
            }
        }
    }

    private static boolean hasExactTypes(FunctionSymbol function, List<Type> argumentTypes) {
        List<Symbol> parameters = function.getParameters();
        if (parameters.size() != argumentTypes.size()) {
            return false;
        }
        for (int i = 0; i < parameters.size(); i++) {
            if (!parameters.get(i).getType().sameAs(argumentTypes.get(i))) {
                return false;
            }
        }
        return true;
    }
}
