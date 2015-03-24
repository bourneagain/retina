package mygson;

import jdk.nashorn.internal.runtime.OptimisticReturnFilters;

/**
 * Created by saikat on 3/24/15.
 */
public class Event {
    String eventType;
    String eventWarn;
    String eventError;
    String eventCrash;
    String eventClick;
    Event(String type, String warn, String error, String crash, String click) {
        this.eventType = type;
        this.eventWarn = warn;
        this.eventError = error;
        this.eventCrash = crash;
        this.eventClick = click;
    }

    Event(String type, String warn, String error, String crash) {
        this.eventType = type;
        this.eventWarn = warn;
        this.eventError = error;
        this.eventCrash = crash;
    }

    Event(String type, String click) {
        this.eventType = type;
        this.eventClick = click;
    }
}
