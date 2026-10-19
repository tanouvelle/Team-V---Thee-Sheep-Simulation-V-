import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;

public class Board extends JPanel implements ActionListener {
    public static int bWidth; // Board width
    public static int bHeight; // Board height
    private static int tic; // Simulation tic
    public static int GetTic()
    {
        return tic;
    }
    private Timer timer;
    public static List<List<Entity>> entities;
    public static List<Animal> babyAnimals;
    public static Plant[][] plantGrid;

    public static boolean debug;
    public Board(int bWidth, int bHeight) {
        Board.bWidth = bWidth;
        Board.bHeight = bHeight;
        
        init();
    }

    //Initialise the board
    private void init() {
        addKeyListener(new KeyInput());
        setLayout(null);
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(bWidth, bHeight + 100));

        resetSimulation();
    }

    private void resetSimulation() {

        babyAnimals = new ArrayList<>();
        entities = new ArrayList<>();
        entities.add(new ArrayList<>());
        entities.add(new ArrayList<>());
        entities.add(new ArrayList<>());
        entities.add(new ArrayList<>());
        spawnEntities();
        entities.stream()
                  .forEach(list -> list.stream()
                                      .map(ent -> ent.pos = Position.genRand(bWidth, bHeight, 0, 100, 40))
                                      .collect(Collectors.toList())
                          );

        plantGrid = new Plant[12][16];
        for(Entity e : entities.get(Ent.flower.get())) {
            plantGrid[(e.pos.getY() - 100) / 40][e.pos.getX() / 40] = (Flower)e;
        }

        for(Entity e : entities.get(Ent.grass.get())) {
            plantGrid[(e.pos.getY() - 100) / 40][e.pos.getX() / 40] = (Grass)e;
        }

        tic = 1;
        nextSpawn = 300;
        timer = new Timer(10, this);
        timer.start();
    }

    /*
        Entity start amounts:

        Sheep: 5-8
        Wolves: 2-3
        Grass: 8-12
        Flowers: 0-2
    */
    private void spawnEntities() {
        //Spawn sheep
        int startAmount = (int)(Math.random() * 3 + 5);
        for (int i = 0; i < startAmount; i++) {
            float speed = (int)(Math.random() * 2) + 1;
            float perception = 10 * ((int)(Math.random() * 4) + 6);
            Sheep newSheep = new Sheep(null, speed, perception, 5, new Animal[]{null, null});
            entities.get(Ent.sheep.get()).add(newSheep);
        }

        //Spawn wolves
        startAmount = (int)(Math.random() + 2);
        for (int i = 0; i < startAmount; i++) {
            float speed = 1;
            float perception = 10 * ((int)(Math.random() * 2) + 10);
            Wolf newWolf = new Wolf(null, speed, perception, 3, new Animal[]{null, null});
            entities.get(Ent.wolf.get()).add(newWolf);
        }

        //Spawn Grass
        startAmount = (int)(Math.random() * 4 + 8);
        for (int i = 0; i < startAmount; i++) {
            entities.get(Ent.grass.get()).add(new Grass());
        }

        //Spawn Flower(s)
        startAmount = (int)(Math.random() * 2);
        for (int i = 0; i < startAmount; i++) {
            entities.get(Ent.flower.get()).add(new Flower());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.gray);
        g.fillRect(0, 0, bWidth, 100);
        drawEntities(g);
        drawGrid(g);
        drawCounts(g);
    }

    private void drawEntities(Graphics g) {
        entities.stream()
                .forEach(list -> {
                    list.stream()
                        .peek(ent -> ent.drawEntity(g))
                        .filter(ent -> ent instanceof Animal)
                        .map(ent -> (Animal)ent)
                        .forEach(animal -> animal.AnimalBehaviour());
                });
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.white);
        for (int i = 40; i < bWidth; i += 40) {
            g.drawLine(i, 100, i, bHeight + 100);
        }
        for (int i = 100; i < bHeight + 100; i += 40) {
            g.drawLine(0, i, bWidth, i);
        }
    }

    Font medium = new Font("Helvetica", Font.PLAIN, 16);
    private void drawCounts(Graphics g) {
        g.setFont(medium);
        FontMetrics metrics = g.getFontMetrics();
        int yPos =  ((100 - metrics.getHeight()) / 2) + metrics.getAscent();

        int nextX = bWidth / 16;
        g.drawString("Sheep: " + entities.get(Ent.sheep.get()).size(), nextX, yPos);

        nextX += bWidth / 4;
        g.drawString("Wolves: " + entities.get(Ent.wolf.get()).size(), nextX, yPos);

        nextX += bWidth / 4;
        g.drawString("Flowers: " + entities.get(Ent.flower.get()).size(), nextX, yPos);

        nextX += bWidth / 4;
        g.drawString("Grass: " + entities.get(Ent.grass.get()).size(), nextX, yPos);

        g.drawString("Tic: " + tic, bWidth/2, 30);
    }
    private int nextSpawn = 50;
    @Override
    // After the timer finishes do this
    public void actionPerformed(ActionEvent e) {
        // if (tic >= nextSpawn) {
        //     Flower newFlower = new Flower();
        //     do {
        //         newFlower.pos = Position.genRand(bWidth, bHeight, 0, 100, 40);
        //     } while (plantGrid[(newFlower.pos.getY() - 100) / 40][newFlower.pos.getX() / 40] != null);
        //     entities.get(Ent.flower.get()).add(newFlower);
        //     nextSpawn = tic + (int)(Math.random() * 300 + 200);
        //     //((Grass)entities.get(Ent.grass.get()).get(0)).spreadGrass();
            
        // }
        if (tic >= nextSpawn) {
            spreadGrass();
            if (entities.get(Ent.grass.get()).size() <= 2) {
                Grass newGrass = new Grass();
                do {
                    newGrass.pos = Position.genRand(bWidth, bHeight, 0, 100, 40);
                } while (plantGrid[(newGrass.pos.getY() - 100) / 40][newGrass.pos.getX() / 40] != null);
                entities.get(Ent.grass.get()).add(newGrass);
                plantGrid[(newGrass.pos.getY() - 100) / 40][newGrass.pos.getX() / 40] = newGrass;
            }
        }
        repaint();
        tic++;

        CreateChildren();
        CleanUp(); 
    }
    // private void printPlantGrid() {
    //     for (Plant[] arr : plantGrid) {
    //         for (Plant p : arr) {
    //             if (p == null) {
    //                 System.out.print("- ");
    //             }
    //             else if (p instanceof Grass) {
    //                 System.out.print("g ");
    //             }
    //             else if (p instanceof Flower) {
    //                 System.out.print("f ");
    //             }
    //         }
    //         System.out.println();
    //     }
    // }

    private void spreadGrass() {
        List<Entity> newGrass = new ArrayList<>();
        List<Entity> newFlower = new ArrayList<>();
        int maxSpawn = Math.max(entities.get(Ent.sheep.get()).size(), 3);
        newGrass = entities.get(Ent.grass.get()).stream()
                                                .map(en -> ((Grass)en).spreadGrass())
                                                .filter(en -> en != null)
                                                .limit(maxSpawn)
                                                .collect(Collectors.toList());

        newFlower = newGrass.stream()
                            .filter(en -> en instanceof Flower)
                            .collect(Collectors.toList());
        
        newGrass = newGrass.stream()
                            .filter(en -> en instanceof Grass)
                            .collect(Collectors.toList());
        
        entities.get(Ent.grass.get()).addAll(newGrass);
        entities.get(Ent.flower.get()).addAll(newFlower);
        nextSpawn = tic + (int)(Math.random() * 200 + 75);
    }

    private void CreateChildren()
    {
        babyAnimals.stream()
                    .forEach(baby -> entities.get(baby.GetType().get()).add(baby));

        babyAnimals.clear();
    }

    private void CleanUp()
    {
        
        for(List<Entity> subList : entities)
        {
            for(int i = subList.size() -1; i >= 0; i--)
            {
                if(!subList.get(i).IsAlive())
                {   
                    if (subList.get(i) instanceof Plant) {
                        plantGrid[(subList.get(i).pos.getY() - 100) / 40][subList.get(i).pos.getX() / 40] = null;
                    }
                    subList.remove(i);
                }
            }
        }
    }

    public enum Ent {
        sheep(2), wolf(3), flower(1), grass(0);

        private final int index;
        
        private Ent(int index) {
            this.index = index;
        }

        public int get() {
            return index;
        }
    }

    private class KeyInput extends KeyAdapter {
        @Override 
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_R) {
                entities.clear();
                timer.stop();
                resetSimulation();
            }

            if(keyCode == KeyEvent.VK_Q)
            {
                debug = !debug;
            }
        }
    }
}
