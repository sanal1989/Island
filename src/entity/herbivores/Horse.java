package entity.herbivores;



public class Horse extends Herbivores {
    public static final double MAX_EAT_UP = 45;
    public static final int MAX_DEATH = 5;
    public static int newHorse = 0;
    public static int deathHorse = 0;
    public Horse() {
        weight = 300;
        stepDeath = 0;
    }

    @Override
    public void eat() {

    }

    @Override
    public void move() {

    }

    @Override
    public void reproduce() {

    }
}