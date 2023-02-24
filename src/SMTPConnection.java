import java.net.*;
import java.io.*;

/**
 * Open an SMTP connection to a mailserver and send one mail.
 *
 */
public class SMTPConnection {
    /* The socket to the server */
    private Socket connection;

    /* Streams for reading and writing the socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnection object. Create the socket and the 
       associated streams. Initialize SMTP connection. */
    public SMTPConnection(Envelope envelope) throws IOException {
        connection = new Socket("datacomm.bhsi.xyz", 2526);
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());

        String msg;

        msg = fromServer.readLine();
        if(!msg.contains("220")){
            throw new IOException("Server didn't respond");
        }

	    /* SMTP handshake. We need the name of the local machine.
	    Send the appropriate SMTP handshake command. */
        String localhost = "localhost";
        sendCommand("HELO " + localhost, 250);


        isConnected = true;
    }

    /* Send the message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(Envelope envelope) throws IOException {
        System.out.println("Sending mail");
        //this.close();
        sendCommand("MAIL FROM: <"+envelope.Sender+">", 250);
        sendCommand("RCPT TO: <" + envelope.Recipient+">", 250);
        sendCommand("DATA " + envelope.Message, 354);
        sendCommand(".", 250);
    }

    /* Close the connection. First, terminate on SMTP level, then
       close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand( "quit", 221 );
            connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code is
       what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        /* Fill in */
        /* Write command to server and read reply from server. */
        /* Fill in */

        toServer.writeBytes(command + "\r\n");
        toServer.flush();


        /* Fill in */
	    /* Check that the server's reply code is the same as the parameter
	   rc. If not, throw an IOException. */
        /* Fill in */
        if(parseReply(fromServer.readLine()) != rc){
            throw new IOException("Wrong command");
        }
    }

    /* Parse the reply line from the server. Returns the reply code. */
    private int parseReply(String reply) {
        System.out.println(reply);
        return Integer.parseInt(reply.substring(0, 3));
    }

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
        if(isConnected) {
            close();
        }
        super.finalize();
    }
}