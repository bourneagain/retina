package mygson; /**
 * Created by saikat on 3/24/15.
 */


import java.text.SimpleDateFormat;
import java.util.Date;


public class json_parser {

    static String generateTimeStamp() {
         String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date());
         String result = timeStamp.substring(0, 10) + "T" + timeStamp.substring(11) + 'Z';
         return result;
     }

    static Integer eventID = 0;

    static String getEventID() {
        eventID++;
        return Integer.toString(eventID);
    }

//    public static void main(String[] args) {
//        PhoneMetadata pm = new PhoneMetadata("1234", "Nexus5", "M8974", "LRX22C");
//        AppMetadata a = new AppMetadata("app1234", "1.0");
//        Event e =  new Event("Click", "type1:3 type2:4");
//        RetinaEvent re = new RetinaEvent(pm, a, e);
//        re.timestamp = generateTimeStamp();
//        re.eventid = getEventID();
//        System.out.println(re.toJSON());
//    }
}
