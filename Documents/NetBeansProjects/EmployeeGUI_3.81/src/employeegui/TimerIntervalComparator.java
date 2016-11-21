/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;

/**
 *
 * @author e
 */
public class TimerIntervalComparator {
    
    private static DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    static final Comparator<TimerInterval> DATE_ORDER = 
            new Comparator<TimerInterval>(){
                @Override
                public int compare(TimerInterval t1, TimerInterval t2){
                    return LocalDate.parse(t1.getStartTime().substring(0, 10), formatter).
                            compareTo(LocalDate.parse(t2.getStartTime().substring(0, 10), formatter));
                }
            };
    
}
