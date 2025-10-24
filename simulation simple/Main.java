import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Main extends JPanel {

    // Dimensions du monde
    static final int WIDTH = 500;
    static final int HEIGHT = 200;

    class Creature {
        double speed;
        double vision;
        double efficiency;
        double energie;
        double x, y;

        Creature() {
            speed = 1.2 + Math.random();
            vision = 10 + Math.random() * 15;
            efficiency = 0.8 + Math.random() * 0.5;
            energie = 100;
            x = Math.random() * WIDTH;
            y = Math.random() * HEIGHT;
        }

        void mutate() {
            speed *= 1 + (Math.random() - 0.5) * 0.1;
            vision *= 1 + (Math.random() - 0.5) * 0.1;
            efficiency *= 1 + (Math.random() - 0.5) * 0.1;
        }

        Creature reproduce() {
            Creature child = new Creature();
            child.speed = this.speed;
            child.vision = this.vision;
            child.efficiency = this.efficiency;
            child.mutate();
            child.x = this.x + (Math.random() - 0.5) * 20;
            child.y = this.y + (Math.random() - 0.5) * 20;
            child.x = Math.max(0, Math.min(WIDTH, child.x));
            child.y = Math.max(0, Math.min(HEIGHT, child.y));
            return child;
        }

        void move(World world) {
            x += (Math.random() - 0.5) * speed * 5;
            y += (Math.random() - 0.5) * speed * 5;

            x = Math.max(0, Math.min(WIDTH, x));
            y = Math.max(0, Math.min(HEIGHT, y));

            energie -= speed * (0.05 / efficiency);
        }
    }

    class Food {
        double x, y, energy;

        Food() {
            x = Math.random() * WIDTH;
            y = Math.random() * HEIGHT;
            energy = 80 + Math.random() * 40;
        }
    }

    class World {
        List<Creature> creatures;
        List<Food> foodList;
        int generationCount = 0;

        World(int numCreatures, int numFood) {
            creatures = new ArrayList<>();
            foodList = new ArrayList<>();

            for (int i = 0; i < numCreatures; i++) creatures.add(new Creature());
            for (int i = 0; i < numFood; i++) foodList.add(new Food());
        }

        void update() {
            List<Food> eatenFood = new ArrayList<>();

            for (Creature c : creatures) {
                c.move(this);

                for (Food f : foodList) {
                    double dist = Math.hypot(c.x - f.x, c.y - f.y);
                    if(dist < c.vision){
                        if (dist < 10) {
                            c.energie += f.energy;
                            eatenFood.add(f);
                            break;
                        }
                    }
                }
            }

            foodList.removeAll(eatenFood);
            creatures.removeIf(c -> c.energie <= 0);

            if (foodList.size() < 300) foodList.add(new Food());
        }

        void nextGeneration() {
            List<Creature> newGen = new ArrayList<>();
            List<Creature> survivors = new ArrayList<>();

            for (Creature c : creatures)
                if (c.energie > 100) survivors.add(c);

            if (survivors.size() == 0 && creatures.size() > 0) {
                System.out.println("⚠ Extinction évitée !");
                survivors.add(creatures.get(new Random().nextInt(creatures.size())));
            }

            for (Creature s : survivors) {
                newGen.add(s);
                newGen.add(s.reproduce());
            }

            creatures = newGen;
            for (Creature c : creatures) c.energie = 100;
        }

        void run(int steps, JFrame frame, Main panel) {
            for (int i = 0; i < steps; i++) {
                update();

                double vision_moy = 0.0; 
                double speed_moy = 0.0;
                double efficiency_moy = 0.0;

                for (Creature c : creatures) {
                    vision_moy += c.vision;
                    speed_moy += c.speed;
                    efficiency_moy += c.efficiency;
                }

                int nbcreatures = creatures.size();
                if (nbcreatures > 0) {
                    vision_moy /= nbcreatures;
                    speed_moy /= nbcreatures;
                    efficiency_moy /= nbcreatures;
                }


                if (i % 20 == 0) {
                    nextGeneration();
                    generationCount++;
                    System.out.println("---- Génération " + generationCount + " | Créatures: " + creatures.size() + " | Nourriture: " + foodList.size()
                    + " | vision = " + vision_moy + " | speed = " + speed_moy + " | efficiency = " + efficiency_moy);
                }

                panel.repaint();
                try { 
                    Thread.sleep(50); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    World world;

    public Main() {
        world = new World(40, 400);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Fond blanc
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Nourriture en vert
        g.setColor(Color.GREEN);
        for (Food f : world.foodList)
            g.fillOval((int) f.x - 2, (int) f.y - 2, 4, 4);

        // Créatures en rouge
        g.setColor(Color.RED);
        for (Creature c : world.creatures)
            g.fillOval((int) c.x - 4, (int) c.y - 4, 8, 8);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main sim = new Main();
            
            JFrame frame = new JFrame("Evolution - Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(sim);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Lancer la simulation dans un thread séparé
            new Thread(() -> sim.world.run(5000, frame, sim)).start();
        });
    }
}