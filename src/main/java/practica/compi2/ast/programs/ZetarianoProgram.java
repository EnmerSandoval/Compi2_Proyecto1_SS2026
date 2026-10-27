package practica.compi2.ast.programs;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.definitions.ClassDefinition;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.TypeRules;
import practica.compi2.semantic.types.ZetarianoCompatibilityTable;

@Getter
public class ZetarianoProgram extends Program {

    private ClassDefinition classDefinition;

    public ZetarianoProgram(Position position, String fileName, ClassDefinition classDefinition) {
        super(position, fileName);
        this.classDefinition = classDefinition;
    }

    public void registerTypes(SemanticContext sc) {
        sc.useFile(fileName, new TypeRules(new ZetarianoCompatibilityTable()));
        classDefinition.registerName(sc, nameWithoutExtension());
    }

    public void linkParent(SemanticContext sc) {
        sc.useFile(fileName, new TypeRules(new ZetarianoCompatibilityTable()));
        classDefinition.linkParent(sc);
    }

    public void registerMembers(SemanticContext sc) {
        sc.useFile(fileName, new TypeRules(new ZetarianoCompatibilityTable()));
        classDefinition.registerMembers(sc);
    }

    public void validate(SemanticContext sc) {
        sc.useFile(fileName, new TypeRules(new ZetarianoCompatibilityTable()));
        classDefinition.validate(sc);
    }

    public void toIntermediateCode(IntermediateCodeUtils u) {
        u.useBooleanWords("true", "false");
        classDefinition.toIntermediateCode(u);
    }

    private String nameWithoutExtension() {
        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        if (name.endsWith(".z")) {
            name = name.substring(0, name.length() - 2);
        }
        return name;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("archivo", fileName);
        printer.child("clase", classDefinition);
    }
}
