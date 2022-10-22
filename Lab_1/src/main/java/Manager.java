import os.lab1.compfuncs.advanced.Concatenation;
import os.lab1.compfuncs.advanced.DoubleOps;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Manager {
    private Function<Integer, Optional<Optional<String>>> f;
    private Function<Integer, Optional<Optional<String>>> g;

    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private PrintWriter out;
    private BufferedReader in;
    private TaskProcess taskG = null;
    private TaskProcess taskF = null;
    //private int x;

    public Manager() {
        //System.out.println("26");

        try {
            serverSocket = new ServerSocket(2222);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
       // System.out.println("34");

        Runtime current = Runtime.getRuntime();
        current.addShutdownHook(new Thread(()->{

                System.out.println("Computations cancelled:");
            switch (taskF.getResult()) {
                case null -> System.out.println("f(x) - not finished");
                case "$$SoftFail" -> System.out.println("f(x) - soft fail");
                case "$$HardFail" -> System.out.println("f(x) - hard fail");
                default -> System.out.println("f(x) - computed");
            }

            switch (taskG.getResult()) {
                case null -> System.out.println("g(x) - not finished");
                case "$$SoftFail" -> System.out.println("g(x) - soft fail");
                case "$$HardFail" -> System.out.println("g(x) - hard fail");
                default -> System.out.println("g(x) - computed");
            }

        }
        ));
        //System.out.println("55");

        f = (x) ->{
            try {
                return Concatenation.trialF(x);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        g = (x) ->{
            try {
                return Concatenation.trialG(x);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        //System.out.println("ss");
    }
    public void run(){
        System.out.println("Enter x: ");
        Scanner scannerMain = new Scanner(System.in);
        final int x = scannerMain.nextInt();
        Thread cancel = new Thread(getCancellationRunnable());
        cancel.start();
        //getCancellationRunnable().run();
        //System.out.println("eee");
        CompletableFuture<String> fTask = CompletableFuture.supplyAsync(() -> {
            taskF = new TaskProcess(x, f);
            taskF.run();
            //System.out.println(taskF.getResult());

            return taskF.getResult();//f.apply(x).get();
            //return taskF.run();
        });
        CompletableFuture<String> gTask = CompletableFuture.supplyAsync(() -> {
            taskG = new TaskProcess(x, g);
            taskG.run();
            return taskG.getResult();//f.apply(x).get();
            //return taskG.run();
        });

        CompletableFuture<String> result = fTask.thenCombine(gTask, (f , g)-> f + g);
        //System.out.println("qqq");
        try {
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
    private Runnable getCancellationRunnable(){
        return () -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                while (!scanner.hasNextLine()) {}
                String input = scanner.nextLine();
                if (input.equals("q")) {
                    System.exit(0);
                }
            }
        };
    }
}
