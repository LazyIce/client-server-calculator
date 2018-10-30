import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class RpnServer {
    private static final int PORT = 8189;
    private static ServerSocket server;
    private static Socket socket;

    public static void main(String[] args) {
        try  {
            // establish server socket
            server = new ServerSocket(PORT);
            while(true) {
                try {
                    // wait for client connection
                    socket = server.accept();
                    // input stream to read msg from client
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    // output stream to send msg to client
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    while (true) { //The server will never exit by client user action
                        // read request msg from client
                        String request = in.readUTF();
                        // check whether rpn calculation is finished
                        if (request.equals("Bye"))
                            break;
                        // print server received msg
                        System.out.println("Server received: " + "\"" + request + "\"");
                        // calculate the request msg
                        String[] tokens = request.split(" ");
                        long left = Long.parseLong(tokens[0]);
                        long right = Long.parseLong(tokens[1]);
                        String operator = tokens[2];
                        String response= null;
                        if (operator.equals("+")) {
                            long result = left + right;
                            // check whether the result out of range for int type
                            if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
                                response = "Error103";
                            } else {
                                response = String.valueOf(result);
                            }
                        }
                        else if (operator.equals("-")) {
                            long result = left - right;
                            if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
                                response = "Error103";
                            } else {
                                response = String.valueOf(result);
                            }
                        }
                        else if (operator.equals("*")) {
                            long result = left * right;
                            if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
                                response = "Error103";
                            } else {
                                response = String.valueOf(result);
                            }
                        }
                        else if (operator.equals("/")) {
                            if (right == 0) { // check whether divisor is 0
                                response = "Error104";
                            } else {
                                long result = left / right;
                                if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
                                    response = "Error103";
                                } else {
                                    response = String.valueOf(result);
                                }
                            }
                        }
                        // send response msg to client
                        out.writeUTF(response);
                        out.flush();
                    }
                } finally {
                    // close the socket
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}