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
            {//bf
           // System.out.println("about to wait");
                wait();
             //af
            //System.out.println("finished waiting");
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
            {//System.out.println("before wait");
                wait();
              //System.out.println("after wait");
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
    //added string global for communication between server 3 and server 2
    public static String rsp = new String();

}


class SecureReceiverConnector {
    static Thread t_SecurityReceiverCoordinator;
    static Thread t_SynchronousMCWithReplyReceiver;    
    public static void aSecureReceiverConnector(String msg){
        try {

            SynchronousMCWithReplyReceiver synchronousMCWithReplyReceiver = new SynchronousMCWithReplyReceiver();
            synchronousMCWithReplyReceiver.sendSecSync();
            t_SynchronousMCWithReplyReceiver = synchronousMCWithReplyReceiver.t_SynchronousMCWithReplyReceiver;

            SecurityReceiverCoordinator securityReceiverCoordinator = new SecurityReceiverCoordinator(msg);

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
        //message.messageContent=this.msg;
        //System.out.println("Message content received on secure receiver server is: " +message.messageContent+"\n\n");
        
        //byteMessage = Global.buff4.receive();
        while(i<Global.input)
        {
            i++;
           // try {
                                 //byteMessage.messageContent = (message.messageContent).getBytes();
                                 //byteMessage.messageName = ("message.messageName").getBytes()    ;
                                 //Reply start from here!//
                                 //response = Global.buff2.send(byteMessage);
                                 //response = Global.buff4.send(byteMessage);
                
                                 //Global.buff1.reply(response);
                                 byteMessage = Global.buff4.receive();
                                   try {
                                            String messagethree= new String(byteMessage.messageContent);
                                            //Added byte to string conversion
                                            //String messagethree= byteMessage.toString();
                                            //response = Global.buff3.send(byteMessage); //replace with     post request
                                            RestTemplate resttemp = new RestTemplate();
                                            String baseurl = "http://127.0.0.1:80/sendmessage";
                                            URI uri = new URI(baseurl);
                                            ResponseEntity<String> result = resttemp.postForEntity(uri,messagethree,String.class);
                                            //String Status = new String(result.getStatusCodeValue());
                                            //System.out.println("Message sent");
                                           int Status = result.getStatusCodeValue();
                                           Global.rsp = result.getBody();
                                           System.out.println(Global.rsp);
                                           if (Status == 201){//check response results 
                                                System.out.println("Status is: " +Status+"\n\n");
                                                
                                                    //return "Data received. This is a response from ser    ver two";
                                                   // Global.buff4.reply(response);
                                                    //return result.body;
                                                    }

                 
                  }catch (Exception e) {
                                     e.printStackTrace();
                                 }
            //byteMessage = Global.buff3.receive();
            //byteMessage.toString()
            //response = Global.buff4.send(byteMessage);
            //Global.buff3.reply(response);
        }

    }
}



class SecurityReceiverCoordinator implements Runnable
{
    //added
    String msg = new String();
    public SecurityReceiverCoordinator(String msg){
                                   this.msg = msg;
                               }

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
        Global.input=1;
        //message.messageContent=this.msg;
        //System.out.println("Message content received on secure receiver server is: " +message.messageContent+"\n\n");
        while(i<Global.input)
        {
            i++;

            KeyRequestMessage keyRequestMessage = new KeyRequestMessage();
            ByteMessage byteMessage = new ByteMessage();
            Message message = new Message();
            Response response= new Response();

            message.messageContent=this.msg;
            System.out.println("Message content received on secure receiver server is: " +message.messageContent+"\n\n");
            //System.out.println("Message content received on secure receiver server is: " +this.msg+"\n\n");

            try {

                //byteMessage = Global.buff4.receive();
                keyRequestMessage.messageName = "Request Key";
                /*commented*/
                //message.messageContent = new String(byteMessage.messageContent);
                //message.messageName = new String(byteMessage.messageName);
                //response =Global.buff5.send(byteMessage);
                /*Added*/
                byteMessage.messageContent = (message.messageContent).getBytes();
                //byteMessage.messageName = (message.messageName).getBytes();
                byteMessage.messageName = ("message.messageName").getBytes();
                response =Global.buff4.send(byteMessage);
                /*RestTemplate resttemp = new RestTemplate();
                String baseurl = "http://127.0.0.1:80/sendmessage";
                URI uri = new URI(baseurl);
                ResponseEntity<String> result = resttemp.postForEntity(uri,message.messageContent,String.class);
                //String Status = new String(result.getStatusCodeValue());
                int Status = result.getStatusCodeValue();
                if (Status == 201){//check response results 
                                         System.out.println("Status is: " +Status+"\n\n");
                                         return "Data received. This is a response from server three";
                                    }*/
                //Global.buff4.reply(response);
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
            SecureReceiverConnector.aSecureReceiverConnector(ob);
            SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
            SecureReceiverConnector.t_SynchronousMCWithReplyReceiver.join();
                               }catch(InterruptedException e)
                                  {
                                    System.out.println("InterruptedException caught");
                                                }
            /*try{
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
                                         }*/
          // Storing the incoming data in the list
               //Data.add(new Details(ob.number, ob.name));
               //String message = ob.toString();
               //System.out.println("message received "+ob);
              return Global.rsp;
                   }

	public static void main(String[] args) {
		SpringApplication.run(ServerthreeApplication.class, args);
	}

}
