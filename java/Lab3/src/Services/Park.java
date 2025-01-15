package StoryWorld;

import Characters.Ruler;

class Park {
    private boolean isOpen;

    public Park() {
        this.isOpen = false;
    }

    public void updateStatusBasedOnRulers() {
        if (Ruler.areAllOutOfCastle()) {
            isOpen = true;
            System.out.println("The park is now open for children!");
        } else {
            isOpen = false;
            System.out.println("The park is closed as rulers are in the castle.");
        }
    }

    public boolean isOpen() {
        return isOpen;
    }
}