import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is in charge of adding the snake's tails to the world and
 * ensuring it meets the behaviour of the snake.
 * 
 * @author Maor Gornik, Luke Liu, Qirong Su, Rahim Somjee 
 * @version June 9, 2019
 */
public class SnakeTail extends Actor
{
    // Holding the number of frames that elapsed
    private int framesElapsed = 0;

    // Holding the number of frames that the snake's body part is alive for
    private static int lifeCycle;

    // Determining when the snake's turning body part's image can be changed
    private boolean canBeSwitched;
    
    // Storing a reference to the world
    SnakeWorld currentWorld;

    // Creating a list to hold all the SnakeTail instances in the world
    ArrayList<SnakeTail> tailList = new ArrayList<>(); 

    // Creating a list to hold all the tail instances' elapsed frames
    ArrayList<Integer> tailFrames = new ArrayList<>();

    // Creating an object to store the snake's last body part (tail)
    SnakeTail lastTail;

    /**
     * A constructor for the snake's tails
     */
    public SnakeTail() {
        GreenfootImage tail = new GreenfootImage("snake/body.png");
        setImage(tail); //Set the image of the snake tail
    }

    /**
     * Finding the snake's last tail and changing its image 
     */
    private void changeLastTail() {
        // Populating tailFrames with all the SnakeTail instances' number
        // of elapsed frames
        for(SnakeTail tail : getWorld().getObjects(SnakeTail.class)) {
            tailFrames.add(tail.getTailFrames());
        }

        // Sorting the list (ascending order)
        Collections.sort(tailFrames);

        // Retrieving a not sorted list containing all the snake's tails
        addToList(false);

        // Looping through all the SnakeTail instances in the world
        for(int i=0; i<tailList.size(); i++) {
            // Locating the last tail in the list (the tail)
            if(tailList.get(i).getTailFrames() == tailFrames.get(tailFrames.size() - 1)) {
                // Storing the last tail instance in a variable
                lastTail = tailList.get(i);

                // Changing its image to a tail
                GreenfootImage tail = new GreenfootImage("snake/tail.png");
                lastTail.setImage(tail);

                // Determining the tail's direction and flipping it accordingly
                SnakeTail tempHead = null;

                for(int j=0;j<getWorld().getObjects(SnakeTail.class).size();j++) {
                    if(tailFrames.size() > 1 && getWorld().getObjects(SnakeTail.class).get(j).getTailFrames() == tailFrames.get(tailFrames.size() - 2)) {
                        tempHead = getWorld().getObjects(SnakeTail.class).get(j);
                    }
                }

                if(tempHead != null && tempHead.getRotation() == 0) {
                    lastTail.setRotation(0);
                } else if(tempHead != null && tempHead.getRotation() == 90) {
                    lastTail.setRotation(90);
                } else if(tempHead != null && tempHead.getRotation() == 180) {
                    lastTail.setRotation(180);
                } else if(tempHead != null && tempHead.getRotation() == 270) {
                    lastTail.setRotation(270);
                }
            }
        }

    }

    /**
     * Setter for the life duration of the snake body parts
     * 
     * @duration The number of frames for the body parts to last
     */
    public static void setLifeDuration(int duration) {
        lifeCycle = duration;
    }

    /**
     * Getter for the life duration of the snake body parts
     * 
     * @return The number of frames that the snake's body parts last
     */
    public static int getLifeDuration() {
        return lifeCycle;
    }

    /**
     * Getter for the number of elapsed frames of a specific SnakeTail instance
     * 
     * @return The number of elapsed frames for a specific instance
     */
    public int getTailFrames() {
        return framesElapsed;
    }
    
    /**
     * Resets the game
     * 
     * @currentWorld The world where this method is being called from
     */
    public static void reset(World currentWorld) {
        lifeCycle = 25;
        currentWorld.removeObjects(currentWorld.getObjects(SnakeTail.class));
    }

   /**
     * Sorting the snake's tails based on the number of frames they have
     * been alive
     * 
     * @toSort Sorting the list containing all the snake tail instances based
     * on their on their elapsed number of frames
     */
    private void addToList(boolean toSort) {
        // Clearing the tailList to prevent slow performance
        tailList.clear();
        
        // Adding all the SnakeTail instances to a list
        tailList.addAll(getWorld().getObjects(SnakeTail.class));

        if(toSort) {
            // Sorting tail based on the number of frames they have lived for
            for (int i = 1; i < tailList.size(); i++) {
                for (int j = i; j > 0 && tailList.get(j - 1).getTailFrames() >
                tailList.get(j).getTailFrames(); j--) {
                    Collections.swap(tailList, j, j - 1);
                }
            }
        }
    }

