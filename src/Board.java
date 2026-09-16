import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class Board extends JPanel implements ActionListener {
    public static int bWidth; // Board width
    public static int bHeight; // Board height
    private int tic; // Simulation tic
    private Timer timer;
    public static List<List<Entity>> entities;
    public static List<Animal> babyAnimals;
    public static Plant[][] plantGrid;

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
        entities.get(Ent.sheep.get()).add(new Sheep("Mary", 1, 100, 5, new Animal[]{null, null}));
        entities.get(Ent.sheep.get()).add(new Sheep("Franky", 2, 70, 5, new Animal[]{null, null}));
        entities.get(Ent.sheep.get()).add(new Sheep("Bert", 1, 100, 5, new Animal[]{null, null}));
        entities.get(Ent.sheep.get()).add(new Sheep("Henry VII", 2, 70, 5, new Animal[]{null, null}));
        entities.get(Ent.sheep.get()).add(new Sheep("Mary3", 1, 60, 5, new Animal[]{null, null}));

        entities.get(Ent.wolf.get()).add(new Wolf("Fido", 1, 120, 5, new Animal[]{null, null}));
        entities.get(Ent.wolf.get()).add(new Wolf("Scar", 1, 120, 5, new Animal[]{null, null}));
        // for(int i =0; i < 20; i ++)
        // {
        //     entities.get(Ent.flower.get()).add(new Flower());
        // }
        //entities.get(Ent.flower.get()).add(new Flower());
        entities.get(Ent.grass.get()).add(new Grass());
        entities.get(Ent.grass.get()).add(new Grass());
        entities.get(Ent.grass.get()).add(new Grass());
        entities.get(Ent.grass.get()).add(new Grass());
        entities.get(Ent.grass.get()).add(new Grass());
        entities.get(Ent.grass.get()).add(new Grass());
        for (List<Entity> list : entities) {
            for (Entity ent : list) {
                ent.pos = Position.genRand(bWidth, bHeight, 0, 100, 40);
                System.out.println(ent);
            }
        }

        plantGrid = new Plant[12][16];
        for(Entity e : entities.get(Ent.flower.get())) {
            plantGrid[(e.pos.getY() - 100) / 40][e.pos.getX() / 40] = (Flower)e;
        }

        for(Entity e : entities.get(Ent.grass.get())) {
            plantGrid[(e.pos.getY() - 100) / 40][e.pos.getX() / 40] = (Grass)e;
        }

        tic = 1;

        timer = new Timer(10, this);
        timer.start();
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
        for (List<Entity> list : entities) {
            for (Entity ent : list) {
                ent.drawEntity(g);
                if(ent instanceof Animal)
                {
                    Animal animal = (Animal)ent;
                    animal.AnimalBehaviour();
                    if (tic % 100 == 0) {

                        //System.out.println(animal + " " + animal.name + " Hunger " + animal.GetHunger() + "State: " + animal.GetState().toString());
                    }
                }
            }
        }
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
    }
    private int nextSpawn = 300;
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
        for (Entity en : entities.get(Ent.grass.get())) {
            Plant newG = ((Grass)en).spreadGrass();
            if (newG != null) {
                if (newG instanceof Flower) {
                    newFlower.add(newG);
                }
                else {
                    newGrass.add(newG);
                }
                maxSpawn--;
            }
            if (maxSpawn <= 0) {
                break;
            }
        }
        entities.get(Ent.grass.get()).addAll(newGrass);
        entities.get(Ent.flower.get()).addAll(newFlower);
        nextSpawn = tic + (int)(Math.random() * 300 + 200);
    }

    private void CreateChildren()
    {
        for(Animal baby : babyAnimals )
        {
            entities.get(baby.GetType().get()).add(baby);
        }

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
        }
    }
}
