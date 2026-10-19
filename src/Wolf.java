import java.awt.*;
import java.util.List;

public class Wolf extends Animal {
    public Wolf() {
        super("Wolf", 1.5, 3.0, 8, null);
        this.colour = Color.gray;
    }
    public Wolf(String name, float speed, float perception, int lifeSpan, Animal[] parents)
    {
        super(name, speed, perception, lifeSpan, parents);
        this.colour = Color.gray;
        entityType = Board.Ent.wolf;
        stealth = 20;

        sprintOnChase = true;
        maxStamina = 200;
        staminaUseRate = 10;
        stamina = 100;

    }  

    @Override
    public Entity LookForFood() {
        List<Entity> allSheep = Board.entities.get(Board.Ent.sheep.get());

        Entity closest = null;

        double closestDist = 99999;
        for (Entity sheep : allSheep) {

            double dist = this.pos.dist(sheep.pos) ;
            if(dist < this.perception && dist < closestDist)
            {
                closest = sheep;
                closestDist = dist;
            }
        }

        if(closest != null)
        {
            // System.out.println("Closest sheep @ " + closest.pos);
        }else{
            // System.out.println("No sheep in range");
        }
        return closest;
    }

    @Override 
    protected void ChasingFood()
    {
        

        Animal targ = (Animal)targetEntity;
        if(targ == null)
        {
            System.out.println("ERROR, targ was null for creature " + name);
            return;
        }
        if(targ.GetState() != AnimalState.fleeing)
        {
            targ.AlertAnimal(this);
        }

        super.ChasingFood();
    }

    
    public void EatSheep(Entity entity) {
        if (entity instanceof Sheep) {
            EatFood(entity);
        }
    }
    
@Override 
    protected void Reproduce(Animal partAnimal)
    {
        super.Reproduce(partAnimal);
       Board.babyAnimals.add(new Wolf(null, 1, 300, 5, new Animal[]{this, partAnimal}));
        
    }
/*
    public void Die() {
        KillEntity();
    }
*/

}
