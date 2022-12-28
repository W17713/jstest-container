package com.servfour.serverfour;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity; 

import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;


@SpringBootApplication
@RestController
public class receiver {
   @GetMapping("/receiver") 
   public String home() {
                      try{
                            return "This is the receiver server";
                        }catch(Exception e){
                            String errorMessage = null;
                            errorMessage = e.getMessage();
                            return errorMessage;
                        }
                         }
    
    @PostMapping("/sendmessage")
    public String insert(@RequestBody String ob)
          {
              try{
                //System.out.println(ob);
                //KeyGenerator keygen = KeyGenerator.getInstance("DES");
                //SecretKey secretKey = keygen.generateKey();
                
                //SecretKey secretKey = ob.secretKey;

                //KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
                //kpg.initialize(512); // 512 is the key size.
        
                //KeyPair kp = kpg.generateKeyPair();
                //PublicKey publicKey = kp.getPublic();
                //PrivateKey privateKey = kp.getPrivate();
        
               //Parameters
                Global.input =1;    //programmer will decide input
        
                Global.queueSize=25;  //program will decide queueSize
                setSize setside= new setSize();
                setside.setSize(Global.input,Global.queueSize);
                //SenderComponent senderComponent = new SenderComponent(secretKey, privateKey);
                //SecureSenderConnector.aSecureSenderConnector();
        
                //SecureReceiverConnector.aSecureReceiverConnector();
        
                //PublicKeyRepository publicKeyRepository = new PublicKeyRepository(publicKey);
                //ReceiverComponent receiverComponent = new ReceiverComponent(secretKey);
                //System.out.println(ob);
                

               ReceiverComponent receiverComponent = new ReceiverComponent(ob);
                //senderComponent.t_senderComponent.join();
                //SecureSenderConnector.t_SecuritySenderCoordinator.join();
                //SecureSenderConnector.t_AsynchronousMCSender.join();
                //SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
                //SecureReceiverConnector.t_AsynchronousMCReceiver.join();
                receiverComponent.t_receiverComponent.join();
                //publicKeyRepository.t_PublicKeyRepository.join();
                         }catch(InterruptedException e)
                         {
                                 System.out.println("InterruptedException caught");
                             }
                     return "Reply form a Receiver: Status Success";
                   }


	public static void main(String[] args) {
		SpringApplication.run(receiver.class, args);
	}

}
/*
public class receiver{ //Main

public static void main(String args[]) throws Exception {

        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey secretKey = keygen.generateKey();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
        kpg.initialize(512); // 512 is the key size.

        KeyPair kp = kpg.generateKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();

       //Parameters
        Global.input =10;    //programmer will decide input

        Global.queueSize=25;  //program will decide queueSize
        setSize setside= new setSize();
        setside.setSize(Global.input,Global.queueSize);
        //SenderComponent senderComponent = new SenderComponent(secretKey, privateKey);
        //SecureSenderConnector.aSecureSenderConnector();

        //SecureReceiverConnector.aSecureReceiverConnector();

        PublicKeyRepository publicKeyRepository = new PublicKeyRepository(publicKey);
        ReceiverComponent receiverComponent = new ReceiverComponent(secretKey);
        //senderComponent.t_senderComponent.join();
        //SecureSenderConnector.t_SecuritySenderCoordinator.join();
        //SecureSenderConnector.t_AsynchronousMCSender.join();
        //SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
        //SecureReceiverConnector.t_AsynchronousMCReceiver.join();
        receiverComponent.t_receiverComponent.join();
        publicKeyRepository.t_PublicKeyRepository.join();

}
}*/

class Message {
    String messageName = null;
    String messageContent = null;

    SecretKey secretKey = null;
    String senderID=null;
    String userRole=null;
    PrivateKey privateKey = null;
}

class StringBMessage {
    String messageName = null;
    String messageContent = null;
        
    String senderID=null;
    String userRole=null;
    String signature = null;
    String hashedValue = null;
                 }

