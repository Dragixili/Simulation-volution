public class Main {
    class World {

    }

    class Creature {
        //toutes les variables en float = double à peut près
        double speed;
        double vision;
        double efficiency;
        double energie;
        double x, y;
        
        //initialisation des variables aléatoire selon l'individu, math.random() donne une valeur entre 0.0 et 1.0
        Creature() {
            speed = 1.0 + Math.random();
            vision = 5 * Math.random() + 1.0;
            efficiency = 0.5 + Math.random();
            energie = 100;
            x = Math.random() * 800;
            y = Math.random() * 600;
        }

        void mutate() {
            speed *= 1 + (Math.random() - 0.5) * 0.1; // + ou - 5%
            vision *= 1 + (Math.random() - 0.5) * 0.1;
            efficiency *= 1 + (Math.random() - 0.5) * 0.1;
        }

        Creature reproduce() {
            Creature child = new Creature();
            child.speed = this.speed;
            child.vision = this.vision;
            child.efficiency = this.efficiency;

            child.mutate();


            return child;
        }

        void move(World world) {
            //déplacement aléatoire
            x += (Math.random() - 0.5)* speed * 5;
            y += (Math.random() - 0.5)* speed * 5;

            energie -= speed * (1.0 / efficiency);
        }
    }

    class food {
        double x;
        double y;
        double energy;

        food (){
            //posistion aléatoire
            x = Math.random() * 800;
            y = Math.random() * 600;
            //energy donné
            energy = 20 + Math.random() * 30; // entre 20 et 50
        }
    }
}
