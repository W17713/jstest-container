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
class SecureSenderConnector {

    static Thread t_SecuritySenderCoordinator;
    static Thread t_SynchronousMCWithReplySender;

    static void aSecureSenderConnector(String msg){
        try {

            SecuritySenderCoordinator securitySenderCoordinator = new SecuritySenderCoordinator(msg);
            securitySenderCoordinator.sendSecSync(
);
            t_SecuritySenderCoordinator = securitySenderCoordinator.t_SecuritySenderCoordinator;

            SynchronousMCWithReplySender synchronousMCWithReplySender = new SynchronousMCWithReplySender();
            synchronousMCWithReplySender.sendSecSync();
            t_SynchronousMCWithReplySender = synchronousMCWithReplySender.t_SynchronousMCWithReplySender;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

class SecuritySenderCoordinator implements Runnable {

    Thread t_SecuritySenderCoordinator;

    boolean resultRply;

    //added
    public SecuritySenderCoordinator(String msg){
        this.msg = msg;
    }
    public void sendSecSync(
)throws Exception
    {
        t_SecuritySenderCoordinator = new Thread(this, "SecuritySenderCoordinator");
        t_SecuritySenderCoordinator.start();
    }
    public void run()
    {
        int i=0;
        Message message = new Message();
        ByteMessage byteMessage = new ByteMessage();
        KeyRequestMessage keyRequestMessage = new KeyRequestMessage();
        Response response =new Response();
        while(i<Global.input)
        {
            i++;

            //message = Global.buff1.receive();
            message=this.msg; //message received from server one

            try {
                byteMessage.messageContent = (message.messageContent).getBytes();
                //byteMessage.messageName = (message.messageName).getBytes();
                //Reply start from here!//
                response = Global.buff2.send(byteMessage);

                //Global.buff1.reply(response);

 }catch (Exception e) {
                e.printStackTrace();
            }        }
    }
}
class SynchronousMCWithReplySender implements Runnable {
    Thread t_SynchronousMCWithReplySender;

    public void sendSecSync() throws Exception{
        t_SynchronousMCWithReplySender = new Thread(this, "SynchronousMCWithReplySender");
        t_SynchronousMCWithReplySender.start();
    }

    public void run()
    {
        ByteMessage byteMessage = new ByteMessage();
        Response response = new Response();
        int i=0;
        while(i<Global.input)
        {
            i++;

            byteMessage = Global.buff2.receive();

            try {
                response = Global.buff3.send(byteMessage); //replace with post request

                Global.buff2.reply(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }}
//added Main for secure sender, other threads are commented 
public class secureSender{ //Main
    public static void main(String args[]) throws Exception {

        //Parameters
         //Global.input =10;    //programmer will decide input
 
         //CustomerComponent customerComponent = new CustomerComponent();
         SecureSenderConnector.aSecureSenderConnector();
 
         //SecureReceiverConnector.aSecureReceiverConnector();
 
 
         //RequisitionSever requisitionSever = new RequisitionSever();
         //customerComponent.t_CustomerComponent.join();
         SecureSenderConnector.t_SecuritySenderCoordinator.join();
         SecureSenderConnector.t_SynchronousMCWithReplySender.join();
         //SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
         //SecureReceiverConnector.t_SynchronousMCWithReplyReceiver.join();
         //requisitionSever.t_RequisitionSever.join();
      }
  }