/*
class PublicKeyRepository implements Runnable{

    Thread t_PublicKeyRepository;
    PublicKey publicKey;

    public PublicKeyRepository(PublicKey publicKey) throws Exception
    {
        t_PublicKeyRepository = new Thread(this, "PublicKeyRepository");
        t_PublicKeyRepository.start();
        this.publicKey = publicKey;

    }
    public void run() {
        int i = 0;
        KeyRequestMessage keyRequestMessage = new KeyRequestMessage();

        while(i<Global.input)
        {
            i++;

            keyRequestMessage = Global.mbrPublicKey.receive();

            if (keyRequestMessage.messageName.equals("Request Key"))
            {
                Global.mbrPublicKey.reply(publicKey);
            }
        }
    }

}*/
/*
class MessageQueue { //Message Queue using Message class

    private int maxCount;
    private int messageCount = 0;
    private Message[] buffer;
    private int bottom = 0, up = 0;

    MessageQueue(int queueSize) {
        this.maxCount=queueSize;
        buffer = new Message[queueSize];
        for (int i = 0; i < maxCount; i++){
            buffer[i] = new Message();
        }
    }
    public synchronized void put(Message message) {
        while (messageCount == maxCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        messageCount++;

        buffer[up].messageName = message.messageName;
        buffer[up].messageContent = message.messageContent;

        buffer[up].secretKey = message.secretKey;
        buffer[up].privateKey = message.privateKey;
        buffer[up].senderID = message.senderID;
        buffer[up].userRole = message.userRole;

        up = (up + 1) % maxCount; // to place next item in

        if (messageCount == 1)
            notify();
    }

    /*public synchronized Message get() {
        Message message;
        while (messageCount == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        message = buffer[bottom]; // remove message from buffer;
        bottom = (bottom + 1) % maxCount; // to fetch next item from
        --messageCount;
        if (messageCount == maxCount - 1)
            notify();
        return message;
    }*/
    /*public Message get(Message msg) {
        Message message;
        message = new Message();
        //byte[] decodedSecretKey = Base64.getDecoder().decode(msg.secretKey);
        //byte[] decodedPrivateKey = Base64.getDecoder().decode(msg.privateKey);
        //SecretKey secretKey = new SecretKeySpec(decodedSecretKey, 0, decodedKey.length, "AES"); 
        //SecretKey privateKey = new SecretKeySpec(decodedPrivateKey, 0, decodedKey.length, "AES"); 

        message.messageName = msg.messageName;
        message.messageContent = msg.messageContent;

        message.secretKey = msg.secretKey;
        message.privateKey = msg.privateKey;
        message.senderID = msg.senderID;
        message.userRole = msg.userRole;
        
        while (messageCount == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        message = buffer[bottom]; // remove message from buffer;
        bottom = (bottom + 1) % maxCount; // to fetch next item from
        --messageCount;
        if (messageCount == maxCount - 1)
            notify();
        return message;
    }
}*/

class MBRSecretKey {  //Message Buffer and Response using KeyRequestMessage class, return secret key

    private SecretKey key;
    private KeyRequestMessage keyRequestMessage;
    private boolean messageBufferFull = false;
    private boolean responseBufferFull = false;

        synchronized SecretKey send(KeyRequestMessage keyRequestMessage)
            {
                 this.keyRequestMessage = keyRequestMessage;
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
                    return key;
               }

            synchronized KeyRequestMessage receive()
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
                              return keyRequestMessage;
                              }

                 synchronized void reply(SecretKey secretKey)
                     {
                                 this.key = secretKey;
                                         responseBufferFull = true;
                                                 notify();
                                                     }
}

class KeyRequestMessage {
    String messageName = null;
}

class setSize{
    Global global = new Global();

   public void setSize(int a,int b) {

        //Global.senderComponentQueue = new MessageQueue(b);
        //Global.q2 = new ByteMessageQueue(b);
        //Global.q3 = new ByteMessageQueue(b);
        //Global.q4 = new ByteMessageQueue(b);
       // Global.receiverComponentQueue = new MessageQueue(b);
    }
}

class Global{

   // public static MessageQueue senderComponentQueue;
   // public static ByteMessageQueue q2;
   // public static ByteMessageQueue q3;
   // public static ByteMessageQueue q4;
    //public static MessageQueue receiverComponentQueue;

    //public static MBRSecretKey mbrSecretKey = new MBRSecretKey();
    //public static MBRPublicKey mbrPublicKey = new MBRPublicKey();
    public static int input;
    public static int queueSize;
}


class ReceiverComponent implements Runnable
{
    
    StringBMessage message = new StringBMessage();
    SecretKey secretKey;
    public ReceiverComponent(String msg)
    {
        t_receiverComponent = new Thread(this, "ReceiverComponent");
        t_receiverComponent.start();
        //this.secretKey = message.secretKey;
        Gson gson = new Gson();
        
        this.message = gson.fromJson(msg,StringBMessage.class);
        
    }

    Thread t_receiverComponent;

    public void run()
    {
        int i = 0;
        StringBMessage message = new StringBMessage();
        //KeyRequestMessage keyRequestMessage = new KeyRequestMessage();

        while(i<Global.input)
        {
            i++;
                
            message = this.message;
            /*keyRequestMessage = Global.mbrSecretKey.receive();

            if (keyRequestMessage.messageName.equals("Request Key"))
            {
                Global.mbrSecretKey.reply(secretKey);
            }*/


            //message = Global.receiverComponentQueue.get();
            System.out.println("ReceiverComponent: messageName = " + message.messageName);
            System.out.println("ReceiverComponent: messageContent = " + message.messageContent + "\n");

        }
    }
}



