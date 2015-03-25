package retina;

/**
 * Created by saikat on 3/24/15.
 */
class PhoneMetaData {
    String phoneImei = "";
    public String phoneVersion = "";
    public String phoneModel = "";
    public String phoneBaseband = "";
    public String phoneBuild = "";

    PhoneMetaData(String imei, String version, String model, String baseband, String build) {
        this.phoneImei = imei;
        this.phoneVersion = version;
        this.phoneBaseband = baseband;
        this.phoneBuild = build;
        this.phoneModel =  model;
    }
}