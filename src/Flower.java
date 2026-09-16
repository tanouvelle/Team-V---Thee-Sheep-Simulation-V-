import java.awt.Color;

public class Flower extends Plant {

    Color fColour;
    public Flower()
    {
        float randomR = (float)Math.random() * 0.75f + 0.25f;
        float randomG = 0;
        float randomB = (float)Math.random() * 0.75f + 0.25f;

        fColour = new Color(randomR, randomG, randomB);
        this.nutrition = 0.6f;
        this.name = "Flower";
        this.pColour = fColour;
        entityType = Board.Ent.flower;
    }
}
