package practica.compi2.c3d.natives;

import java.util.List;
import practica.compi2.c3d.IntermediateCodeUtils;

public class NativeLibrary {

    public static void generate(IntermediateCodeUtils utils) {
        List<NativeFunction> natives = List.of(
                new PrintStringNative(),
                new ConcatNative(),
                new IntToStringNative(),
                new FloatToStringNative(),
                new CompareStringsNative(),
                new ReadLineNative(),
                new StringToIntNative(),
                new StringToFloatNative());
        for (NativeFunction nativeFunction : natives) {
            nativeFunction.generate(utils);
        }
    }
}
