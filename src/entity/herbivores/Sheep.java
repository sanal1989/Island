package entity.herbivores;

import entity.Cell;
import entity.Plant;
import world.Properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Sheep extends Herbivores {
    public static int newSheep = 0;
    public static int deathSheep = 0;
    private String name = new String();

    public Sheep() {
        weight = Properties.WEIGHT_SHEEP;
        stepDeath = 0;
        name = Integer.toString(newSheep);
        satiety = Properties.SATIETY_SHEEP;
        isEat = true;
        isMove = false;
        isReproduce = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sheep sheep = (Sheep) o;
        return Objects.equals(name, sheep.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Sheep{" +
                "weight=" + weight +
                ", satiety=" + satiety +
                ", stepDeath=" + stepDeath +
                '}';
    }

    public static void life(Cell[][] cells, int i, int j) {
        ArrayList<Sheep> sheeps = cells[i][j].getSheep();
        ArrayList<Sheep> newSheep = new ArrayList<>();
        ArrayList<Sheep> arrayListSheepNeedToDelete = new ArrayList<>();
        for (int k = 0; k < sheeps.size(); k++) {
            Sheep sheep = sheeps.get(k);
            //кушаем
            List<Plant> plants = cells[i][j].getSynchronizedPlant();
            List<Plant> needToDeletePlant = sheep.eat(plants);
            for (Plant plant : needToDeletePlant) {
                plants.remove(plant);
            }

            //размножаемся
            newSheep.addAll(sheep.reproduce(cells, i, j));

            //передвижение
            Sheep sheepMove = sheep.move(cells, i, j);
            if (sheepMove != null) {
                arrayListSheepNeedToDelete.add(sheepMove);
            }

        }
        sheeps.addAll(newSheep);
        for (Sheep sheep : arrayListSheepNeedToDelete) {
            sheeps.remove(sheep);
        }

        int needToKill = 0;
        Iterator<Sheep> iterator = sheeps.iterator();

        if (sheeps.size() > Properties.MAX_SHEEP) {
            needToKill = Math.abs(sheeps.size() - Properties.MAX_SHEEP);
            while (needToKill > 0) {
                iterator.next();
                iterator.remove();
                Sheep.deathSheep++;
                needToKill--;
            }
        } else {
            while (iterator.hasNext()) {
                Sheep sheep = iterator.next();
                if (sheep.stepDeath == 3) {
                    iterator.remove();
                    Sheep.deathSheep++;
                }
            }
        }
    }

    public List<Plant> eat(List<Plant> plants) {
        //если он даже немного поел ставлю ему поленое насыщение иначе он вымирает
        List<Plant> needToDelete = new ArrayList<>();
        if (this.isEat == false && this.isReproduce == false && this.isMove == false) {
            int needToEat = 15;
            if (plants.size() > needToEat) {
                for (int i = 0; i < needToEat; i++) {
                    needToDelete.add(plants.get(i));
                    Plant.deathPlants++;
                }
            }
//            while (iterator.hasNext()){
//                iterator.next();
//                iterator.remove();
//                Plant.deathPlants++;
//                needToEat--;
//                if(needToEat ==0)break;
//            }
            this.satiety = Properties.SATIETY_SHEEP;
            this.stepDeath = 0;
            this.isEat = true;
        } else {
            this.isEat = false;
            this.satiety -= Properties.SATIETY_STEP_TO_DEATH_SHEEP;
            if (this.satiety < 0) stepDeath++;
        }
        return needToDelete;
    }

    @Override
    public ArrayList<Sheep> reproduce(Cell[][] cells, int i, int j) {
        ArrayList<Sheep> newSheep = new ArrayList<>();
        int randomLengthSheep = ThreadLocalRandom.current().nextInt(2);
        if (this.isReproduce == false && this.isEat == false && this.isMove == false) {
            for (int k = 0; k < randomLengthSheep; k++) {
                newSheep.add(new Sheep());
                Sheep.newSheep++;
            }
            this.isReproduce = true;
        } else {
            this.isReproduce = false;
        }
        this.satiety -= Properties.SATIETY_STEP_TO_DEATH_SHEEP;
        if (this.satiety < 0) stepDeath++;
        return newSheep;
    }

    @Override
    public Sheep move(Cell[][] cells, int i, int j) {
        if (this.isEat == false && this.isReproduce == false && this.isMove == false) {
            int randomStepLength = ThreadLocalRandom.current().nextInt(4);
            int randomWay = ThreadLocalRandom.current().nextInt(4);
            this.isMove = true;
            if (randomWay == 0) {
                //west(left)
                int iStepToLeft = j - randomStepLength;
                if (iStepToLeft >= 0) {
                    this.satiety -= Properties.SATIETY_STEP_TO_DEATH_SHEEP;
                    if (this.satiety < 0) stepDeath++;
                    ArrayList<Sheep> MoveToOtherSheep = cells[i][iStepToLeft].getSheep();
                    MoveToOtherSheep.add(this);
                    return this;
                } else {
                    Sheep.deathSheep++;
                    return this;
                }
            } else if (randomWay == 1) {
                //north(up)
                int iStepToUp = i - randomStepLength;
                if (iStepToUp >= 0) {
                    this.satiety -= Properties.SATIETY_STEP_TO_DEATH_SHEEP;
                    if (this.satiety < 0) stepDeath++;
                    ArrayList<Sheep> MoveToOtherSheep = cells[iStepToUp][j].getSheep();
                    MoveToOtherSheep.add(this);
                    return this;
                } else {
                    Sheep.deathSheep++;
                    return this;
                }
            } else if (randomWay == 2) {
                //east(right)
                int iStepToRight = j + randomStepLength;
                if (iStepToRight < cells[0].length) {
                    this.satiety -= Properties.SATIETY_STEP_TO_DEATH_SHEEP;
                    if (this.satiety < 0) stepDeath++;
                    ArrayList<Sheep> MoveToOtherSheep = cells[i][iStepToRight].getSheep();
                    MoveToOtherSheep.add(this);
                    return this;
                } else {
                    Sheep.deathSheep++;
                    return this;
                }
            } else if (randomWay == 3) {
                //south(down)
                int iStepToDown = i + randomStepLength;
                if (iStepToDown < cells.length) {
                    this.satiety -= Properties.SATIETY_STEP_TO_DEATH_SHEEP;
                    if (this.satiety < 0) stepDeath++;
                    ArrayList<Sheep> MoveToOtherSheep = cells[iStepToDown][j].getSheep();
                    MoveToOtherSheep.add(this);
                    return this;
                } else {
                    Sheep.deathSheep++;
                    return this;
                }
            }
        } else {
            this.isMove = false;
        }
        return null;
    }

    @Override
    public void eat() {

    }
}
