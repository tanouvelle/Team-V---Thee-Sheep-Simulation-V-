public class Genes {

    private static double mutationChance = 0.05;
    private double perception;
    private double speed;
    private int lifeSpan;

    private int maxStamina;
    private int staminaUseRate;
    private int staminaRecoverRate;
    private double sprintMultiplier;


    private double hungerCapacity;
    private double hungerRate = 0.0001;
    
    



    //properties
    public double GetPerception(){
        return perception;
    }

    public double GetSpeed()
    {
        return speed;
    }

    public int GetLifespan()
    {
        return lifeSpan;
    }

    public int GetMaxStamina(){
        return maxStamina;
    }

    public int GetStaminaUseRate()
    {
        return staminaUseRate;
    }

    public int GetStaminaRecoverRate()
    {
        return staminaRecoverRate;
    }

    public double GetHungerCapacity()
    {
        return hungerCapacity;
    }

    public double GetHungerRate()
    {
        return hungerRate;
    }

    public double GetSprintMultiplier()
    {
        return sprintMultiplier;
    }


    public Genes(Genes ParentA, Genes ParentB)
    {
         this.perception = InheritGene(ParentA.GetPerception(), ParentB.GetPerception());
         this.speed = InheritGene(ParentA.GetSpeed(), ParentB.GetSpeed());
         this.lifeSpan = InheritGene(ParentA.GetLifespan(), ParentB.GetLifespan());
         this.maxStamina = InheritGene(ParentA.GetMaxStamina(), ParentB.GetMaxStamina());
         this.staminaUseRate = InheritGene(ParentA.GetStaminaUseRate(), ParentB.GetStaminaUseRate());
         this.staminaRecoverRate = InheritGene(ParentA.GetStaminaRecoverRate(), ParentB.GetStaminaRecoverRate());
         this.hungerCapacity = InheritGene(ParentA.GetHungerCapacity(), ParentB.GetHungerCapacity());
         this.sprintMultiplier = InheritGene(ParentA.GetSprintMultiplier(), ParentB.GetSprintMultiplier());
         
    }
    




    private double InheritGene(double geneA, double geneB)
    {
        double roll = Math.random();
        if(roll < mutationChance)
        {
            double newGene = geneA + geneB;
            return newGene * Math.random();
            ///mutate
        }else
        {
            roll = Math.random();
            if(roll > 0.5)
            {
                return geneA;
            }else{
                return geneB;
            }
        }
        
    }

    private int InheritGene(int geneA, int geneB)
    {
        return (int)InheritGene((double)geneA, (double)geneB);
        
    }

}
