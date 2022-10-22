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

    public TaskProcess(Function<Integer, Optional<Optional<String>>> function) {
        id = nextId++;
        this.function = function;
        try {
            clientSocket = new Socket("localhost", 2222);
            System.out.println("clint was created");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getResult(){
        return result;
    }
    public void run(){
        System.out.println(clientSocket.getInetAddress().getHostName() + id + " connected");
        try {
            x = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("x is " + x +" id is " + id);
        try {
            Optional<Optional<String>> softOptional = Optional.empty();

            for (int i = 0; i < MAX_COMPUTATION_ATTEMPTS; i++){
                softOptional = function.apply(x);
                if (softOptional.isPresent()){
                    break;
                }
            }
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

        //sending result
        out.println(result);
    }
}
