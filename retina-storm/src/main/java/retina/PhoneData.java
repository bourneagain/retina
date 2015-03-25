package retina;

/**
 * Created by saikat on 3/24/15.
 */
class PhoneData {
    String phonetimestamp = "";
    String eventid = "";
    String eventtype = "";
    String phoneimei = "";
    String appid = "";
    String logs = "";

    PhoneData(String time, String eid, String etype, String imei, String appid, String logs) {
        this.phonetimestamp = time;
        this.eventid = eid;
        this.eventtype = etype;
        this.phoneimei = imei;
        this.appid = appid;
        this.logs = logs;
    }

}
