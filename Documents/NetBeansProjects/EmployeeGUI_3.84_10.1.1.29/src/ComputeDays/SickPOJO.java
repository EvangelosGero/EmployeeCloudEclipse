/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ComputeDays;

import java.time.LocalDate;

/**
 *
 * @author evgero
 */
public class SickPOJO {
    private LocalDate start;
    private LocalDate end;
    private int realDays;
    private int IKADays;

    public SickPOJO() {
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public int getRealDays() {
        return realDays;
    }

    public void setRealDays(int realDays) {
        this.realDays = realDays;
    }

    public int getIKADays() {
        return IKADays;
    }

    public void setIKADays(int IKADays) {
        this.IKADays = IKADays;
    }    
}
