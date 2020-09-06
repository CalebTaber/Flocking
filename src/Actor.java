import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class Actor {

    private ImageView icon;
    private double heading, speed;
    private int ID;

    private ArrayList<Actor> neighborhood;
    private final int NEIGHBORHOOD_SIZE = 7;
    private int maxX, maxY;

    private Label label;

    private DecimalFormat df = new DecimalFormat("#0.00");
    private Random r = new Random();

    public Actor(double x, double y, int ID, int maxX, int maxY) {
        icon = new ImageView(getClass().getResource("Icon.png").toExternalForm());
        this.ID = ID;
        this.maxX = maxX;
        this.maxY = maxY;

        heading = r.nextInt(360);
        speed = 0.1;
        neighborhood = new ArrayList<>();

        icon.setTranslateX(x);
        icon.setTranslateY(y);
        icon.setRotate(90 - heading);

        // Temporary
        label = new Label(String.valueOf(ID));
        label.setLayoutX(x);
        label.setLayoutY(y);
        // label.setRotate(heading);
        label.setTextFill(Color.RED);

        heading *= Math.PI / 180;
    }

    public void update() {
        // Find the distance between this and each neighbor (if the neighborhood is larger than it should be) and remove the farthest neighbors
        neighborhood.clear();
        PriorityQueue<Actor> rankedNeighbors = new PriorityQueue<>(new Comparator<Actor>() {
            @Override
            public int compare(Actor a1, Actor a2) {
                double distance1 = Math.sqrt(Math.pow(a1.getX() - getX(), 2) + Math.pow(a1.getY() - getY(), 2));
                double distance2 = Math.sqrt(Math.pow(a2.getX() - getX(), 2) + Math.pow(a2.getY() - getY(), 2));
                return Double.compare(distance1, distance2);
            }
        });
        rankedNeighbors.addAll(Main.getActors());

        // Add only the necessary members of the neighborhood
        while (neighborhood.size() < NEIGHBORHOOD_SIZE) {
            if (rankedNeighbors.peek() == this) rankedNeighbors.poll();
            neighborhood.add(rankedNeighbors.poll());
        }

        /*
        String neighbors = "Neighbors of " + ID;
        for (Actor n : neighborhood) {
            neighbors += "\n" + n.getID();
        }
        System.out.println(neighbors);
         */

        // Get neighborhood average heading and position
        double neighborsHeading = 0;
        double neighborsX = 0;
        double neighborsY = 0;
        for (Actor a : neighborhood) {
            neighborsHeading += a.getHeading();
            neighborsX += a.getX();
            neighborsY += a.getY();
        }
        neighborsHeading /= NEIGHBORHOOD_SIZE;
        neighborsX /= NEIGHBORHOOD_SIZE;
        neighborsY /= NEIGHBORHOOD_SIZE;

        final double STEER_AMT = 0.01;
        final double STEER_AVG_HEADING_WEIGHT = 5;
        final double STEER_AVG_POS_WEIGHT = 5;

        // Steer toward average heading
        if (heading < neighborsHeading) heading += STEER_AMT * STEER_AVG_HEADING_WEIGHT;
        else if (heading > neighborsHeading) heading -= STEER_AMT * STEER_AVG_HEADING_WEIGHT;

        // Steer toward average position
        double posHeading = (neighborsY - getY()) / Math.sqrt(Math.pow(neighborsX - getX(), 2) + Math.pow(neighborsY - getY(), 2));
        if (heading < posHeading) heading += STEER_AMT * STEER_AVG_POS_WEIGHT;
        else if (heading > posHeading) heading -= STEER_AMT * STEER_AVG_POS_WEIGHT;

        // Have to make this adjustment because the way things are rotated in JavaFX is different from regular trigonometry
        // JavaFX starts rotations from the y-axis, going clockwise, but trig starts from the x-axis, going counter-clockwise
        icon.setRotate(90 - (heading / Math.PI * 180));

        // TODO Avoid boundaries and obstacles
        // Square the weight of the turn with the distance from the barrier
        // How to find the obstacles in front of the boid?

        // Move
        double dx = Math.cos(heading) * speed;
        double dy = Math.sin(heading) * speed;
        TranslateTransition move = new TranslateTransition(Duration.millis(1), icon);
        move.setByX(dx);
        move.setByY(-dy);

        // TODO Figure out correct algorithm to steer away more as the boid gets closer to the boundary
        if (dx > 0) {
            double r = icon.getRotate();
            if (r > 0 && r <= 90) {
                heading -= STEER_AMT  / Math.pow(getX() - maxX, 2);
            } else if (r > 90 && r < 180) {
                heading += STEER_AMT / Math.pow(getX() - maxX, 2);
            }
        }

        heading %= 6.28;

        move.play();
    }

    public double getX() {
        return icon.getTranslateX();
    }

    public double getY() {
        return icon.getTranslateY();
    }

    public double getHeading() {
        return heading;
    }

    public int getID() {
        return ID;
    }

    public String toString() {
        return String.valueOf(ID);
    }

    public ImageView getIcon() {
        return icon;
    }

    public Label getIDLabel() {
        return label;
    }

}
