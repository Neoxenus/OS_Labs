import java.util.*;
import java.util.function.BinaryOperator;

public class ShortestRemainingTimeFirstSchedulingAlgorithm {
    private final List<Process> processes;
    private final List<Process> waitingPool;

    private Process currentRunningProcess;
    private int numberOfProcesses;
    private int finishedProcesses;
    private int currentTime;


    public ShortestRemainingTimeFirstSchedulingAlgorithm() {
        processes = new ArrayList<>();
        waitingPool = new ArrayList<>();
        currentRunningProcess = null;
        finishedProcesses = 0;
    }

    public void createProcesses(int numberOfProcesses, int maxArrivalTime, int minBurstTime,  int maxBurstTime){
        this.numberOfProcesses = numberOfProcesses;
        for (int i = 0; i < numberOfProcesses; ++i) {
            processes.add(Process.createProcess(maxArrivalTime, minBurstTime, maxBurstTime));
        }
    }
    public void createProcessesCustomCase(){
        processes.add(new Process( 2, 6));
        processes.add(new Process(5, 2));
        processes.add(new Process( 1, 8));
        processes.add(new Process( 0, 3));
        processes.add(new Process( 4, 4));
        numberOfProcesses = 5;
    }
    public void printProcesses(){
        String format = "%15s %15s %15s %n";
        System.out.printf(format, "Process", "Burst Time", "Arrival Time");
        for (Process e : processes) {
            System.out.printf(format, e, e.getInitialBurstTime(), e.getArrivalTime());
        }
    }
    private boolean arrivingOfProcesses(){
        boolean isProcessArrived = false;
        for (Process process : processes) {
            if (process.getArrivalTime() == currentTime) {
                isProcessArrived = true;
                //Process buf = processes.remove(i);
                waitingPool.add(process);
                System.out.println("Process " + process + " arrives");
                //i--;
            }
        }
        return isProcessArrived;
    }
    private boolean checkAndChangeCurrentProcess(){
        //Comparator.comparingInt(Process::getRemainingBurstTime)
        Process previous = currentRunningProcess;
        Optional<Process> min = waitingPool.stream().reduce(BinaryOperator.minBy(Comparator.comparingInt(Process::getRemainingBurstTime)));
        if(min.isPresent()){
            if(currentRunningProcess == null ||
                    min.get().getRemainingBurstTime() < currentRunningProcess.getRemainingBurstTime()){
                if(currentRunningProcess != null)
                    waitingPool.add(currentRunningProcess);
                currentRunningProcess = min.get();
                waitingPool.remove(min.get());

            }
        }
        return previous != currentRunningProcess;
    }
    public void run() {
        currentTime = 0;
        int previousTime = 0;
        boolean processChanged = false;
        while (finishedProcesses != numberOfProcesses) {//change+++
            boolean isArrived = arrivingOfProcesses();
            if (isArrived)
                processChanged = checkAndChangeCurrentProcess();

            currentTime++;

            if (currentRunningProcess != null) {
                currentRunningProcess.run();
                if (processChanged || isArrived || currentRunningProcess.getRemainingBurstTime() == 0){
                    System.out.println("Process " + currentRunningProcess + " start executing");
                    print(previousTime);
                    processChanged = false;
                    previousTime = currentTime;
                }

                if (currentRunningProcess.getRemainingBurstTime() == 0){
                    System.out.println("The process " + currentRunningProcess + " finishes its execution.");
                    currentRunningProcess.setCompletionTime(currentTime);
                    finishedProcesses++;
                    currentRunningProcess = null;
                    processChanged = checkAndChangeCurrentProcess();
                }
            }
        }
    }
    private void print(int previousTime){
        System.out.println("------------------------>");
        String format = "%20s %20s %20s %25s %25s %n";
        System.out.println("Time: " + previousTime + "-" + currentTime );


        System.out.printf("%45s %20s %25s %25s %n", "Process", "Arrival Time", "Initial Burst Time",
                "Remaining Burst Time");
        if(currentRunningProcess == null){
            System.out.println("No current running process");
        }else{
            System.out.printf(format, "Current process", currentRunningProcess, currentRunningProcess.getArrivalTime(),
                    currentRunningProcess.getInitialBurstTime(), currentRunningProcess.getRemainingBurstTime());
        }

        for (Process e : waitingPool) {
            System.out.printf(format,"Waiting process", e, e.getArrivalTime(),
                    e.getInitialBurstTime(), e.getRemainingBurstTime());
        }
        System.out.println("<------------------------");

    }

    public void getStatistic(){
        String format = "%20s %20s %20s %20s %20s  %n";
        System.out.printf(format, "Process","Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");
        double sumTurnAroundTime = 0;
        double sumWaitingTime = 0;
        for (Process e : processes) {
            sumTurnAroundTime += (e.getCompletionTime() - e.getArrivalTime());
            sumWaitingTime +=(e.getCompletionTime() - e.getArrivalTime() - e.getInitialBurstTime());
            System.out.printf(format, e,
                    e.getInitialBurstTime(),
                    e.getCompletionTime(),
                    (e.getCompletionTime() - e.getArrivalTime()),
                    (e.getCompletionTime() - e.getArrivalTime() - e.getInitialBurstTime()) );
        }
        System.out.println("Average turn around time = " + sumTurnAroundTime/numberOfProcesses);
        System.out.println("Average waiting time = " + sumWaitingTime/numberOfProcesses);
        //System.out.printf("%40s %20s %20s  %n", "Average", sumTurnAroundTime/numberOfProcesses, sumWaitingTime/numberOfProcesses);
    }

}
