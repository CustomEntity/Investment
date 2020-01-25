package fr.customentity.investment.exceptions;

/**
 * Created by CustomEntity on 1/24/2020 for [SpigotMc] Investment.
 */
public class WorldDoesntExistException extends Exception {

    public WorldDoesntExistException() {
        super("The selected world does not exist");
    }
}
