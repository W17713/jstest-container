import java.security.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;


class Message {
    String messageName = null;
    String messageContent = null;

}

class MBR1 {  //Message Buffer and Response using Message Class

    private Response response;
    private Message message;
    private boolean messageBufferFull = false;
    private boolean responseBufferFull = false;

    synchronized Response send(Message message)
    {
        this.message = message;
        messageBufferFull = true;
        notify();
        while(responseBufferFull==false)
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                System.out.println("InterruptedException caught");
            }
        //this.response = response;
        responseBufferFull = false;
        return response;
    }

    synchronized Message receive()
    {
        while(messageBufferFull==false)
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                System.out.println("InterruptedException caught");
            }
        messageBufferFull = false;
        notify();
        return message;
    }
    synchronized void reply(Response response)
    {
        this.response = response;
        responseBufferFull = true;
        notify();
    }
}

class ByteMessage {
    byte[] messageName = null;
    byte[] messageContent = null;

}
class Response {
    byte[] status = null;


}

class MBR {  //Message Buffer and Response using ByteMessage class
    private ByteMessage byteMessage;
    private Response response;
    private boolean messageBufferFull = false;
    private boolean responseBufferFull = false;

    synchronized Response send(ByteMessage byteMessage)
    {
        this.byteMessage = byteMessage;
        messageBufferFull = true;
        notify();
        while(responseBufferFull==false)
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                System.out.println("InterruptedException caught");
            }
        responseBufferFull = false;
        return response;
    }

    synchronized ByteMessage receive()
    {
        while(messageBufferFull==false)
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                System.out.println("InterruptedException caught");
            }
        messageBufferFull = false;
        notify();
        return byteMessage;
    }
    synchronized void reply(Response response)
    {
        this.response= response;
        responseBufferFull = true;
        notify();
    }

}

class KeyRequestMessage {
    String messageName = null;
}
class Global{
    public static MBR1 buff1 = new MBR1(); //between sender component and security sender coordinator
    public static MBR buff2 = new MBR(); // between sender coordinator and SMCWR sender
    public static MBR buff3 = new MBR(); //between SMCWR sender and SMCWR receiver
    public static MBR buff4 = new MBR(); //between SMCWR receiver and security receiver coordinator
    public static MBR buff5 = new MBR(); //to send byteMessage between security receiver coordinator and receiver component
    public static int input; //How many messages will be sent?

}


class SecureReceiverConnector {
    static Thread t_SecurityReceiverCoordinator;
    static Thread t_SynchronousMCWithReplyReceiver;    
    public static void aSecureReceiverConnector(){
        try {

            SynchronousMCWithReplyReceiver synchronousMCWithReplyReceiver = new SynchronousMCWithReplyReceiver();
            synchronousMCWithReplyReceiver.sendSecSync();
            t_SynchronousMCWithReplyReceiver = synchronousMCWithReplyReceiver.t_SynchronousMCWithReplyReceiver;

            SecurityReceiverCoordinator securityReceiverCoordinator = new SecurityReceiverCoordinator();

            securityReceiverCoordinator.sendSecSync();
            t_SecurityReceiverCoordinator = securityReceiverCoordinator.t_SecurityReceiverCoordinator;

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}

class SynchronousMCWithReplyReceiver implements Runnable {
    Thread t_SynchronousMCWithReplyReceiver;

    public void sendSecSync() throws Exception{

        t_SynchronousMCWithReplyReceiver = new Thread(this, "SynchronousMCWithReplyReceiver");
        t_SynchronousMCWithReplyReceiver.start();

    }

    public void run()
    {

        int i = 0;
        ByteMessage byteMessage = new ByteMessage();
        Response response= new Response();

        while(i<Global.input)
        {
            i++;
            byteMessage = Global.buff3.receive();
            response = Global.buff4.send(byteMessage);
            Global.buff3.reply(response);
        }

    }
}



class SecurityReceiverCoordinator implements Runnable
{
    Thread t_SecurityReceiverCoordinator;
    boolean result;

    public void sendSecSync(
) throws Exception{
        t_SecurityReceiverCoordinator = new Thread(this, "SecurityReceiverCoordinator");
        t_SecurityReceiverCoordinator.start();
        t_SecurityReceiverCoordinator = new Thread(this, "SecurityReceiverCoordinator");
        t_SecurityReceiverCoordinator.start();
 }
    public void run(){
        int i = 0;
        while(i<Global.input)
        {
            i++;

            KeyRequestMessage keyRequestMessage = new KeyRequestMessage();
            ByteMessage byteMessage = new ByteMessage();
            Message message = new Message();
            Response response= new Response();

            try {

                byteMessage = Global.buff4.receive();
                keyRequestMessage.messageName = "Request Key";

                message.messageContent = new String(byteMessage.messageContent);
                message.messageName = new String(byteMessage.messageName);
                response =Global.buff5.send(byteMessage);

                Global.buff4.reply(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
//added Main for secure receiver, other threads are commented 
public class SMCWR_TD_RPLY{ //Main

    public static void main(String args[]) throws Exception {
    
           //Parameters
            Global.input =10;    //programmer will decide input
    
            //CustomerComponent customerComponent = new CustomerComponent();
            //SecureSenderConnector.aSecureSenderConnector();
    
            SecureReceiverConnector.aSecureReceiverConnector();
    
    
            //RequisitionSever requisitionSever = new RequisitionSever();
            //customerComponent.t_CustomerComponent.join();
            //SecureSenderConnector.t_SecuritySenderCoordinator.join();
            //SecureSenderConnector.t_SynchronousMCWithReplySender.join();
            SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
            SecureReceiverConnector.t_SynchronousMCWithReplyReceiver.join();
            //requisitionSever.t_RequisitionSever.join();
         }
     }

