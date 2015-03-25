package retina;

/**
 * Created by saikat on 3/24/15.
 */
public class Event {
    String appId = "";
    String phoneId = "";
    String eventType = "";
    String eventWarn = "";
    String eventError = "";
    String eventCrash = "";
    String eventClick = "";

    Event() {}

    Event(String type,
          String warn,
          String error,
          String crash,
          String click,
          String app,
          String phone) {
        this.appId = app;
        this.phoneId = phone;
        this.eventType = type;
        this.eventWarn = warn;
        this.eventError = error;
        this.eventCrash = crash;
        this.eventClick = click;
    }

    Event(String type, String warn, String error, String crash, String app, String phone) {
        this.appId = app;
        this.phoneId = phone;
        this.eventType = type;
        this.eventWarn = warn;
        this.eventError = error;
        this.eventCrash = crash;
    }

    Event(String type, String log, String app, String phone) {
        this.appId = app;
        this.phoneId = phone;
        this.eventType = type;
        if (type.equalsIgnoreCase("HeartBeat")) {
            // nothing to do
        } else if (type.equalsIgnoreCase("Crash")) {
            this.eventCrash = log;
        } else if (type.equalsIgnoreCase("Error")) {
            this.eventError = log;
        } else if (type.equalsIgnoreCase("Warn")) {
            this.eventWarn = log;
        } else if (type.equalsIgnoreCase("Click")) {
            this.eventClick = log;
        }
    }
}
