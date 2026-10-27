package practica.compi2.c3d;

import practica.compi2.ast.Ast;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.ast.programs.ZetarianoProgram;
import practica.compi2.c3d.natives.NativeLibrary;
import practica.compi2.semantic.types.TypeTable;

public class IntermediateCodeGenerator {

    public IntermediateCodeUtils generate(Ast ast, TypeTable typeTable) {
        IntermediateCodeUtils u = new IntermediateCodeUtils();
        u.setTypeTable(typeTable);
        for (YProgram program : ast.getYPrograms()) {
            program.registerStructs(u);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            u.registerClass(program.getClassDefinition().getSymbol());
        }
        NativeLibrary.generate(u);
        for (YProgram program : ast.getYPrograms()) {
            program.toIntermediateCode(u);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            program.toIntermediateCode(u);
        }
        ast.getMain().toIntermediateCode(u);
        u.linkReturns();
        return u;
    }
}
