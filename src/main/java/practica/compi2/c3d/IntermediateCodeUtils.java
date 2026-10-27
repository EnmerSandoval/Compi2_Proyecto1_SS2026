package practica.compi2.c3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Cuarteta;
import practica.compi2.c3d.cuartetas.FunctionEnd3D;
import practica.compi2.c3d.cuartetas.FunctionStart3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.semantic.symbols.ClassSymbol;
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
    private List<ClassSymbol> classes;
    private TemporalSaver temporalSaver;
    private ReturnLinker returnLinker;

    public IntermediateCodeUtils() {
        this.cuartetas = new ArrayList<>();
        this.counter = new IdsCounter();
        this.breakLabels = new Stack<>();
        this.continueLabels = new Stack<>();
        this.structs = new HashMap<>();
        this.trueText = "verdadero";
        this.falseText = "falso";
        this.classes = new ArrayList<>();
        this.temporalSaver = new TemporalSaver(counter);
        this.returnLinker = new ReturnLinker();
    }

    public void startFunction(String name, int size) {
        begin(name, false, size);
    }

    public void startMain(int size) {
        begin("main", true, size);
    }

    public void endFunction() {
        temporalSaver.saveAroundCalls(cuartetas, cuartetas.indexOf(currentFunction));
        cuartetas.add(new LabelDefinition3D(currentFunction.getEndLabel()));
        FunctionEnd3D end = new FunctionEnd3D(currentFunction.getName(), currentFunction.isMain());
        if (!currentFunction.isMain()) {
            returnLinker.addReturnAddress(end, loadReturnAddress());
        }
        cuartetas.add(end);
        currentFunction = null;
    }

    public void registerCall(String name, int returnLabel, CallSite site) {
        returnLinker.addCall(name, returnLabel);
        temporalSaver.addCall(site);
    }

    public List<Cuarteta> getMainFirst() {
        int mainStart = 0;
        while (mainStart < cuartetas.size()
                && !(cuartetas.get(mainStart) instanceof FunctionStart3D start && start.isMain())) {
            mainStart++;
        }
        int mainEnd = mainStart;
        while (!(cuartetas.get(mainEnd) instanceof FunctionEnd3D)) {
            mainEnd++;
        }
        List<Cuarteta> ordered = new ArrayList<>(cuartetas.subList(mainStart, mainEnd + 1));
        ordered.addAll(cuartetas.subList(0, mainStart));
        ordered.addAll(cuartetas.subList(mainEnd + 1, cuartetas.size()));
        return ordered;
    }

    public void registerClass(ClassSymbol classSymbol) {
        classes.add(classSymbol);
    }

    public void linkReturns() {
        cuartetas = returnLinker.link(cuartetas);
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
        currentFunction = new FunctionStart3D(name, main, counter.getAndIncrementLabelCount());
        frameSize = size;
        cuartetas.add(currentFunction);
    }

    private TemporalAccess loadReturnAddress() {
        TemporalAccess slot = new TemporalAccess(counter.getAndIncrementTemporalCount());
        TemporalAccess address = new TemporalAccess(counter.getAndIncrementTemporalCount());
        cuartetas.add(new Operation3D(slot, PointerAccess.STACK, new Literal3D(1), "-"));
        cuartetas.add(new Assignation3D(address, new StackAccess(slot)));
        return address;
    }
}
