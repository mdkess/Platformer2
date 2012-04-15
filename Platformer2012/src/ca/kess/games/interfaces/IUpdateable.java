package ca.kess.games.interfaces;

public interface IUpdateable {
    /**
     * Update the entity one step.
     * 
     * Should use Constants.DELTA as the amount of time.
     */
    public void update();
}
