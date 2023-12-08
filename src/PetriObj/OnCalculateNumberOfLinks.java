/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PetriObj;

import static java.lang.Math.round;

public  class OnCalculateNumberOfLinks {
    private int arcNumber;
    private int pricePerKilometer;
    private double minSpeed;
    private double maxSpeed;
    
    public OnCalculateNumberOfLinks(int arcNumber, int pricePerKilometer, double minSpeed, double maxSpeed){
        this.arcNumber = arcNumber;
        this.pricePerKilometer = pricePerKilometer;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }
    
    public int getNumberOfLinks(double timeTaken) throws ExceptionInvalidTimeDelay{
        double randomSpeed = FunRand.unif(minSpeed, maxSpeed);
        return (int) round(pricePerKilometer * randomSpeed * (timeTaken / 60));
    }
    
    public int getArcNumber(){
        return arcNumber;
    }
}