    /**
     * Determining whether the snake is turning
     * 
     * @return true if the snake is turning, false if not.
     */
    private boolean isSnakeTurning() {
        // Clearing the tailList to prevent slow performance
        tailList.clear();
        
        // Retrieving a not sorted list containing all the snake's tails
        addToList(false);

        // Creating an object to store the snake's head
        SnakeHead tempHead = getWorld().getObjects(SnakeHead.class).get(0);

        // Finding whether the rotation of the head of the snake and its
        // tails differ
        for(int i = 0; i<tailList.size();i++) {

            if(tempHead.getRotation() != tailList.get(i).getRotation()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Changing the selected snake's body part and changing its pictures
     * to reflect the turn the snake is taking
     * 
     * @twistedPart The snake's turning body part
     * @tempHead The snake's head object
     */
    private void twistImage(SnakeTail twistedPart, SnakeHead tempHead) {
        // Initializing an image to hold the snake's turning body part image
        GreenfootImage tempImg = twistedPart.getImage();

        // Calculating the difference in rotation between the snake's head and its
        // turning body part
        int rotationDiff = tempHead.getRotation() - twistedPart.getRotation();

        // Determining in what direction the snake is headed
        if(rotationDiff == -90 || rotationDiff == 270) {
            tempImg = new GreenfootImage("snake/body-twist-flipped.png");
        } else {
            tempImg = new GreenfootImage("snake/body-twist.png");
        }

        // Updating the image
        twistedPart.setImage(tempImg);
    }

    /**
     * Changing the connecting body part of the snake to reflect the
     * turn it is taking
     */
    private void twistBody() {
        // Retrieving a sorted list containing all the snake's tails based on the
        // amount of frames they have been alive for
        addToList(true);
        
        // Creating an object to store the snake's head
        SnakeHead tempHead = getWorld().getObjects(SnakeHead.class).get(0);
        
        // Storing the first snake body part (the one right after the snake's head)
        SnakeTail tempTail = tailList.get(0);

        // Checking whether the snake's head rotation is the same as its tail
        if(tempHead.getRotation() != tempTail.getRotation()) {

            // Limiting execution to every 12 seconds
            if((((SnakeWorld)getWorld()).getGameFrames() % 5) == 0) {

                // Checking whether the head and the turning body part are not aligned over the
                // Y axis and are going either left or right
                if(tempHead.getRotation() == 0 || tempHead.getRotation() == 180) {
                    if(tempHead.getX() != tempTail.getX()) {
                        // The snake's turning body part's image can be changed 
                        canBeSwitched = true;
                    }

                    // Checking whether the head and the turning body part are not aligned over the
                    // X axis and are going either up or down 
                } else {
                    if(tempHead.getY() != tempTail.getY()) {
                        // The snake's turning body part's image can be changed 
                        canBeSwitched = true;
                    } 
                }

                // If the snake's body part and its head are not aligned vertically or horizonally
                // changing the connecting body part image to a turning body part image
                // to reflect that turn the snake it taking
                if(canBeSwitched) {
                    // Changing the turning object's image
                    twistImage(tempTail, tempHead); 
                    canBeSwitched = false;
                }
            }
        }
    }

    /**
     * Act - do whatever the SnakeTail wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        // Getting the current world object
        currentWorld = (SnakeWorld) getWorld();
        
        // Incrementing the number of elapsed frames each execution
        framesElapsed++;   

        // Changing the picture of the last snake's body part to a tail
        changeLastTail();
        
        // Changing the picture of the turning snake's body part
        if(isSnakeTurning()) twistBody();

	// Removing the snake's body part when it reaches its maximum life duration
        if(framesElapsed >= lifeCycle) {
            getWorld().removeObject(this);

            // Also removing it from the tail list
            if(tailList.size() > 0) {
                for(int i=0; i < tailList.size(); i++) {
                    if(tailList.get(i).equals(this)) {
                        currentWorld.removeObject(this);
                    }
                }
            }
        }
    }      
}