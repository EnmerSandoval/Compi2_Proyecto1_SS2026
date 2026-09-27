package practica.compi2.semantic.symbols;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ClassSymbol {

    private String name;
    private List<Symbol> attributes;
    private List<FunctionSymbol> constructors;
    private List<FunctionSymbol> methods;

    public ClassSymbol(String name) {
        this.name = name;
        this.attributes = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public String getInitName() {
        return name + "_atributos";
    }

    public boolean addAttribute(Symbol attribute) {
        if (getAttribute(attribute.getName()) != null) {
            return false;
        }
        attributes.add(attribute);
        return true;
    }

    public Symbol getAttribute(String attributeName) {
        for (Symbol attribute : attributes) {
            if (attribute.getName().equals(attributeName)) {
                return attribute;
            }
        }
        return null;
    }

    public boolean addConstructor(FunctionSymbol constructor) {
        for (FunctionSymbol existing : constructors) {
            if (existing.hasSameParameters(constructor)) {
                return false;
            }
        }
        constructors.add(constructor);
        return true;
    }

    public boolean addMethod(FunctionSymbol method) {
        for (FunctionSymbol existing : getMethodsNamed(method.getName())) {
            if (existing.hasSameParameters(method)) {
                return false;
            }
        }
        methods.add(method);
        return true;
    }

    public List<FunctionSymbol> getMethodsNamed(String methodName) {
        List<FunctionSymbol> found = new ArrayList<>();
        for (FunctionSymbol method : methods) {
            if (method.getName().equals(methodName)) {
                found.add(method);
            }
        }
        return found;
    }
}
