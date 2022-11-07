
public class Main {
    public static void main(String[] args) {
        ShortestRemainingTimeFirstSchedulingAlgorithm scheduler = new ShortestRemainingTimeFirstSchedulingAlgorithm();


        scheduler.createProcesses(5, 5, 2, 8);
        //scheduler.createProcessesCustomCase();


        scheduler.printProcesses();
        scheduler.run();

        scheduler.getStatistic();
    }
}
