package LibTest;

import PetriObj.ArcIn;
import PetriObj.ArcOut;
import PetriObj.ExceptionInvalidNetStructure;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.FunRand;
import PetriObj.OnCalculateNumberOfLinks;
import PetriObj.PetriNet;
import PetriObj.PetriObjModel;
import PetriObj.PetriP;
import PetriObj.PetriSim;
import PetriObj.PetriT;
import static java.lang.Math.round;
import java.util.ArrayList;

/**
 *
 * @author ihorklimov
 */
public class CourseWork {

    private static final int PRICE_PER_KILOMETER = 3;
    private static final double MIN_SPEED = 30.0;
    private static final double MAX_SPEED = 40.0;
    private static final int TIME_MODELING = 1440 * 2;
    private static final int STARTING_CAPITAL = 0;
    private static final int EMPLOYEE_SALARY = 1000;
    private static final int NUMBER_OF_CARS = 10;
    private static final int NUMBER_OF_CHANNELS = 5;
    private static final int PRICE_OF_PREORDER = 20;
    private static final int WORKDAY_LENGTH_IN_MINUTES = 1440;

    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        // цей фрагмент для запуску імітації моделі з заданною мережею Петрі на інтервалі часу timeModeling  

//        for (int numberOfEmployees = 1; numberOfEmployees <= 15; numberOfEmployees++) {
            int numberOfEmployees = 15;
            PetriObjModel model = getModel(numberOfEmployees);
            model.setIsProtokol(false);
            model.go(TIME_MODELING);

            // Statistics 
            PetriNet net = model.getListObj().get(0).getNet();

//            System.out.println("--------");
//
//            System.out.println("Printing points");
//            for (int i = 0; i < net.getListP().length; i++) {
//                PetriP p = net.getListP()[i];
//                if (!p.getName().startsWith("P")) {
//                    System.out.println(p.getName() + " value = " + p.getMark() + " min = " + p.getObservedMin() + " max = " + p.getObservedMax() + " mean = " + p.getMean());
//                }
//            }
//
//            System.out.println("");
//            System.out.println("Printing transitions");
//            for (int i = 0; i < net.getListT().length; i++) {
//                PetriT t = net.getListT()[i];
//                System.out.println(t.getName() + " completed = " + t.getNumOfCompleted() + " mean = " + t.getMean() + " mean run time: " + t.getMeanRunTime());
//            }
//
//            System.out.println("");
            int resultCapital = 0;
            int unpaidSalaries = 0;
            double meanTripTime = 0;
            int numOfCalls = 0;

            for (int i = 0; i < net.getListP().length; i++) {
                PetriP p = net.getListP()[i];
                if (p.getName().equals("Капітал")) {
                    resultCapital = p.getMark();
                } else if (p.getName().equals("Неоплачена зп")) {
                    unpaidSalaries = p.getMark();
                } else if (p.getName().equals("Черга")) {
                    System.out.println("Queue time = " + p.getMeanRunTime());
                    meanTripTime += p.getMeanRunTime();
                }
            }

            double totalRunOfTripsToCustomer = 0;

            int numberOfFirstCalls = 0;
            int numberOfSecondCalls = 0;
            int numberOfThirdCalls = 0;
            int numberOfForthCalls = 0;

            for (int i = 0; i < net.getListT().length; i++) {
                PetriT t = net.getListT()[i];
                if (t.getName().equals("Обслуговування")) {
                    meanTripTime += t.getMeanRunTime();
                } else if (t.getName().equals("Доїхати до клієнта, 5км")
                        || t.getName().equals("Доїхати до клієнта, 8км")
                        || t.getName().equals("Доїхати до клієнта, 9км")
                        || t.getName().equals("Доїхати до клієнта, 11км")
                        || t.getName().equals("Доїхати до клієнта, 12км")
                        || t.getName().equals("Доїхати до клієнта, 20км")) {
                    totalRunOfTripsToCustomer += t.getMeanRunTime();
                } else if (t.getName().equals("Набирання номеру 1")) {
                    numberOfFirstCalls = t.getNumOfCompleted();
                } else if (t.getName().equals("Набирання номеру 2")) {
                    numberOfSecondCalls = t.getNumOfCompleted();
                } else if (t.getName().equals("Набирання номеру 3")) {
                    numberOfThirdCalls = t.getNumOfCompleted();
                } else if (t.getName().equals("Набирання номеру 4")) {
                    numberOfForthCalls = t.getNumOfCompleted();
                }
            }

