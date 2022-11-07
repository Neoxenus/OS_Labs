
public class Process {
    private int id;
    private int arrivalTime;
    private final int initialBurstTime;
    private int remainingBurstTime;
    private static int nextID = 0;

    public Process(int id, int arrivalTime, int burstTime ) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.initialBurstTime = burstTime;
        this.remainingBurstTime = burstTime;
    }
    public static Process createProcess(int maxArrivalTime, int minBurstTime, int maxBurstTime) {
        return new Process(nextID++,
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

    public boolean run(){
        if(remainingBurstTime > 0)
            remainingBurstTime--;
        return remainingBurstTime == 0;
    }

    @Override
    public String toString() {
        return "P" + id;
    }
}
