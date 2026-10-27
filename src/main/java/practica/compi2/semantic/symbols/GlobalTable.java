package practica.compi2.semantic.symbols;

import java.util.HashMap;
import java.util.Map;

public class GlobalTable {

    private final Map<String, StructSymbol> structs;
    private final Map<String, FunctionSymbol> functions;
    private final Map<String, ClassSymbol> classes;

    public GlobalTable() {
        this.structs = new HashMap<>();
        this.functions = new HashMap<>();
        this.classes = new HashMap<>();
    }

    public boolean addStruct(StructSymbol struct) {
        if (existsType(struct.getName())) {
            return false;
        }
        structs.put(struct.getName(), struct);
        return true;
    }

    public boolean addClass(ClassSymbol classSymbol) {
        if (existsType(classSymbol.getName())) {
            return false;
        }
        classSymbol.setId(classes.size() + 1);
        classes.put(classSymbol.getName(), classSymbol);
        return true;
    }

    public boolean addFunction(FunctionSymbol function) {
        if (functions.containsKey(function.getName())) {
            return false;
        }
        functions.put(function.getName(), function);
        return true;
    }

    public StructSymbol getStruct(String name) {
        return structs.get(name);
    }

    public ClassSymbol getClass(String name) {
        return classes.get(name);
    }

    public FunctionSymbol getFunction(String name) {
        return functions.get(name);
    }

    public boolean isSubclass(String child, String parent) {
        ClassSymbol childClass = classes.get(child);
        ClassSymbol parentClass = classes.get(parent);
        return childClass != null && parentClass != null && childClass.isSubclassOf(parentClass);
    }

    private boolean existsType(String name) {
        return structs.containsKey(name) || classes.containsKey(name);
    }
}
