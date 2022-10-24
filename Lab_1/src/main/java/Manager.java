import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import os.lab1.compfuncs.advanced.Concatenation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class Manager {

    private Socket clientSocketF = null;
    private Socket clientSocketG = null;
    private TaskProcess taskG = null;
    private TaskProcess taskF = null;
    //private int x;

    public Manager() {
        //System.out.println("26");
        Function<Integer, Optional<Optional<String>>> f = (x) -> {
            try {
                return Concatenation.trialF(x);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        Function<Integer, Optional<Optional<String>>> g = (x) -> {
            try {
                return Concatenation.trialG(x);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        try {
            ServerSocket serverSocket = new ServerSocket(2222);
            System.out.println("server socket was created");

            taskF = new TaskProcess(f);
            taskG = new TaskProcess(g);


            clientSocketF = serverSocket.accept();
            clientSocketG = serverSocket.accept();

            System.out.println("client sockets was initialized");
        } catch (IOException e) {
            e.printStackTrace();
        }
       // System.out.println("34");

        Runtime current = Runtime.getRuntime();
        current.addShutdownHook(new Thread(()->{

                //System.out.println("Computations cancelled:");
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


    }
    private void sendX(int x, Socket clientSocket){
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(x);
            //out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private String recvResult(Socket clientSocket){
        String result = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            result = in.readLine();
            //in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void run(){
        System.out.println("Enter x: ");
        Scanner scannerMain = new Scanner(System.in);
        final int x = scannerMain.nextInt();

        System.out.println("Enter q for cancel computation\n");
        Thread cancel = new Thread(getCancellationRunnable());
        cancel.setDaemon(true);
        cancel.start();

        CompletableFuture<String> fTask = CompletableFuture.supplyAsync(() -> {
            sendX(x, clientSocketF);
            taskF.run();
            return recvResult(clientSocketF);
        });
        CompletableFuture<String> gTask = CompletableFuture.supplyAsync(() -> {
            sendX(x, clientSocketG);
            taskG.run();
            return recvResult(clientSocketG);
        });

        CompletableFuture<String> result = fTask.thenCombine(gTask, (f , g)-> f + g);
        try {
            System.out.println("\nResult:\n" + result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private @NotNull Runnable getConfirmationRunnable(AtomicLong start, AtomicBoolean confirmation){
        return ()->{
            try {
                Thread.sleep(5000);
                start.set(0);
                confirmation.set(false);
                System.out.println("Timeout: computing will continue");
            } catch (InterruptedException ignored) {}
        };
    }

    private Runnable getCancellationRunnable(){
        return () -> {
            AtomicBoolean confirmation = new AtomicBoolean(false);
            AtomicLong start = new AtomicLong(0);
            Scanner scanner = new Scanner(System.in);
            Thread confirmationThread = null;
            while (true) {
                while (!scanner.hasNextLine()) {}
                String input = scanner.nextLine();
                if (!confirmation.get() && input.equals("q")) {
                    System.out.println("Please confirm that computation should be stopped y/n:");
                    start.set(System.currentTimeMillis());
                    confirmation.set(true);
                    confirmationThread = new Thread(getConfirmationRunnable(start, confirmation));
                    confirmationThread.setDaemon(true);
                    confirmationThread.start();
                }else  if (confirmation.get() && input.equals("y")) {
                    Objects.requireNonNull(confirmationThread).interrupt();
                    System.out.println("Computations cancelled:");
                    System.exit(0);
                }else if(confirmation.get() && input.equals("n")){
                    confirmation.set(false);
                    start.set(0);
                    System.out.println("Computing will continue");
                    Objects.requireNonNull(confirmationThread).interrupt();
                }
            }
        };
    }
}
