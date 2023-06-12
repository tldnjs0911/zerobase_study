package zerobase_study;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomELFunctions {
    public static String getCurrentDateTime() {
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(currentDate);
    }
}