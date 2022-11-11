package com.servthree.serverthree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.net.URI;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.lang.*;
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
    //public static MBR1 buff1 = new MBR1(); //between sender component and security sender coordinator
    //public static MBR buff2 = new MBR(); // between sender coordinator and SMCWR sender
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




@RestController
@SpringBootApplication
public class ServerthreeApplication {
     @GetMapping("/securereceiver")
     public String home(){

         return "This is the secure receiver server";
         }

     //Handling post request
     //@PostMapping(path="/sendmessage",consumes = "any", produces = "application/octet-stream")
     @PostMapping("/sendmessage")
     public String insert(@RequestBody String ob)
          {
            try{
            Global.input =1;    //programmer will decide input    
            SecureReceiverConnector.aSecureReceiverConnector();
            SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
            SecureReceiverConnector.t_SynchronousMCWithReplyReceiver.join();
                               }catch(InterruptedException e)
                                  {
                                    System.out.println("InterruptedException caught");
                                                }
            try{
            RestTemplate resttemp = new RestTemplate();
            String baseurl = "http://127.0.0.1:80/sendmessage";
            URI uri = new URI(baseurl);
            ResponseEntity<String> result = resttemp.postForEntity(uri,ob,String.class);
            //String Status = new String(result.getStatusCodeValue());
            int Status = result.getStatusCodeValue();
            if (Status == 201){//check response results 
                    System.out.println("Status is: " +Status+"\n\n");
                    return "Data received. This is a response from server three";
                        }
                           } catch (Exception e) {
                                             e.printStackTrace();
                                         }
          // Storing the incoming data in the list
               //Data.add(new Details(ob.number, ob.name));
               //String message = ob.toString();
               //System.out.println("message received "+ob);
               return "This is server three";
                   }

	public static void main(String[] args) {
		SpringApplication.run(ServerthreeApplication.class, args);
	}

}
