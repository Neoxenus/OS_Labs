import java.util.*;

public class ShortestRemainingTimeFirstSchedulingAlgorithm {
    private final List<Process> processes;
    private final TreeSet<Process> waitingPool;
    private Process currentRunningProcess;

    private int currentTime;


    public ShortestRemainingTimeFirstSchedulingAlgorithm() {
        processes = new ArrayList<>();
        waitingPool = new TreeSet<>(Comparator.comparingInt(Process::getRemainingBurstTime));
        currentRunningProcess = null;
    }

    public void createProcesses(int numberOfProcesses, int maxArrivalTime, int minBurstTime,  int maxBurstTime){
        for (int i = 0; i < numberOfProcesses; ++i) {
            processes.add(Process.createProcess(maxArrivalTime, minBurstTime, maxBurstTime));
        }
    }
    public void createProcessesSecondCase(){
        processes.add(new Process(1, 2, 6));
        processes.add(new Process(2, 5, 2));
        processes.add(new Process(3, 1, 8));
        processes.add(new Process(4, 0, 3));
        processes.add(new Process(5, 4, 4));
    }
    public void printProcesses(){
        String format = "%15s %15s %15s %n";
        System.out.printf(format, "Process", "Burst Time", "Arrival Time");
        for (Process e : processes) {
            System.out.printf(format, e, e.getRemainingBurstTime(), e.getArrivalTime());
        }
    }
    private boolean arrivingOfProcesses(){
        boolean isProcessArrived = false;
        for (int i = 0; i < processes.size();++i) {
            if (processes.get(i).getArrivalTime() == currentTime) {
                isProcessArrived = true;
                Process buf = processes.remove(i);
                waitingPool.add(buf);
                System.out.println("Process " + buf + " arrives");
                i--;
            }
        }
        return isProcessArrived;
    }
    private boolean checkAndChangeCurrentProcess(){
        Process previous = currentRunningProcess;
        if (currentRunningProcess != null)
            waitingPool.add(currentRunningProcess);
        currentRunningProcess = waitingPool.pollFirst();
        if(previous != currentRunningProcess) {
            System.out.println("Process " + currentRunningProcess + " start executing");
        }
        return previous != currentRunningProcess;
    }
    public void run() {
        currentTime = 0;
        int previousTime = 0;
        boolean processChanged = false;

        while (!waitingPool.isEmpty() || !processes.isEmpty() || currentRunningProcess != null) {
            boolean isArrived = arrivingOfProcesses();
            if (isArrived)
                processChanged = checkAndChangeCurrentProcess();

            currentTime++;

            if (currentRunningProcess != null) {
                currentRunningProcess.run();
                if (processChanged || isArrived || currentRunningProcess.getRemainingBurstTime() == 0){
                    print(previousTime);
                    processChanged = false;
                    previousTime = currentTime;
                }

                if (currentRunningProcess.getRemainingBurstTime() == 0){
                    System.out.println("The process " + currentRunningProcess + " finishes its execution.");
                    currentRunningProcess = null;
                    processChanged = checkAndChangeCurrentProcess();
                }
            }
        }
    }
    public void print(int previousTime){
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
    public static void main(String[] args) {
        ShortestRemainingTimeFirstSchedulingAlgorithm scheduler = new ShortestRemainingTimeFirstSchedulingAlgorithm();
        scheduler.createProcesses(5, 5, 2, 8);
        //scheduler.createProcesses_2();
        scheduler.printProcesses();
        scheduler.run();
    }
}
