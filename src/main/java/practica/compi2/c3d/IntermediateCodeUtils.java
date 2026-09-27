package practica.compi2.c3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.c3d.cuartetas.Cuarteta;
import practica.compi2.c3d.cuartetas.FunctionEnd3D;
import practica.compi2.c3d.cuartetas.FunctionStart3D;
import practica.compi2.semantic.symbols.StructSymbol;
import practica.compi2.semantic.types.TypeTable;

@Getter
public class IntermediateCodeUtils {

    private List<Cuarteta> cuartetas;
    private IdsCounter counter;
    private Stack<Integer> breakLabels;
    private Stack<Integer> continueLabels;
    private Map<String, StructSymbol> structs;
    private FunctionStart3D currentFunction;
    private int frameSize;
    private String trueText;
    private String falseText;
    @Setter
    private TypeTable typeTable;

    public IntermediateCodeUtils() {
        this.cuartetas = new ArrayList<>();
        this.counter = new IdsCounter();
        this.breakLabels = new Stack<>();
        this.continueLabels = new Stack<>();
        this.structs = new HashMap<>();
        this.trueText = "verdadero";
        this.falseText = "falso";
    }

    public void startFunction(String name, int size) {
        begin(name, false, size);
    }

    public void startMain(int size) {
        begin("main", true, size);
    }

    public void endFunction() {
        currentFunction.setLastTemporal(counter.getTemporalCount());
        cuartetas.add(new FunctionEnd3D(currentFunction.getName(), currentFunction.isMain()));
        currentFunction = null;
    }

    public void enterLoop(int continueLabel, int breakLabel) {
        continueLabels.push(continueLabel);
        breakLabels.push(breakLabel);
    }

    public void exitLoop() {
        continueLabels.pop();
        breakLabels.pop();
    }

    public void enterSwitch(int breakLabel) {
        breakLabels.push(breakLabel);
    }

    public void exitSwitch() {
        breakLabels.pop();
    }

    public int getBreakLabel() {
        return breakLabels.peek();
    }

    public int getContinueLabel() {
        return continueLabels.peek();
    }

    public void registerStruct(StructSymbol struct) {
        structs.put(struct.getName(), struct);
    }

    public StructSymbol getStruct(String name) {
        return structs.get(name);
    }

    public void useBooleanWords(String trueWord, String falseWord) {
        this.trueText = trueWord;
        this.falseText = falseWord;
    }

    private void begin(String name, boolean main, int size) {
        currentFunction = new FunctionStart3D(name, main, counter.getTemporalCount());
        frameSize = size;
        cuartetas.add(currentFunction);
    }
}
