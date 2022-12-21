package com.servfive.serverfive;

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

import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;

import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;


@SpringBootApplication
@RestController
public class key {
   @GetMapping("/key") 
   public String home() {
                      try{
                        KeyGenerator keygen = KeyGenerator.getInstance("DES");
                        SecretKey secretKey = keygen.generateKey();
                
                        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
                        kpg.initialize(512); // 512 is the key size.
                
                        KeyPair kp = kpg.generateKeyPair();
                        PublicKey publicKey = kp.getPublic();
                        PrivateKey privateKey = kp.getPrivate();

                        String encsecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
                        String encpublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
                        String encprivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

                        Gson gson = new Gson();

                        Keys keys = new Keys();
                        keys.secretKey = encsecretKey;
                        keys.publicKey = encpublicKey;
                        keys.privateKey= encprivateKey;
                        Global.keysJsonString = gson.toJson(keys);
                        System.out.println(Global.keysJsonString);
                
                        //convert keys to string and return
        
                        //PublicKeyRepository publicKeyRepository = new PublicKeyRepository(publicKey);
                        //ReceiverComponent receiverComponent = new ReceiverComponent(secretKey);
                        
                        return "This is the key genrator server";
                        }catch(Exception e){
                            String errorMessage = null;
                            errorMessage = e.getMessage();
                            return errorMessage;
                        }
                         }
    
    @GetMapping("/requestkey")
    //public String insert(@RequestBody String ob)
    public String getkeys()
          {
              try{/*
                  System.out.println(ob);
                  String rk = new String("requestkeys");
                  System.out.println(rk);
                if (ob == rk){
                    return Global.keysJsonString;
                }else{
                    return "Check request name";
                }*/
                    return Global.keysJsonString;
                         }catch(Exception e)
                         {
                                 System.out.println("Exception caught");
                                 String errorMessage = null;
                                 errorMessage = e.getMessage();
                                 return errorMessage;
                             }
                   }


	public static void main(String[] args) {
		SpringApplication.run(key.class, args);
	}

}


class Global{
    /*public static MessageQueue senderComponentQueue;
    public static ByteMessageQueue q2;
    public static ByteMessageQueue q3;
    public static ByteMessageQueue q4;
    public static MessageQueue receiverComponentQueue;

    public static MBRSecretKey mbrSecretKey = new MBRSecretKey();
    public static MBRPublicKey mbrPublicKey = new MBRPublicKey();
    public static int input;
    public static int queueSize;*/
    public static String keysJsonString;
}

class Keys {
    String secretKey = null;
    String publicKey = null;
    String privateKey = null;
}
/*
class Message {
    String messageName = null;
    String messageContent = null;

    SecretKey secretKey = null;
    String senderID=null;
    String userRole=null;
    PrivateKey privateKey = null;
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

    public synchronized Message get() {
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
    }
    public Message get(StringMessage msg) {
        Message message;
        message = new Message();
        byte[] decodedSecretKey = Base64.getDecoder().decode(msg.secretKey);
        byte[] decodedPrivateKey = Base64.getDecoder().decode(msg.privateKey);
        SecretKey secretKey = new SecretKeySpec(decodedSecretKey, 0, decodedKey.length, "AES"); 
        SecretKey privateKey = new SecretKeySpec(decodedPrivateKey, 0, decodedKey.length, "AES"); 

        message.messageName = message.messageName;
        message.messageContent = message.messageContent;

        message.secretKey = message.secretKey;
        message.privateKey = message.privateKey;
        message.senderID = message.senderID;
        message.userRole = message.userRole;
        
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

class KeyRequestMessage {
    String messageName = null;
}

/*class setSize{
    Global global = new Global();

   public void setSize(int a,int b) {

        Global.senderComponentQueue = new MessageQueue(b);
        Global.q2 = new ByteMessageQueue(b);
        Global.q3 = new ByteMessageQueue(b);
        Global.q4 = new ByteMessageQueue(b);
        Global.receiverComponentQueue = new MessageQueue(b);
    }
}*/


