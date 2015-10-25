package example.srujan.com.drinkwater;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Srujan on 20-09-2015.
 */
public class DailyDetails implements Serializable {
    Calendar calendar;
    int min;
    int volume;
    int target;
}
