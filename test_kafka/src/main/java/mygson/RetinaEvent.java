package mygson;

/**
 * Created by saikat on 3/24/15.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetinaEvent {
    // final JSON fields
    public String timestamp;
    public String phoneimei;
    public String appid;
    public String eventid;
    public String phoneversion;
    public String phonemodel;
    public String phonebaseband;
    public String phonebuild;
    public String appversion;
    public String eventtype;
    public String eventwarn;
    public String eventerror;
    public String eventcrash;
    public String eventclick;

    RetinaEvent(PhoneMetadata p, AppMetadata a) {
        this.phoneimei = p.phoneImei;
        this.appid = a.appid;
        this.phoneversion = p.phoneVersion;
        this.phonemodel = p.phoneModel;
        this.phonebaseband = p.phoneBaseband;
        this.phonebuild = p.phoneBuild;
        this.appversion = a.appversion;
    }

    RetinaEvent(PhoneMetadata p, AppMetadata a, Event e) {
        //this.timestamp
        this.phoneimei = p.phoneImei;
        this.appid = a.appid;
        this.phoneversion = p.phoneVersion;
        this.phonemodel = p.phoneModel;
        this.phonebaseband = p.phoneBaseband;
        this.phonebuild = p.phoneBuild;
        this.appversion = a.appversion;
        this.eventtype = e.eventType;
        this.eventwarn = e.eventWarn;
        this.eventerror = e.eventError;
        this.eventcrash = e.eventCrash;
        this.eventclick = e.eventClick;
    }

    String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

}
