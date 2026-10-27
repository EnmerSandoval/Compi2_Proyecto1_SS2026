package practica.compi2.c3d;

import lombok.AllArgsConstructor;
import lombok.Getter;
import practica.compi2.c3d.access.FrameOffset3D;
import practica.compi2.c3d.cuartetas.Cuarteta;

@Getter
@AllArgsConstructor
public class CallSite {

    private Cuarteta start;
    private Cuarteta end;
    private FrameOffset3D offset;
    private int frameSize;
}
