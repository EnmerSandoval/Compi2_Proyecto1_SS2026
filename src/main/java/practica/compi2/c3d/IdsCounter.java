package practica.compi2.c3d;

import lombok.Getter;

@Getter
public class IdsCounter {

    private Integer temporalCount;
    private Integer labelCount;

    public IdsCounter() {
        this.temporalCount = 0;
        this.labelCount = 0;
    }

    public int getAndIncrementTemporalCount() {
        temporalCount++;
        return temporalCount - 1;
    }

    public int getAndIncrementLabelCount() {
        labelCount++;
        return labelCount - 1;
    }
}