            int numberOf4Attempts = numberOfForthCalls;
            int numberOf3Attempts = numberOfThirdCalls - numberOfForthCalls;
            int numberOf2Attempts = numberOfSecondCalls - numberOfThirdCalls;
            int numberOf1Attempts = numberOfFirstCalls - numberOfSecondCalls;

            if (numberOf4Attempts + numberOf3Attempts + numberOf2Attempts + numberOf1Attempts != numberOfFirstCalls) {
                throw new RuntimeException("Wrong call attempt calculation");
            }

            meanTripTime += totalRunOfTripsToCustomer / 6;

            resultCapital -= unpaidSalaries;
            double averageNumberOfAttempts = (numberOf1Attempts * 1.0
                    + numberOf2Attempts * 2 
                    + numberOf3Attempts * 3
                    + numberOf4Attempts * 4) / (numberOf1Attempts + numberOf2Attempts + numberOf3Attempts + numberOf4Attempts);
                    
            double profitPerDay = resultCapital / (TIME_MODELING / 1440);
            
            System.out.println("Number of employees = " + numberOfEmployees + " capital = " + resultCapital + " profit per day = " + profitPerDay + " mean trip time = " + meanTripTime);
            System.out.println("Number of attempts : 1 = " + numberOf1Attempts + " 2 = " + numberOf2Attempts + " 3 = " + numberOf3Attempts + " 4 = " + numberOf4Attempts + " average num of attempts = "+ averageNumberOfAttempts);
//        }

