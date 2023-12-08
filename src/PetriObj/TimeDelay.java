/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PetriObj;

public class TimeDelay {
    private double timeOut;
    private double actualDelayTime;
    
    public TimeDelay(double timeOut, double actualDelayTime){
        this.timeOut = timeOut;
        this.actualDelayTime = actualDelayTime;
    }
    
    public double getTimeOut(){
        return timeOut;
    }
    
    public double getActualDelayTime(){
        return actualDelayTime;
    }   
}
