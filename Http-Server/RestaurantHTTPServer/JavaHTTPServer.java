import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.io.FileNotFoundException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;

class JavaHTTPServer implements Runnable {
  static final File WEBROOT = new File(".", "assets");
  static final String DEFAULT = "index.html";
  static final String FILE_NOT_FOUND = "404.html";
  static final String METHOD_NOT_SUPPORTED = "not_supported.html";

  static final boolean verbose = true;

  private Socket connect;

  public JavaHTTPServer(Socket c) {
    connect = c;
  }

  @Override
  public void run() {
    BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
    String fileRequested = null;

    try {
      // read characters from client via input stream
      // in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
      in  = new BufferedReader(new InputStreamReader(connect.getInputStream()));
      // character output stream to client (for headers)
      out = new PrintWriter(connect.getOutputStream());
      // bytes output stream to client (for requested data)
      dataOut = new BufferedOutputStream(connect.getOutputStream());

      // get first line of request from client
      String input = in.readLine();
      // parse the request with tokenizer
      StringTokenizer parse = new StringTokenizer(input);
      // get the HTTP method of the client
      String method = parse.nextToken().toUpperCase();
      // get the file requested
      fileRequested = parse.nextToken().toLowerCase();

      // handle unsupported methods
      if (!method.equals("GET") && !method.equals("POST")) {
        if (verbose) { System.out.println("501 Not Implemented " + method + " method."); }
        // return not supported file to the client
        File file = new File(WEBROOT, METHOD_NOT_SUPPORTED);
        int fileLength = (int) file.length();
        String contentMimeType = "text/html";
        // read contents to return to client
        byte[] fileData = readFileData(file, fileLength);

        // send HTTP header with data
        out.println("HTTP/1.1 501 Not Implemented");
        out.println("Server: Java HTTP Server");
        out.println("Date: " + new Date());
        out.println("Content-type: " + contentMimeType);
        out.println("Content-length: " + fileLength);
        out.println();
        out.flush();
        // send the file
        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();
      } else {
        if (fileRequested.endsWith("/")) {
          fileRequested += DEFAULT;
        }
        File file = new File(WEBROOT, fileRequested);
        int fileLength = (int) file.length();
        String contentType = getContentType(fileRequested);

        if (method.equals("GET")) {
          byte[] fileData = readFileData(file, fileLength);

          // send HTTP headers
          out.println("HTTP/1.1 200 OK");
          out.println("Server: Java HTTP Server");
          out.println("Date: " + new Date());
          out.println("Content-type: " + contentType);
          out.println("Content-length: " + fileLength);
          out.println();
          out.flush();
          // send the file
          dataOut.write(fileData, 0, fileLength);
          dataOut.flush();

          if (verbose) {
            System.out.println("File " + fileRequested + " of type " + contentType + " returned.");
          }
        } else if (method.equals("POST")) {
          // read the json: list of items being sent
          while (!in.readLine().isEmpty());
          String requestBody = in.readLine();
          JSONObject jsonObject = new JSONObject(requestBody);
          JSONArray jsonArray = (JSONArray) jsonObject.get("menuIds");
          HashMap<Integer, Integer> order = new HashMap<>();
          for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = (JSONObject) jsonArray.get(i);
            order.put((Integer) jsonObject.get("id"), (Integer) jsonObject.get("qty"));
          }
          // read the order file to calculate the amount of time required for each item
          byte[] fileData = readFileData(file, fileLength);
          jsonObject = new JSONObject(new String(fileData));
          jsonArray = (JSONArray) jsonObject.get("items");
          // map the id to the preparation_time
          HashMap<Integer, Integer> id_time = new HashMap<>();
          for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = (JSONObject) jsonArray.get(i);
            id_time.put((Integer) jsonObject.get("id"), (Integer) jsonObject.get("preparation_time"));
          }
          // compute the total time required
          int preparation_time = 0;
          for (Integer id: order.keySet()) {
            preparation_time += id_time.get(id) * order.get(id);
          }
          // send the json with preparation_time to the client
          String msg = "{\"preparation_time\": " + String.valueOf(preparation_time) + "}";
          byte[] msg_bytes = msg.getBytes();
          // send HTTP headers
          out.println("HTTP/1.1 200 OK");
          out.println("Server: Java HTTP Server");
          out.println("Date: " + new Date());
          out.println("Content-type: application/json");
          out.println("Content-length: " + msg.length());
          out.println();
          out.flush();
          // send the msg
          dataOut.write(msg_bytes, 0, msg_bytes.length);
          dataOut.flush();

          if (verbose) {
            System.out.println("Preparation Time for given" + fileRequested + " returned.");
          }
        }
      }
    } catch (FileNotFoundException e) {
      try {
        fileNotFound(out, dataOut, fileRequested);
      } catch(IOException ioe) {
        System.err.println("Error with File Not Found Exception: " + ioe.getMessage());
      }
    } catch (IOException e) {
      System.err.println("IOException: " + e.getMessage());
    } finally {
      try {
        in.close();
        out.close();
        dataOut.close();
        connect.close();
      } catch (IOException ioe) {
        System.out.println("Error closing stream: " + ioe.getMessage());
      }

      if (verbose) { System.out.println("Connections closed.\n"); }
    }
  }

  private byte[] readFileData(File file, int fileLength) throws IOException {
    FileInputStream fin = null;
    byte[] fileData = new byte[fileLength];

    try {
      fin = new FileInputStream(file);
      fin.read(fileData);
    } finally {
      if (fin != null) fin.close();
    }

    return fileData;
  }

  private String getContentType(String fileRequested) {
    if (fileRequested.endsWith("htm") || fileRequested.endsWith("html"))
      return "text/html";
    else if (fileRequested.endsWith("png"))
      return "image/png";
    else return "text/plain";
  }

  private void fileNotFound(PrintWriter out, BufferedOutputStream dataOut, String fileRequested) throws IOException {
    File file = new File(WEBROOT, FILE_NOT_FOUND);
    int fileLength = (int) file.length();
    String contentMimeType = "text/html";
    byte[] fileData = readFileData(file, fileLength);

    // send HTTP header with data
    out.println("HTTP/1.1 404 File Not Found");
    out.println("Server: Java HTTP Server");
    out.println("Date: " + new Date());
    out.println("Content-type: " + contentMimeType);
    out.println("Content-length: " + fileLength);
    out.println();
    out.flush();
    // send the file
    dataOut.write(fileData, 0, fileLength);
    dataOut.flush();

    if (verbose) { System.out.println("File " + fileRequested + " Not Found"); }
  }
}
