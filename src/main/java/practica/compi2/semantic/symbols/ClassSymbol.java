package practica.compi2.semantic.symbols;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.definitions.AccessModifier;

@Getter
public class ClassSymbol {

    public static final int HEADER_SIZE = 1;

    private String name;
    private AccessModifier access;
    @Setter
    private int id;
    @Setter
    private ClassSymbol parent;
    private List<Symbol> attributes;
    private List<FunctionSymbol> constructors;
    private List<FunctionSymbol> methods;

    public ClassSymbol(String name, AccessModifier access) {
        this.name = name;
        this.access = access;
        this.attributes = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public String getInitName() {
        return name + "_atributos";
    }

    public int getFirstAttributeOffset() {
        if (parent == null) {
            return HEADER_SIZE;
        }
        return parent.getSize();
    }

    public int getSize() {
        return getFirstAttributeOffset() + attributes.size();
    }

    public boolean isSubclassOf(ClassSymbol other) {
        for (ClassSymbol current = this; current != null; current = current.parent) {
            if (current == other) {
                return true;
            }
        }
        return false;
    }

    public boolean addAttribute(Symbol attribute) {
        for (Symbol existing : attributes) {
            if (existing.getName().equals(attribute.getName())) {
                return false;
            }
        }
        attributes.add(attribute);
        return true;
    }

    public Symbol getAttribute(String attributeName) {
        for (ClassSymbol current = this; current != null; current = current.parent) {
            for (Symbol attribute : current.attributes) {
                if (attribute.getName().equals(attributeName)) {
                    return attribute;
                }
            }
        }
        return null;
    }

    public List<Symbol> getAllAttributes() {
        List<Symbol> all = new ArrayList<>();
        if (parent != null) {
            all.addAll(parent.getAllAttributes());
        }
        all.addAll(attributes);
        return all;
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

    public boolean hasNoArgConstructor() {
        if (constructors.isEmpty()) {
            return true;
        }
        for (FunctionSymbol constructor : constructors) {
            if (constructor.getParameters().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public FunctionSymbol getImplicitConstructor() {
        if (constructors.isEmpty()) {
            return parent == null ? null : parent.getImplicitConstructor();
        }
        for (FunctionSymbol constructor : constructors) {
            if (constructor.getParameters().isEmpty()) {
                return constructor;
            }
        }
        return null;
    }

    public boolean addMethod(FunctionSymbol method) {
        for (FunctionSymbol existing : methods) {
            if (existing.getName().equals(method.getName()) && existing.hasSameParameters(method)) {
                return false;
            }
        }
        methods.add(method);
        return true;
    }

    public List<FunctionSymbol> getMethodsNamed(String methodName) {
        List<FunctionSymbol> found = new ArrayList<>();
        for (ClassSymbol current = this; current != null; current = current.parent) {
            for (FunctionSymbol method : current.methods) {
                if (method.getName().equals(methodName) && !hasSameSignature(found, method)) {
                    found.add(method);
                }
            }
        }
        return found;
    }

    public FunctionSymbol findImplementation(FunctionSymbol method) {
        FunctionSymbol own = findInheritableMethod(method);
        if (own != null) {
            return own;
        }
        if (this == method.getOwnerClass() || parent == null) {
            return method;
        }
        return parent.findImplementation(method);
    }

    private FunctionSymbol findInheritableMethod(FunctionSymbol method) {
        for (FunctionSymbol candidate : methods) {
            if (candidate.getName().equals(method.getName()) && candidate.hasSameParameters(method)
                    && candidate.getAccess().isInheritable()) {
                return candidate;
            }
        }
        return null;
    }

    private boolean hasSameSignature(List<FunctionSymbol> list, FunctionSymbol method) {
        for (FunctionSymbol other : list) {
            if (other.hasSameParameters(method)) {
                return true;
            }
        }
        return false;
    }
}
