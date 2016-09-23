package hoz.ceph.staffspy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ceph on 22.09.2016.
 */

public class DateUtils {

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date rawDate = new Date();
        return dateFormat.format(rawDate);
    }
}
