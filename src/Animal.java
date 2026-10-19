import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public abstract class Animal extends Entity{

    protected enum AnimalState
    {
        lookingForFood, 
        chasingFood,
        lookingForMate,
        chasingMate,
        fleeing;
    }

    private static String[] nameList = {"Mary", "Franky", "Franklin", "Georgie", "Susan", "Laura", "Daisy", "Rose","Cody","Cuddles", "Bill", "Shaun", "Katie", "Brutus", "Scar", "Fido", "Jet", };

    protected String name = "Background Character";

    protected double hunger = 0.5; //Range from 1.0 (Creature full), (0.0) Creature dies)

    public double GetHunger()
    {
        return hunger;
    }

    protected double speed = 1.0; //Tiles moved

    protected double perception = 2.0; //Range of which creature can see

    protected double stealth = 1.0;

    public double GetStealth()
    {
        return stealth;
    }
    
    protected Genes gene;
    public Genes GetGenes()
    {
        return gene;
    }
    protected double sprintMultiplier = 2;

    protected int maxStamina = 400;

    protected int stamina = 200;

    protected int staminaUseRate = 2;

    protected int age = 0; // Increase by 1 every generation
    public int GetAge()
    {
        return age;
    }

    protected int lifeSpan = 5;//Every generation after this, flip coin to see if survives

    private boolean exhausted = false;//used all stamina, must refill to reuse.

    protected Color colour = Color.white;

    protected static double hungerToReproduce = 0.75; //Will look for a mate if hunger above this level;

    private ArrayList<Animal> children = new ArrayList<Animal>();
    private Animal[] parents = new Animal[2];

    protected Entity targetEntity = null;

    public Position randPos;
    private Position fleeTarget;

    protected AnimalState state = AnimalState.lookingForFood;

    public AnimalState GetState(){
        return state;
    }


    protected Animal chasingAnimal; //creature that is chasing THIS animal
    
    //#region
    protected boolean sprintOnChase = false;
    protected boolean sprintOnFlee = false;

    public Animal()
    {
        
    }

    public Animal(String name, double speed, double perception, int lifeSpan, Animal[] parents)
    {
        if(name != null)
        {
            this.name = name;
        }else{
            this.name = nameList[(int)(Math.random() * nameList.length)];
        }
        
        this.speed = speed;
        this.perception = perception;
        this.lifeSpan = lifeSpan * 400;
        this.parents = parents;
        this.pos = new Position(0, 0);
        this.hunger = 0.6;
        if(parents[0] != null)
        {
            this.pos.setX(parents[0].pos.getX());
            this.pos.setY(parents[0].pos.getY());
        }
        this.randPos = Position.genRand(Board.bWidth, Board.bHeight, 0, 100, 40);
    }

    //Moves animal towards destination, returns true if it is at the destination already with buffer.
    private boolean Move(Position destination, int buffer, boolean sprint)
    {
        
        Position direction = pos.dir(destination);
        if(Math.abs(direction.getX()) <= buffer && Math.abs(direction.getY()) <= buffer){return true;}
        
        double moveSpeed = speed;
        
        if(sprint && stamina > 0 && !exhausted)
        {
            moveSpeed *= sprintMultiplier;
            stamina -= staminaUseRate;
            if(stamina < 0)
            {
                exhausted = true;
                stamina = 0;
            }
        }
        
        if(Math.abs(direction.getX()) > Math.abs(direction.getY()))
        {
            pos.setX((int)(pos.getX() + moveSpeed * Math.signum(direction.getX())));
        }else{
            pos.setY((int)(pos.getY() + moveSpeed * Math.signum(direction.getY())));
        }
        return false;
    }

    int wait = 0;
    public void AnimalBehaviour()
    {
        if(!IsAlive()) return;
        hunger -= gene.GetHungerRate();
        age += 1;
        if(state != AnimalState.fleeing && stamina < maxStamina)
        {
            stamina += 1;
            hunger -= 0.00005;
        }
        

        if(stamina > maxStamina)
        {
            exhausted = false;
            stamina = maxStamina;
        }
        if(hunger < 0)
        {
            System.out.println(name + " Starved");
            hunger = 0;
            KillEntity();
            return;
        }else if(age > lifeSpan)
        {
            double roll = Math.random();
            if(roll < 0.01)
            {
                System.out.println(name + " Died of old age" + age);
                KillEntity();
            }
            
        }
        
        switch(state){
            case AnimalState.lookingForFood:
            {
                LookingForFood();
                break;
            }
            case AnimalState.chasingFood:
            {
                ChasingFood();
                break;
            }case AnimalState.lookingForMate:
            {
                LookingForMate();
                break;
            }case AnimalState.chasingMate:
            {
                ChasingMate();
                break;
            }case AnimalState.fleeing:
            {
                Flee();
                break;
            }

        }     
    }

    protected void Flee()
    {

        double dist = this.pos.dist(chasingAnimal.pos) ;
        if(dist > this.perception || !chasingAnimal.IsAlive())
        {
            state = AnimalState.lookingForFood;
            return;
        }
        if(fleeTarget == null)
        {
            fleeTarget = GetFleeTarget(chasingAnimal);
        }
         //temp magic
        if(Move(fleeTarget, 10, sprintOnFlee))
        {
            fleeTarget = GetFleeTarget(chasingAnimal);
        }

      
        
    }

    private Position GetFleeTarget(Animal hunter)
    {
        if(hunter == null){
            System.out.print("Animal " + name + " is fleeing but has no chase target?");
            return new Position(Board.bWidth/2, Board.bHeight/2 + 100);
        }
        int newWidth;
        int newBufferX = 0;
        int newHeight;
        int newBufferY = 100;
        if(pos.getX() > hunter.pos.getX())
        {
            newWidth = Board.bWidth - hunter.pos.getX();
            newBufferX += hunter.pos.getX();
      
            
           
        }else{
            newWidth = hunter.pos.getX();
            

        }

        if(pos.getY() > hunter.pos.getY())
        {
            newHeight = Board.bHeight - hunter.pos.getY();
            newBufferY += hunter.pos.getY();
            

        }else{
            newHeight = hunter.pos.getY();
        }

        newHeight = Math.clamp(newHeight, 0, Board.bHeight);
        newBufferY = Math.clamp(newBufferY, 100, Board.bHeight);
        newWidth = Math.clamp(newWidth, 0, Board.bWidth);
        newBufferX = Math.clamp(newBufferX, 0, Board.bWidth);

        System.out.println(newHeight + " / " + newBufferY);
        int end = newBufferY + newHeight;
        System.out.println(name + " Flee target bounds within " + newBufferY + " - " + end);
        Position targetLocation = Position.genRand(newWidth, newHeight, newBufferX, newBufferY, 40);
        System.out.println("Location " + targetLocation.toString());
        return targetLocation;
    }
    protected void LookingForFood()
    {
        if(targetEntity == null || !targetEntity.IsAlive())
        {
            this.targetEntity = LookForFood();
            if(this.targetEntity != null)
            {
                state = AnimalState.chasingFood;
            }else
            {
                RoamRandomly();
            
            }
        
        }else{
            targetEntity = null;
            RoamRandomly();
        }

    }

    protected void ChasingFood()
    {
        
        if(targetEntity == null || 
            !targetEntity.IsAlive() || 
            pos.dist(targetEntity.pos) > perception)
        {
           targetEntity = null;
            state = AnimalState.lookingForFood;
            return;
        }

   
        if(Move(targetEntity.pos, 10, sprintOnChase))
        {
            EatFood(targetEntity);
            if(hunger >= hungerToReproduce)
            {
                state = AnimalState.lookingForMate;
                //state = AnimalState.lookingForFood;
            }else{
                state = AnimalState.lookingForFood;
            }
        }
    }

    public void AttractMate(Entity ent)
    {
        targetEntity = ent;
        state = AnimalState.chasingMate;
    }
    
    protected void LookingForMate()
    {
        Entity closest = null;
        double closestDist = 99999;        
        for (Entity ent: Board.entities.get(entityType.get()) )
        {
            if(ent == null || ent == this)
            {
                continue;
            }else {
                Animal a = (Animal)ent;
                if(a != null && a.GetState() == AnimalState.lookingForMate)
                {
                    //other creature is not ready to mate
                    a.AttractMate(this);
                    
                }else{
                    continue;
                }
            }
            double dist = this.pos.dist(ent.pos) ;
            if(dist < this.perception*100 && dist < closestDist)
            {
                closest = ent;
                closestDist = dist;
            }
        }


            

        if(closest != null)
        {
            targetEntity = closest;
            state = AnimalState.chasingMate;
        }else if(hunger < hungerToReproduce)
        {
            System.out.println(name + " is now to hungry for love :(");
            state = AnimalState.lookingForFood;
        }else
        {
            RoamRandomly();
        }
        
    }

    protected void RoamRandomly()
    {
        boolean moved = Move(randPos, 10, false);
            if (moved && (wait == 0 || hunger < 0.25))
            {   
                wait = 100;
                randPos = Position.genRand(Board.bWidth, Board.bHeight, 0, 100, 40);
            }
            else if (moved) {
                wait--;
            }
    }
    protected void ChasingMate()
    {
        if(hunger < hungerToReproduce)
        {
            state = AnimalState.lookingForFood;
            targetEntity = null;
            return;
        }
        if(targetEntity == null || !targetEntity.IsAlive())
        {
            targetEntity = null;
            state = AnimalState.lookingForMate;
        }
        try{
            if(Move(targetEntity.pos, 10, false))
            {
                System.out.println("reproducing");
                Reproduce((Animal)targetEntity);
                System.out.println("baby Created");
                state = AnimalState.lookingForFood;

            }
        } catch(NullPointerException npe) {
            System.out.println(this + "Target Entity not found");
        }
    }

    public void AlertAnimal(Animal hunter)
    {
        if(state == AnimalState.fleeing) return;

        double distance = pos.dist(hunter.pos);

        double effectiveRange = perception - hunter.GetStealth();

        double chance = 1.0 - (distance / effectiveRange);
        chance = Math.max(0, Math.min(1, chance));

        if(Math.random() < chance)
        {
            state = AnimalState.fleeing;
            chasingAnimal = hunter;
        }
    }
    protected void Reproduce(Animal anim)
    {
        targetEntity = null;
        
        hunger = hunger - 0.4f;
        anim.hunger = anim.hunger - 0.4f;
        anim.targetEntity = null;



        //temp
        
        
    }
    public Entity LookForFood()
    {
        //System.out.println("This is the base look for food function, should not be called");
        return null;
    }

    
    public void EatFood(Entity entity)
    {
       
        hunger += entity.nutrition;
        stamina += entity.nutrition * 200;
        
        if(hunger > 1.0)
        {
            hunger = 1.0;
        }
        System.out.println("Creature "+ name + " ate entity worth "+ entity.nutrition + " nutrition. Total hunger now: " + hunger);
        entity.KillEntity();
        targetEntity = null;

    }


    
    public void Reproduce() 
    {
        
    }

    @Override
    public void drawEntity(Graphics g) {
        g.setColor(colour);
        g.fillRect(pos.getX()-20, pos.getY()-20, 40,40);

        if(!Board.debug) return;
        g.drawOval(pos.getX() - (int)perception/2, pos.getY() - (int)perception/2, (int)perception, (int)perception);


        int xOffset = pos.getX()- 20;
        int yOffset = pos.getY() - 20;
        g.setColor(Color.RED);
        g.drawString(name.toString(), xOffset, yOffset + 2);
        g.setColor(Color.green);
        double rounded = Math.round(hunger * 100.0) / 100.0;

        g.drawString(Double.toString(rounded), xOffset ,  yOffset + 15);

        g.setColor(Color.BLUE);
        g.drawString(state.toString(), xOffset, yOffset + 30);

        g.setColor(Color.PINK);
        g.drawString(String.valueOf(age) + " / " +  String.valueOf(lifeSpan), xOffset, yOffset + 40);

        g.setColor(Color.YELLOW);
        g.drawString(stamina + " / " +  maxStamina, xOffset, yOffset + 50);

        
        if(targetEntity != null && targetEntity.IsAlive())
        {
            g.setColor(Color.RED);
            g.drawLine(pos.getX(), pos.getY(), targetEntity.pos.getX(), targetEntity.pos.getY());
        }
        
        

        
    }
}
