package comm.model;

/**
 * Created by Alexander on 24/10/2017.
 */

public class User {
    private Integer id;
    private String name;

    private String currentMode;

    public static final String MODE_PASSENGER = "passenger";
    public static final String MODE_DRIVER = "driver";

    public User(int id, String name) {
        this.id = id;
        this.name = name;

        currentMode = MODE_PASSENGER;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public void switchModeUser() {
        if(currentMode == MODE_PASSENGER) {
            currentMode = MODE_DRIVER;
        }
        else {
            currentMode = MODE_PASSENGER;
        }
    }

}

