import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class TaskProcess{

    private int x;
    private Function<Integer, Optional<Optional<String>>> function;

    private static final int MAX_COMPUTATION_ATTEMPTS = 5;

    private Socket clientSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private static int nextId = 0;
    private int id;
    private String result;

    public TaskProcess(int x, Function<Integer, Optional<Optional<String>>> function) {
        this.x = x;
        id = nextId++;
        this.function = function;
        try {
            clientSocket = new Socket("localhost", 2222);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //this.socket = socket;
    }
    public String getResult(){
        return result;
    }
    public void run(){
        System.out.println(clientSocket.getInetAddress().getHostName() + " connected");
//        if(clientSocket.isClosed())
//        {
//            System.out.println("id- " + id);
//        }
//        final String[] input = new String[1];
//        Thread read = new Thread(() -> {
//            while(true){
//                try{
//                    String obj = in.readLine();
//                    input[0] = obj;
//                    System.out.println(obj);
//                    break;
//                }
//                catch(IOException e){ e.printStackTrace(); }
//            }
//        });
//
//        read.setDaemon(true);
//        read.start();
//        try {
//            read.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //System.out.println("X was received, id: " + id + " "+ input[0]);
        //x = Integer.parseInt(input[0]);
        try {
            Optional<Optional<String>> softOptional = Optional.empty();

            for (int i = 0; i < MAX_COMPUTATION_ATTEMPTS; i++){
                softOptional = function.apply(x);
                if (softOptional.isPresent()){
                    break;
                }
            }
            //System.out.println("sss");
            if (softOptional.isPresent()){
                Optional<String> hardOptional = softOptional.get();
                if(hardOptional.isPresent()){
                    result = hardOptional.get();
                }else {
                    result = "$$HardFail";
                    System.exit(1);
                }
            } else{
                result = "$$SoftFail";
                System.exit(2);

            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("end");
        //return result;
    }
}
