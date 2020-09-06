import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {

    private DecimalFormat df = new DecimalFormat("#0.00");
    private Random r = new Random();

    private static ArrayList<Actor> actors;
    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 800;

    @Override
    public void init() {
        actors = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            actors.add(new Actor(r.nextInt(WINDOW_WIDTH) - WINDOW_WIDTH / 4.0, r.nextInt(WINDOW_HEIGHT) + WINDOW_HEIGHT / 4.0, i, WINDOW_WIDTH, WINDOW_HEIGHT));
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        for (Actor a : actors) {
            root.getChildren().add(a.getIcon());
        }

        new AnimationTimer(){
            @Override
            public void handle(long l) {
                for (Actor a : actors) {
                    a.update();
                }
            }
        }.start();

        stage.setScene(scene);
        stage.setTitle("Flocking Models");
        stage.show();
    }

    public static ArrayList<Actor> getActors() {
        return actors;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
