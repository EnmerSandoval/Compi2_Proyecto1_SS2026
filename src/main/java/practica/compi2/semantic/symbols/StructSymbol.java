package practica.compi2.semantic.symbols;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.definitions.StructDefinition;

@Getter
public class StructSymbol {

    private String name;
    private List<Symbol> fields;
    private StructDefinition definition;

    public StructSymbol(String name, StructDefinition definition) {
        this.name = name;
        this.fields = new ArrayList<>();
        this.definition = definition;
    }

    public boolean addField(Symbol field) {
        if (getField(field.getName()) != null) {
            return false;
        }
        fields.add(field);
        return true;
    }

    public Symbol getField(String fieldName) {
        for (Symbol field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
