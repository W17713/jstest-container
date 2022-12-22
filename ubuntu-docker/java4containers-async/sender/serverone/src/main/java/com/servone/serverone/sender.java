package com.servone.serverone;

import java.net.URI;
import java.net.URL;
import java.io.*;
import java.net.HttpURLConnection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.AbstractHttpMessageConverter; 
//import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.ws.transport.http.HttpUrlConnection;
import org.codehaus.jackson.map.ObjectMapper;


import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.util.*;

import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;

@SpringBootApplication
@RestController
public class sender {
   @GetMapping("/sender") 
   public String home() {
                      try{
                        //KeyGenerator keygen = KeyGenerator.getInstance("DES");
                        //SecretKey secretKey = keygen.generateKey();
                
                        //KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
                        //kpg.initialize(512); // 512 is the key size.
                
                        //KeyPair kp = kpg.generateKeyPair();
                        //PublicKey publicKey = kp.getPublic();
                        //PrivateKey privateKey = kp.getPrivate();
                
                       //Parameters
                        Global.input =10;    //programmer will decide input
                
                        Global.queueSize=25;  //program will decide queueSize
                        setSize setside= new setSize();
                        setside.setSize(Global.input,Global.queueSize);

                        //Request for keys
                        KeyRequest kr = new KeyRequest();
                        KeyMessageRequest kmr = new KeyMessageRequest();
                        //Keys keys = new Keys();
                       // String reqmessageName = "requestkeys";
                       // String keys  = kmr.post("http://127.0.0.1:8000/requestkey",reqmessageName);
                        String keys  = kmr.get("http://127.0.0.1:8000/requestkey");
                        System.out.println(keys);
                        //SenderComponent senderComponent = new SenderComponent(keys.secretKey, keys.privateKey);
                      
                        //senderComponent.t_senderComponent.join();
                    
                        return "This is the customer server";
                        }catch(Exception e){
                            String errorMessage = null;
                            errorMessage = e.getMessage();
                            return errorMessage;
                        }
                         }


	public static void main(String[] args) {
		SpringApplication.run(sender.class, args);
	}

}
/*
public class sender{ //Main

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
        SenderComponent senderComponent = new SenderComponent(secretKey, privateKey);
        //SecureSenderConnector.aSecureSenderConnector();

        //SecureReceiverConnector.aSecureReceiverConnector();

        PublicKeyRepository publicKeyRepository = new PublicKeyRepository(publicKey);
        ReceiverComponent receiverComponent = new ReceiverComponent(secretKey);
        senderComponent.t_senderComponent.join();
        //SecureSenderConnector.t_SecuritySenderCoordinator.join();
        //SecureSenderConnector.t_AsynchronousMCSender.join();
        //SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
        //SecureReceiverConnector.t_AsynchronousMCReceiver.join();
        //receiverComponent.t_receiverComponent.join();
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

class Keys {
    String secretKey = null;
    String publicKey = null;
    String privateKey = null;
}

class KeyRequest {
    String messageName = null;
}

class KeyMessageRequest{
    public String post(String posturl, String requestMsg){
        try{
            Gson gson = new Gson(); 
        URL url = new URL (posturl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String jsonInputString = null;
        if (requestMsg =="requestkeys"){
            jsonInputString = requestMsg;
        }else{
            jsonInputString = "json string later to be declared"; 
            //gson.toJson(krObj);
        }
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);			
        }

        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
                    }
            String rspString = response.toString();
            //String msg = gson.fromJson(rspString, KeyRequest.class);
            System.out.println(rspString);
            return rspString;
            }}catch(Exception e){
            String errorMessage = e.getMessage();
            System.out.println(errorMessage);
            return errorMessage;
            }
    }

    public String get(String geturl){
        try{
        URL url = new URL (geturl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        //HttpURLConnection connection = new URL(geturl).openConnection();
        InputStream response = connection.getInputStream();
        try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                System.out.println(responseBody);
                return responseBody;
        }
       // String rsp = response.toString();
        //return responseBody;
        }catch(Exception e){
            String errormsg=e.getMessage();
            System.out.println(errormsg);
            return errormsg;
        }
}
}

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

    //public synchronized void put(Message message) {
    public void put(Message message) {
        while (messageCount == maxCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        messageCount++;

        /*buffer[up].messageName = message.messageName;
        buffer[up].messageContent = message.messageContent;

        buffer[up].secretKey = message.secretKey;
        buffer[up].privateKey = message.privateKey;
        buffer[up].senderID = message.senderID;
        buffer[up].userRole = message.userRole;*/

        String encsecretKey = Base64.getEncoder().encodeToString(message.secretKey.getEncoded());
        String encprivateKey = Base64.getEncoder().encodeToString(message.privateKey.getEncoded());

        JSONObject msgobj = new JSONObject();
        msgobj.put("messageName", message.messageName);
        msgobj.put("messageContent", message.messageContent);
        msgobj.put("secretKey", encsecretKey);
        msgobj.put("privateKey", encprivateKey);
        msgobj.put("senderID", message.senderID);
        msgobj.put("userRole", message.userRole);


        up = (up + 1) % maxCount; // to place next item in

        if (messageCount == 1){
            //notify(); //change notify function to http post function
            try{
                RestTemplate resttemp = new RestTemplate();
                //MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
                //AbstractHttpMessageConverter converter = new AbstractHttpMessageConverter();
                //converter.setObjectMapper(new ObjectMapper());
                //resttemp.getMessageConverters().add(converter);
                String baseurl = "http://127.0.0.1:3000/sendmessage";
                URI uri = new URI(baseurl);
                ResponseEntity<String> result = resttemp.postForEntity(uri,msgobj,String.class);
                String rsp = new String();
                rsp=result.getBody();
                System.out.println(rsp);
                int Status = result.getStatusCodeValue();
                if (Status == 201){//check response results 
                        System.out.println("Status is: " +Status+"\n\n");
                            }
            }catch(Exception e){
                 String errorMessage = null;
                 errorMessage = e.getMessage();
                 System.out.println(errorMessage);
                 }
        }
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
}

