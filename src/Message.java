import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.text.*;

/* $Id: Message.java,v 1.5 1999/07/22 12:10:57 kangasha Exp $ */

/**
 * Mail message.
 *
 * @author Jussi Kangasharju
 */
public class Message {
    /* The headers and the body of the message. */
    public String Headers;
    public String Body;

    /* Sender and recipient. With these, we don't need to extract them
       from the headers. */
    private String From;
    private String To;

    /* To make it look nicer */
    private static final String CRLF = "\r\n";

    /* Create the message object by inserting the required headers from
       RFC 822 (From, To, Date). */
    public Message(String from, String to, String subject, String text) {
        /* Remove whitespace */
        From = from.trim();
        To = to.trim();
        Headers = "From: " + From + CRLF;
        Headers += "To: " + To + CRLF;
        Headers += "Subject: " + subject.trim() + CRLF;
        Headers += "MIME-Version: 1.0" + CRLF;
	    /* A close approximation of the required format. Unfortunately
	   only GMT. */
        SimpleDateFormat format =
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        String dateString = format.format(new Date());
        Headers += "Date: " + dateString + CRLF;
        Body = text;
    }

    public void addPicture(String fileName){
        String guid = UUID.randomUUID().toString();
        Headers += "Content-Type: multipart/mixed;" + "boundary=\"" + guid + "\"" + CRLF;
        guid = "--" + guid;
        Body += guid + CRLF;
        Body += "Content-Type:application/octet-stream;name="+fileName + CRLF;
        Body += "Content-Transfer-Encoding:base64" + CRLF;
        Body += "Content-Disposition:attachment;filename=\""+ fileName + "\"" + CRLF;
        try {
            var test = this.getClass().getResourceAsStream("/teacher.jpeg");
            String encodedFile = Base64.getEncoder().encodeToString(test.readAllBytes());
            Body += CRLF + encodedFile;
        }catch (IOException e){
            System.out.println("Not found");
        }
        Body += CRLF + guid;

    }
    public void addHeader(String header){
        this.Headers = this.Headers + CRLF + header;
    }
    /* Two functions to access the sender and recipient. */
    public String getFrom() {
        return From;
    }

    public String getTo() {
        return To;
    }

    /* Check whether the message is valid. In other words, check that
       both sender and recipient contain only one @-sign. */
    public boolean isValid() {
        int fromat = From.indexOf('@');
        int toat = To.indexOf('@');

        if(fromat < 1 || (From.length() - fromat) <= 1) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if(toat < 1 || (To.length() - toat) <= 1) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        if(fromat != From.lastIndexOf('@')) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if(toat != To.lastIndexOf('@')) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        return true;
    }

    /* For printing the message. */
    public String toString() {
        String res;

        res = CRLF + Headers + CRLF;
        res += Body + CRLF;
        return res;
    }
}