        System.out.println("Done");
    }

    public static PetriObjModel getModel(int numberOfEmployees) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        ArrayList<PetriSim> list = new ArrayList<>();
        list.add(new PetriSim(createNet(numberOfEmployees)));
        PetriObjModel model = new PetriObjModel(list);
        return model;
    }

    public static PetriNet createNet(int numberOfEmployees) throws ExceptionInvalidNetStructure, ExceptionInvalidTimeDelay {
        ArrayList<PetriP> points = new ArrayList<>();
        ArrayList<PetriT> transitions = new ArrayList<>();
        ArrayList<ArcIn> ins = new ArrayList<>();
        ArrayList<ArcOut> outs = new ArrayList<>();

        points.add(new PetriP("P1", 1));
        points.add(new PetriP("P3", 0));
        points.add(new PetriP("P4", 0));
        points.add(new PetriP("P5", 0));
        points.add(new PetriP("P6", 0));
        points.add(new PetriP("P7", 0));
        points.add(new PetriP("P8", 0));
        points.add(new PetriP("P9", 0));
        points.add(new PetriP("P10", 0));
        points.add(new PetriP("P11", 0));
        points.add(new PetriP("P12", 0));
        points.add(new PetriP("Кількість незадовільнених користувачів", 0));
        points.add(new PetriP("Кількість вільних каналів", NUMBER_OF_CHANNELS));
        points.add(new PetriP("P2", 0));
        points.add(new PetriP("P3", 0));
        points.add(new PetriP("P4", 0));
        points.add(new PetriP("P5", 0));
        points.add(new PetriP("Черга", 0));
        points.add(new PetriP("P1", 0));
        points.add(new PetriP("Кількість виконаних замовлень", 0));
        points.add(new PetriP("Кількість машин", NUMBER_OF_CARS));
        points.add(new PetriP("P1", 0));
        points.add(new PetriP("P2", 0));
        points.add(new PetriP("Кількість вільного персоналу", numberOfEmployees));
        PetriP capital = new PetriP("Капітал", STARTING_CAPITAL);
        points.add(capital);
        points.add(new PetriP("P2", 1));
        points.add(new PetriP("Кількість сплачених зп", 0));
        points.add(new PetriP("P4", 0));
        points.add(new PetriP("Неоплачена зп", 0));

        transitions.add(new PetriT("Виклик таксі", 1.5));
        transitions.get(0).setDistribution("exp", transitions.get(0).getTimeServ());
        transitions.get(0).setParamDeviation(0.0);
        transitions.add(new PetriT("Набирання номеру 1", 0.5));
        transitions.add(new PetriT("Обирання нової спроби", 0.0));
        transitions.add(new PetriT("Нова спроба", 1.0));
        transitions.add(new PetriT("Набирання номеру 2", 0.5));
        transitions.add(new PetriT("Обирання нової спроби", 0.0));
        transitions.add(new PetriT("Нова спроба", 1.0));
        transitions.add(new PetriT("Набирання номеру 3", 0.5));
        transitions.add(new PetriT("Обирання нової спроби", 0.0));
        transitions.add(new PetriT("Нова спроба", 0.0));
        transitions.add(new PetriT("Набирання номеру 4", 0.5));
        transitions.add(new PetriT("Кінець спроб", 0.0));
        transitions.add(new PetriT("З'єднання з каналом", 0.0));
        transitions.get(12).setPriority(1);
        transitions.add(new PetriT("З'єднання з каналом", 0.0));
        transitions.get(13).setPriority(1);
        transitions.add(new PetriT("З'єднання з каналом", 0.0));
        transitions.get(14).setPriority(1);
        transitions.add(new PetriT("З'єднання з каналом", 0.0));
        transitions.get(15).setPriority(1);
        transitions.add(new PetriT("Черга дуже велика", 0.0));
        transitions.get(16).setPriority(1);
        transitions.add(new PetriT("Черга дуже велика", 0.0));
        transitions.get(17).setPriority(1);
        transitions.add(new PetriT("Черга дуже велика", 0.0));
        transitions.get(18).setPriority(1);
        transitions.add(new PetriT("Черга дуже велика", 0.0));
        transitions.get(19).setPriority(1);
        transitions.add(new PetriT("Початок замовлення", 0.0));
        transitions.add(new PetriT("Початок замовлення", 0.0));
        transitions.add(new PetriT("Початок замовлення", 0.0));
        transitions.add(new PetriT("Початок замовлення", 0.0));
        transitions.add(new PetriT("Доїхати до клієнта, 9км", 15.75));
        transitions.get(24).setDistribution("unif", transitions.get(24).getTimeServ());
        transitions.get(24).setParamDeviation(2.25);
        transitions.get(24).setProbability(0.25);
        transitions.add(new PetriT("Обслуговування", 40.0));
        transitions.get(25).setOnCalculateNumberOfLinks(
                new OnCalculateNumberOfLinks(
                        capital.getNumber(),
                        PRICE_PER_KILOMETER,
                        MIN_SPEED,
                        MAX_SPEED));
        transitions.get(25).setDistribution("unif", transitions.get(25).getTimeServ());
        transitions.get(25).setParamDeviation(10.0);
        transitions.add(new PetriT("Доїхати до клієнта, 11км", 19.25));
        transitions.get(26).setDistribution("unif", transitions.get(26).getTimeServ());
        transitions.get(26).setParamDeviation(2.75);
        transitions.get(26).setProbability(0.17);
        transitions.add(new PetriT("Доїхати до клієнта, 8км", 14.0));
        transitions.get(27).setDistribution("unif", transitions.get(27).getTimeServ());
        transitions.get(27).setParamDeviation(2.0);
        transitions.get(27).setProbability(0.2);
        transitions.add(new PetriT("Доїхати до клієнта, 12км", 21.0));
        transitions.get(28).setDistribution("unif", transitions.get(28).getTimeServ());
        transitions.get(28).setParamDeviation(3.0);
        transitions.get(28).setProbability(0.23);
        transitions.add(new PetriT("Доїхати до кілєнта, 5км", 8.75));
        transitions.get(29).setDistribution("unif", transitions.get(29).getTimeServ());
        transitions.get(29).setParamDeviation(1.25);
        transitions.get(29).setProbability(0.1);
        transitions.add(new PetriT("Доїхати до клієнта, 20км", 35.0));
        transitions.get(30).setDistribution("unif", transitions.get(30).getTimeServ());
        transitions.get(30).setParamDeviation(5.0);
        transitions.get(30).setProbability(0.05);
        transitions.add(new PetriT("Виклик таксі затримка 2", 1.5));
        transitions.get(31).setDistribution("exp", transitions.get(31).getTimeServ());
        transitions.get(31).setParamDeviation(0.0);
        transitions.add(new PetriT("Робоча доба", WORKDAY_LENGTH_IN_MINUTES));
        transitions.add(new PetriT("Сплатити зп", 0.0));
        transitions.get(33).setPriority(1);
        transitions.add(new PetriT("Зберігання неоплаченої зп", 0.0));

        ins.add(new ArcIn(points.get(10), transitions.get(11), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(24), 1));
        ins.add(new ArcIn(points.get(20), transitions.get(24), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(24), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(26), 1));
        ins.add(new ArcIn(points.get(20), transitions.get(26), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(26), 1));
        ins.add(new ArcIn(points.get(2), transitions.get(3), 1));
        ins.add(new ArcIn(points.get(27), transitions.get(33), 1));
        ins.add(new ArcIn(points.get(24), transitions.get(33), numberOfEmployees * EMPLOYEE_SALARY));
        ins.add(new ArcIn(points.get(22), transitions.get(1), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(28), 1));
        ins.add(new ArcIn(points.get(20), transitions.get(28), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(28), 1));
        ins.add(new ArcIn(points.get(12), transitions.get(14), 1));
        ins.add(new ArcIn(points.get(7), transitions.get(14), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(14), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(27), 1));
        ins.add(new ArcIn(points.get(20), transitions.get(27), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(27), 1));
        ins.add(new ArcIn(points.get(3), transitions.get(4), 1));
        ins.add(new ArcIn(points.get(12), transitions.get(15), 1));
        ins.add(new ArcIn(points.get(10), transitions.get(15), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(15), 1));
        ins.add(new ArcIn(points.get(15), transitions.get(18), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(18), 11));
        ins.get(25).setInf(true);
        ins.add(new ArcIn(points.get(1), transitions.get(2), 1));
        ins.add(new ArcIn(points.get(21), transitions.get(31), 1));
        ins.add(new ArcIn(points.get(16), transitions.get(23), 1));
        ins.add(new ArcIn(points.get(6), transitions.get(7), 1));
        ins.add(new ArcIn(points.get(15), transitions.get(22), 1));
        ins.add(new ArcIn(points.get(14), transitions.get(17), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(17), 11));
        ins.get(32).setInf(true);
        ins.add(new ArcIn(points.get(16), transitions.get(19), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(19), 11));
        ins.get(34).setInf(true);
        ins.add(new ArcIn(points.get(13), transitions.get(20), 1));
        ins.add(new ArcIn(points.get(12), transitions.get(12), 1));
        ins.add(new ArcIn(points.get(1), transitions.get(12), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(12), 1));
        ins.add(new ArcIn(points.get(7), transitions.get(8), 1));
        ins.add(new ArcIn(points.get(9), transitions.get(10), 1));
        ins.add(new ArcIn(points.get(4), transitions.get(5), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(29), 1));
        ins.add(new ArcIn(points.get(20), transitions.get(29), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(29), 1));
        ins.add(new ArcIn(points.get(25), transitions.get(32), 1));
        ins.add(new ArcIn(points.get(5), transitions.get(6), 1));
        ins.add(new ArcIn(points.get(12), transitions.get(13), 1));
        ins.add(new ArcIn(points.get(4), transitions.get(13), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(13), 1));
        ins.add(new ArcIn(points.get(13), transitions.get(16), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(16), 11));
        ins.get(51).setInf(true);
        ins.add(new ArcIn(points.get(14), transitions.get(21), 1));
        ins.add(new ArcIn(points.get(18), transitions.get(25), 1));
        ins.add(new ArcIn(points.get(17), transitions.get(30), 1));
        ins.add(new ArcIn(points.get(20), transitions.get(30), 1));
        ins.add(new ArcIn(points.get(23), transitions.get(30), 1));
        ins.add(new ArcIn(points.get(0), transitions.get(0), 1));
        ins.add(new ArcIn(points.get(8), transitions.get(9), 1));
        ins.add(new ArcIn(points.get(27), transitions.get(34), 1));

        outs.add(new ArcOut(transitions.get(11), points.get(11), 1));
        outs.add(new ArcOut(transitions.get(24), points.get(18), 1));
        outs.add(new ArcOut(transitions.get(26), points.get(18), 1));
        outs.add(new ArcOut(transitions.get(3), points.get(3), 1));
        outs.add(new ArcOut(transitions.get(33), points.get(26), 1));
        outs.add(new ArcOut(transitions.get(1), points.get(1), 1));
        outs.add(new ArcOut(transitions.get(28), points.get(18), 1));
        outs.add(new ArcOut(transitions.get(14), points.get(12), 1));
        outs.add(new ArcOut(transitions.get(14), points.get(15), 1));
        outs.add(new ArcOut(transitions.get(14), points.get(23), 1));
        outs.add(new ArcOut(transitions.get(27), points.get(18), 1));
        outs.add(new ArcOut(transitions.get(4), points.get(4), 1));
        outs.add(new ArcOut(transitions.get(15), points.get(12), 1));
        outs.add(new ArcOut(transitions.get(15), points.get(16), 1));
        outs.add(new ArcOut(transitions.get(15), points.get(23), 1));
        outs.add(new ArcOut(transitions.get(18), points.get(8), 1));
        outs.add(new ArcOut(transitions.get(2), points.get(2), 1));
        outs.add(new ArcOut(transitions.get(31), points.get(22), 1));
        outs.add(new ArcOut(transitions.get(23), points.get(17), 1));
        outs.add(new ArcOut(transitions.get(23), points.get(24), PRICE_OF_PREORDER));
        outs.add(new ArcOut(transitions.get(7), points.get(7), 1));
        outs.add(new ArcOut(transitions.get(22), points.get(17), 1));
        outs.add(new ArcOut(transitions.get(22), points.get(24), PRICE_OF_PREORDER));
        outs.add(new ArcOut(transitions.get(17), points.get(5), 1));
        outs.add(new ArcOut(transitions.get(19), points.get(11), 1));
        outs.add(new ArcOut(transitions.get(20), points.get(17), 1));
        outs.add(new ArcOut(transitions.get(20), points.get(24), PRICE_OF_PREORDER));
        outs.add(new ArcOut(transitions.get(12), points.get(12), 1));
        outs.add(new ArcOut(transitions.get(12), points.get(13), 1));
        outs.add(new ArcOut(transitions.get(12), points.get(23), 1));
        outs.add(new ArcOut(transitions.get(8), points.get(8), 1));
        outs.add(new ArcOut(transitions.get(10), points.get(10), 1));
        outs.add(new ArcOut(transitions.get(5), points.get(5), 1));
        outs.add(new ArcOut(transitions.get(29), points.get(18), 1));
        outs.add(new ArcOut(transitions.get(32), points.get(27), 1));
        outs.add(new ArcOut(transitions.get(6), points.get(6), 1));
        outs.add(new ArcOut(transitions.get(13), points.get(12), 1));
        outs.add(new ArcOut(transitions.get(13), points.get(14), 1));
        outs.add(new ArcOut(transitions.get(13), points.get(23), 1));
        outs.add(new ArcOut(transitions.get(16), points.get(2), 1));
        outs.add(new ArcOut(transitions.get(21), points.get(17), 1));
        outs.add(new ArcOut(transitions.get(21), points.get(24), PRICE_OF_PREORDER));
        outs.add(new ArcOut(transitions.get(25), points.get(19), 1));
        outs.add(new ArcOut(transitions.get(25), points.get(20), 1));
        outs.add(new ArcOut(transitions.get(25), points.get(23), 1));
        outs.add(new ArcOut(transitions.get(25), points.get(24), 1));
        outs.add(new ArcOut(transitions.get(30), points.get(18), 1));
        outs.add(new ArcOut(transitions.get(0), points.get(0), 1));
        outs.add(new ArcOut(transitions.get(0), points.get(21), 1));
        outs.add(new ArcOut(transitions.get(9), points.get(9), 1));
        outs.add(new ArcOut(transitions.get(32), points.get(25), 1));
        outs.add(new ArcOut(transitions.get(34), points.get(28), numberOfEmployees * EMPLOYEE_SALARY));

        PetriNet net = new PetriNet("CourseWork", points, transitions, ins, outs);
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();

        return net;
    }
}
