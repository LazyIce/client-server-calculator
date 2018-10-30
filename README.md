A four-function reverse polish notation calculator client-server application
===========================

### Introduction
* **Class** ：CS6250 - Computer Network 2018 Fall
* **Title** : Programming Assignment
* **Name** ：Bin Xie
* **Email** ：bin.xie@gatech.edu
* **Date** ：10/30/2018
 
### File structure
bxie41.zip  
&nbsp;&nbsp;| -- bxie41/  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| -- rpnclient.java  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| -- rpnserver.java  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| -- README.pdf  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| -- sample.txt  

 
### Compiling and running instructions
* Open a terminal and go into *bxie41* directory  
* Compile rpnserver.java  
<code>javac rpnserver.java</code>
* Run server first  
<code>java RpnServer</code>
* Open another terminal and go into *bxie41* directory
* Compile rpnclient.java  
<code>javac rpnclient.java</code>
* Then run client  
<code>java RpnClient 8189 "1 2 + 3 *"</code>  
  * The port# must be 8189. If you want to change it, you need to change the port# in rpnserver.java
  * For expression, there is a space between every two tokens
  * Operands must be integer number in range [-2147483648, 2147483647]
  * Operators must be "+", "-", "*" or "/"
  * The length of command shouldn't exceed 262144. Since the linux allows 262144 characters for command line
* The client will be closed after finishing all requests.  
  Then server can accept other clients.
* If you want to close the server, use <code>ctrl + c</code>
 
### Protocol description
* **The format of message between client and server**
    * The request from client to server is a String type expression  
    <code>&lt;operand&gt; &lt;operand&gt; &lt;operator&gt;</code>  
    eg: "1 5 +", "2 4 -", "3 8 *", "4 10 /"
    * The response from server to client is a String type result  
    eg: "6", "-2", "24", "0" (For division, the result is also int)  
    If there is error with request, the server response error type string  
    eg: "Error101", "Error102", "Error103"  
    * Here is some descrition for error message
        * [Error100] Incomplete expression: lack of operand
        * [Error101] Incomplete expression: lack of operator
        * [Error102] Wrong expression: operand is integer overflow
        * [Error103] Wrong calculation: result is integer overflow
        * [Error104] Wrong calculation: divisor is 0
  
* **How multiple requests are handled between client and server**  
  * The multiple requests will be handled sequentially. The client will seperate the initial reverse polish notation into several expressions. It will send a expression to server and wait for the response from server. When it receives the response from server, it will send the new expression to server. Go and return as metioned above.
  * In my algorithm, I use a stack to store the operands. I split the reverse polish notation by space into array of tokens. Then I traverse the tokens array. For operands, push it into stack. For operators, pop two operands from stack and construct the expression. Client send the expression to server. And server receive the expression and calculate the result. During the server calculating, the client wait for the response. When finishing calculating, the server send response to client. The client receive the response. If response is a calculation result, the client push it into stack and do the same work above until the reverse polish notation is finished. Or if response is a error message, the client print the error message and closed.
   
* **How TCP server knows that expression is complete**  
When the reverse polish notation is complete, the client will send an "Bye" message to server to notify it that the client is going to close and server can close the connection to client.
    
* **Anything else implemented**
  * In client, it will check the arguments. If there is any format error in argument, it will throw out IIIegalArgumentException.
  * Both in server and client, if it has errors in establishing the socket and connection, it will throw out IOException.
  * For each calculation, the result must also be in the range of int type. Or server will give a error message to client.
 
### Bugs or limitations
* I haven't found any bugs yet.
* My limitation is that I can only handle the calculation of int type. Any calculation including number out of range of int type will throw an error.
 
 