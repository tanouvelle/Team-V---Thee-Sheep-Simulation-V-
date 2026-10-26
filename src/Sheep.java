import java.util.List;
public class Sheep extends Animal{

    
    public Sheep(Genes genes, Animal[] parents, int age)
    {
        super(genes, parents, age);
        entityType = Board.Ent.sheep;

        sprintOnFlee = true;
    }
    /*
    public Sheep(String name, float speed, float perception, int lifeSpan, Animal[] parents)
    {
        super(name, speed, perception, lifeSpan, parents);
        nutrition = 0.5;
        entityType = Board.Ent.sheep;

        sprintOnFlee = true;
        

    }   
    */

    @Override
    public Entity LookForFood()
    {
        //tempo just to allow for logic development. Replace with proper entity database at some point
        // Do flowers first, then grass
        List<Entity> allPlants = Board.entities.get(Board.Ent.flower.get());
    

        Entity closest = null;
        double closestDist = 99999;
        for (Entity flower : allPlants) {

            double dist = this.pos.dist(flower.pos) ;
            if(dist < genes.GetPerception() && dist < closestDist)
            {
                closest = flower;
                closestDist = dist;
            }
        }
        if(closest != null)
        {
            //System.out.println("Closest flower @ " + closest.pos);
            return closest;
        }else{
            //System.out.println("No flowers in range");

        }
        allPlants = Board.entities.get(Board.Ent.grass.get());
        closestDist = 99999;
        for (Entity grass : allPlants) {

            double dist = this.pos.dist(grass.pos) ;
            if(dist < genes.GetPerception() && dist < closestDist)
            {
                closest = grass;
                closestDist = dist;
            }
        }

        return closest;


        
    }

    @Override 
    protected void Reproduce(Animal partAnimal)
    {

        System.out.println("Making baby sheep");
        
        Genes babyGenes = new Genes(genes, partAnimal.GetGenes());
        Sheep newSheep = new Sheep(babyGenes, new Animal[]{this, partAnimal}, 0);
        Board.babyAnimals.add( newSheep);


        super.Reproduce(partAnimal);
        
    }


}
