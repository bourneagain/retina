package retina;

/**
 * Created by saikat on 3/24/15.
 */

public class AppMetadata {
    String appid = "";
    String appversion = "";
    String appname = "";
    AppMetadata(String id, String version, String name) {
        this.appid = id;
        this.appversion = version;
        this.appname = name;
    }
}
