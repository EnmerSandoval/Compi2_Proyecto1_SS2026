package practica.compi2.semantic.symbols;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.semantic.types.Type;

@Getter
public class FunctionSymbol {

    private String name;
    private Type returnType;
    private String owner;
    private List<Symbol> parameters;
    @Setter
    private int frameSize;

    public FunctionSymbol(String name, Type returnType, String owner) {
        this.name = name;
        this.returnType = returnType;
        this.owner = owner;
        this.parameters = new ArrayList<>();
    }

    public void addParameter(Symbol parameter) {
        parameters.add(parameter);
    }

    public boolean hasSameParameters(FunctionSymbol other) {
        if (parameters.size() != other.parameters.size()) {
            return false;
        }
        for (int i = 0; i < parameters.size(); i++) {
            if (!parameters.get(i).getType().sameAs(other.parameters.get(i).getType())) {
                return false;
            }
        }
        return true;
    }

    public String getCName() {
        if (owner == null) {
            return "fn_" + name;
        }
        StringBuilder sb = new StringBuilder(owner).append("_").append(name);
        for (Symbol parameter : parameters) {
            sb.append("_").append(parameter.getType().toString().replace("[]", "Arr"));
        }
        return sb.toString();
    }

    public String getSignature() {
        StringBuilder sb = new StringBuilder(name).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameters.get(i).getType());
        }
        return sb.append(")").toString();
    }
}
