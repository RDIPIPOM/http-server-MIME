package mx.ucol.httpserver;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
  final Socket socket;

  public ClientHandler(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    PrintWriter output = null;
    BufferedReader input = null;
    File HTMLFile = null;
    String htmlResponse = "";

    try {
      output = new PrintWriter(socket.getOutputStream(), true);
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String received;
      while ((received = input.readLine()) != null) {
        String requestArray[] = received.split(" ");

        if (requestArray[0].equals("GET")) {
          System.out.println("Resource: " + requestArray[1]);
          switch (requestArray[1]){
            case "/":
            case "/index.html":
              HTMLFile = new File("./www/index.html");
              break;
            case "/about":
            case "/about.html":
              HTMLFile = new File("./www/about.html");
              break;
          }
          Scanner scanner = new Scanner(HTMLFile);
          while (scanner.hasNextLine())
            htmlResponse = htmlResponse + scanner.nextLine();
          scanner.close();
          int contentLength = htmlResponse.length();

          // This line should not be modified just yet
          output.write("HTTP/1.1 200 OK\r\nContent-Length: " +
            String.valueOf(contentLength) + "\r\n\r\n" + htmlResponse);

          // We already sent the response, break the loop
          break;
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        output.close();
        input.close();
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}