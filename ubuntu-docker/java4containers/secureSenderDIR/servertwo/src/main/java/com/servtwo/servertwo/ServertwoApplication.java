package com.servtwo.servertwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

class Message {
    String messageName = null;
    String messageContent = null;

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
    public static MBR buff2 = new MBR(); // between sender coordinator and SMCWR sender
    public static MBR buff3 = new MBR(); //between SMCWR sender and SMCWR receiver
    //public static MBR buff4 = new MBR(); //between SMCWR receiver and security receiver coordinator
    //public static MBR buff5 = new MBR(); //to send byteMessage between security receiver coordinator and receiver component
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
                //response = Global.buff3.send(byteMessage); //replace with post request
                RestTemplate resttemp = new RestTemplate();
                String baseurl = "http://127.0.0.1:8080/sendmessagetwo";
                URI uri = new URI(baseurl);
                ResponseEntity<String> result = resttemp.postForEntity(uri,byteMessage,String.class);
                String Status = new String(result.getStatusCodeValue());
                if (Status == 201){//check response results 
                    System.out.println("Status is: " +Status+"\n\n");
                        //return "Data received. This is a response from server two";
                        Global.buff2.reply(response);
                        }

                //Global.buff2.reply(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }}

@RestController
@SpringBootApplication
public class ServertwoApplication {
     @GetMapping("/securesender")
     public String home(){

         return "This is the secure sender server";
         }

     //Handling post request
     //@PostMapping(path="/sendmessage",consumes = "any", produces = "application/octet-stream")
     @PostMapping("/sendmessage")
     public String insert(@RequestBody String ob)
          {
            SecureSenderConnector.aSecureSenderConnector(ob);
            SecureSenderConnector.t_SecuritySenderCoordinator.join();
            SecureSenderConnector.t_SynchronousMCWithReplySender.join();

            /*RestTemplate resttemp = new RestTemplate();
            String baseurl = "http://127.0.0.1:80/sendmessage";
            URI uri = new URI(baseurl);
            ResponseEntity<String> result = resttemp.postForEntity(uri,ob,String.class);
            String Status = new String(result.getStatusCodeValue());
            if (Status == 201){//check response results 
                    System.out.println("Status is: " +Status+"\n\n");
                    return "Data received. This is a response from server two";
                        }*/
          // Storing the incoming data in the list
               //Data.add(new Details(ob.number, ob.name));
               //String message = ob.toString();
               //System.out.println("message received "+ob);
               //return "Data received. This is a response from server two";
                   }

	public static void main(String[] args) {
		SpringApplication.run(ServertwoApplication.class, args);
	}

}
