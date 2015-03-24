package mygson;

/**
 * Created by saikat on 3/24/15.
 */
class PhoneMetadata {
    String phoneImei;
    public String phoneVersion;
    public String phoneModel;
    public String phoneBaseband;
    public String phoneBuild;

    PhoneMetadata(String imei, String version, String basebnd, String build) {
        this.phoneImei = imei;
        this.phoneVersion = version;
        this.phoneBaseband = basebnd;
        this.phoneBuild = build;
    }
}