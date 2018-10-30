import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Stack;

class RpnClient {
    private static Socket client;

    public static void main(String[] args) {
        // check the arguments in commands line
        if (args.length != 2)
            throw new IllegalArgumentException("Parameters: <Port><Rpn>");
        if (!args[0].equals("8189"))
            throw new IllegalArgumentException("Port number should be 8189");
        if (args[1] == null || args[1].length() == 0)
            throw new IllegalArgumentException("Rpn cannot be empty");

        try {
            // establish the client and connect to server
            String host = "localhost";
            InetAddress address = InetAddress.getByName(host);
            int port = Integer.parseInt(args[0]);
            client = new Socket(address, port);
            try {
                // Input stream to read msg from server
                DataInputStream in = new DataInputStream(client.getInputStream());
                //  Output stream to send msg to server
                DataOutputStream out = new DataOutputStream(client.getOutputStream());

                // start to send rpn calc to sever
                String rpn = args[1];
                String[] tokens = rpn.split(" ");
                Stack<String> st = new Stack<>();
                int index = 0;
                while (index < tokens.length) {
                    if (tokens[index].equals("+") || tokens[index].equals("-")
                            || tokens[index].equals("*") || tokens[index].equals("/")) {
                        String right = null, left = null, calc = null;
                        if (!st.isEmpty()) {
                            right = st.pop();
                        }
                        if (!st.isEmpty()) {
                            left = st.pop();
                        }
                        if (left == null || right == null) { // handle rpn expression hasn't enough operands
                            System.out.println("[Error100] Incomplete expression: lack of operand");
                            break;
                        } else if (Long.parseLong(left) < Integer.MIN_VALUE || Long.parseLong(left) > Integer.MAX_VALUE
                            || Long.parseLong(right) < Integer.MIN_VALUE || Long.parseLong(right) > Integer.MAX_VALUE) {
                            System.out.println("[Error102] Wrong expression: operand is integer overflow");
                        } else {
                                calc = left + " " + right + " " + tokens[index];
                                // send calculation with single operation to server
                                out.writeUTF(calc);
                                out.flush();
                                // read response from server and print it
                                String response = in.readUTF();
                                if (response.equals("Error103")) { // handle the result integer overflow
                                    System.out.println("[Error103] Wrong calculation: result is integer overflow");
                                    break;
                                }
                                if (response.equals("Error104")) { // handle divisor is 0 in rpn calculation
                                    System.out.println("[Error104] Wrong calculation: divisor is 0");
                                    break;
                                }
                                st.push(response);
                                if (index == tokens.length - 1)
                                    System.out.println("Client Received Final: " + response);
                                else
                                    System.out.println("Client Received: " + response);
                        }
                    }  else {
                        st.push(tokens[index]);
                    }
                    index++;
                }
                if (st.size() > 1) { // handle rpn expression hasn't enough operators
                    System.out.println("[Error101] Incomplete expression: lack of operator");
                }
                // when rpn is finished or is wrong, send a "done" msg to server to close the socket
                out.writeUTF("Bye");
            } finally {
                // close the socket
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}