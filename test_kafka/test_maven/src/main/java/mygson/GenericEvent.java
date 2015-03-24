package mygson;

/**
 * Created by saikat on 3/24/15.
 */
public class GenericEvent {
    String type;
    String rawLog;
    GenericEvent(String type, String log) {
        this.type = type;
        this.rawLog = log;
    }
}
