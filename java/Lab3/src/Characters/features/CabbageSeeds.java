package Characters.features;

public class CabbageSeeds {
    private int unripeSeeds;

    public CabbageSeeds(int unripeSeeds){
        if (unripeSeeds < 0) {
            throw new IllegalArgumentException("the number of seeds cannot be less than zero");
        }
        this.unripeSeeds = unripeSeeds;
        }
    }
}