class ByteMessage {
        byte[] messageName = null;
        byte[] messageContent = null;

        byte[] senderID=null;
        byte[] userRole=null;
        byte[] signature = null;
        byte[] hashedValue = null;

}

class ByteMessageQueue {

        private int maxCount;
        private int messageCount = 0;
        private ByteMessage[] buffer;
        private int bottom = 0, up = 0;

        ByteMessageQueue(int queueSize) {
                                    this.maxCount=queueSize;
                                            buffer = new ByteMessage[queueSize];
                                                    for (int i = 0; i < maxCount; i++){
                                                                    buffer[i] = new ByteMessage();
                                                                            }
                                                                                }
                        public synchronized void put(ByteMessage byteMessage) {
                                    while (messageCount == maxCount) {
                                                    try {
                                                                        wait();
                                                                                    } catch (InterruptedException e) {
                                                                                                        e.printStackTrace();
                                                                                                                    }
                                                                                                                            }
                         messageCount++;

                                 buffer[up].messageName = byteMessage.messageName; // place message in buffer;
                                         buffer[up].messageContent = byteMessage.messageContent;

                                                 buffer[up].senderID = byteMessage.senderID;
                                                         buffer[up].userRole = byteMessage.userRole;
                                                                 buffer[up].signature = byteMessage.signature;
                                                                         buffer[up].hashedValue = byteMessage.hashedValue;
                         up = (up + 1) % maxCount; // to place next item in

                                 if (messageCount == 1)
                                                 notify();
                                                     }
                         public synchronized ByteMessage get() {
                                     ByteMessage byteMessage;
                                             while (messageCount == 0)
                                                             try {
                                                                                 wait();
                                                                                             } catch (InterruptedException e) {
                                                                                                                 e.printStackTrace();
                                                                                                                             }
                                                                          byteMessage = buffer[bottom];
                                                                                                                                             bottom = (bottom + 1) % maxCount;
                                                                                                                                                     --messageCount;
                                                                                                                                                             if (messageCount == maxCount - 1)
                                                                                                                                                                             notify();
                                                                                                                                                                                     return byteMessage;
                                                                                                                                                                                         }
}
/*
class setSize{
    Global global = new Global();

   public void setSize(int a,int b) {

        Global.senderComponentQueue = new MessageQueue(b);
        Global.q2 = new ByteMessageQueue(b);
        Global.q3 = new ByteMessageQueue(b);
        Global.q4 = new ByteMessageQueue(b);
        //Global.receiverComponentQueue = new MessageQueue(b);
    }
}*/

class Global{

    public static MessageQueue senderComponentQueue;
    public static ByteMessageQueue q2;
    public static ByteMessageQueue q3;
    public static ByteMessageQueue q4;
    //public static MessageQueue receiverComponentQueue;

    //public static MBRSecretKey mbrSecretKey = new MBRSecretKey();
    //public static MBRPublicKey mbrPublicKey = new MBRPublicKey();
    public static int input;
    public static int queueSize;
}

 class setSize{
        Global global = new Global();
     
        public void setSize(int a,int b) {
       
               Global.senderComponentQueue = new MessageQueue(b);
               Global.q2 = new ByteMessageQueue(b);
               // Global.q3 = new ByteMessageQueue(b);
               //Global.q4 = new ByteMessageQueue(b);
               //Global.receiverComponentQueue = new MessageQueue(b);
           }
        }

class SenderComponent  implements Runnable {
    Thread t_senderComponent;
    SecretKey secretKey;
    PrivateKey privateKey;
    SenderComponent(SecretKey sk, PrivateKey privateKey)    {
        t_senderComponent = new Thread(this, "SenderComponent");
        t_senderComponent.start();
        this.secretKey = sk;
        this.privateKey = privateKey;
    }
    public void run()
    {
        int i = 0;

        Message message = new Message();
        while(i<Global.input)
        {
            i++;
            message.messageName = new String("ConfirmShipment "+ i);
            message.messageContent = new String("ShipmentConfirmation " + i);

            message.secretKey = secretKey;
            message.senderID="michaelshin cs5332";
            message.userRole="michaelshin admin";
            message.privateKey = privateKey;
            System.out.println("SenderComponent: messageName = " + message.messageName);
            System.out.println("SenderComponent: messageContent = " + message.messageContent + "\n");

            Global.senderComponentQueue.put(message);
        }
    }
}

