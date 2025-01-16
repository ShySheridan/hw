package Characters.features;

public record CabbageSeeds(int totalSeeds, int sproutedSeeds) {
    public double successRate() {
        return totalSeeds > 0 ? (double) sproutedSeeds / totalSeeds * 100 : 0.0;
    }
    @Override
    public String toString() {
        return String.format("Total seeds: %d, Sprouted seeds: %d, Success rate: %.2f%%",
                totalSeeds, sproutedSeeds, successRate());
    }
}
