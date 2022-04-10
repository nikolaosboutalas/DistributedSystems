package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import common.Result;

class Input {

    static String getHostName(Scanner scanner) {
        System.out.print("[CLIENT] Please insert host name of the master node: ");
        return scanner.nextLine();
    }

    static Integer getPortNumber(Scanner scanner) {
        System.out.print("[CLIENT] Please insert port number of the master node: ");
        return Integer.parseInt(scanner.nextLine());
    }
}

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String hostName = Input.getHostName(scanner);
        Integer portNumber = Input.getPortNumber(scanner);
        System.out.println("[CLIENT] Waiting for input! Please type a GPX file path: ");
        while (scanner.hasNextLine()) {
            String filePath = scanner.nextLine();
            String fileContent = readFile(filePath);
            new Thread(new ClientThread(fileContent, hostName, portNumber)).start();
            System.out.println("[CLIENT] Waiting for input! Please type a GPX file path: ");
        }
    }

    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }
}

class ClientThread implements Runnable {

    private String fileContent;
    private String hostName;
    private Integer portNumber;

    public ClientThread(String fileContent, String hostName, Integer portNumber) {
        this.fileContent = fileContent;
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    @Override
    public void run() {
        System.out.println("[CLIENT-THREAD] Connecting to " + hostName + ":" + portNumber);
        try {
            Socket clientSocket = new Socket(hostName, portNumber);
            System.out.println("[CLIENT-THREAD] Connected to " + clientSocket);
            clientSocket.getOutputStream().write(fileContent.getBytes());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            Result result = (Result) inputStream.readObject();
            System.out.println("[CLIENT-THREAD] Received result: " + result);
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[CLIENT-THREAD] Failed to connect to " + hostName + ":" + portNumber);
            e.printStackTrace();
        }
    }

}
