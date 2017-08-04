/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import com.dynamotors.timer1._rest.Timer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 *
 * @author e
 */
public class TimerComparator {
    
    private static DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static final Comparator<Timer> DATE_ORDER = 
            new Comparator<Timer>(){
                @Override
                public int compare(Timer t1, Timer t2){
                    return LocalDate.parse(t1.getStarttime().toString().substring(0, 10), formatter).
                            compareTo(LocalDate.parse(t2.getStarttime().toString().substring(0, 10), formatter));
                }
            };
    
}

