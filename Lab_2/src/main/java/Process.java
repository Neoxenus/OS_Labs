
public class Process {
    private final int id;
    private final int arrivalTime;
    private final int initialBurstTime;
    private int remainingBurstTime;
    private int completionTime;

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    private static int nextID = 0;

    public Process(int arrivalTime, int burstTime ) {
        this.id = nextID++;
        this.arrivalTime = arrivalTime;
        this.initialBurstTime = burstTime;
        this.remainingBurstTime = burstTime;
    }
    public static Process createProcess(int maxArrivalTime, int minBurstTime, int maxBurstTime) {
        return new Process(
                (int)(Math.random() * maxArrivalTime),
                (int)(Math.random() * (maxBurstTime - minBurstTime) + minBurstTime));
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getInitialBurstTime() {
        return initialBurstTime;
    }

    public int getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public void run(){
        if(remainingBurstTime > 0)
            remainingBurstTime--;
    }

    @Override
    public String toString() {
        return "P" + id;
    }
}
