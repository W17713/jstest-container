package com.servthree.serverthree;

import java.net.URI;
import java.net.URL;
import java.io.*;
import java.net.HttpURLConnection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity; 

import org.springframework.ws.transport.http.HttpUrlConnection;

import java.security.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;


import java.net.Authenticator;
import java.net.PasswordAuthentication;

import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;


@RestController
@SpringBootApplication
public class receiverConnector {
     @GetMapping("/securereceiver")
     public String home(){

         return "This is the secure receiver server";
         }

     //Handling post request
     //@PostMapping(path="/sendmessage",consumes = "any", produces = "application/octet-stream")
//public String insert(@RequestBody Map<String, Object> ob)

     @PostMapping("/sendmessage")
     public String insert(@RequestBody String ob)
          {
            try{
            //Global.input =10;    //programmer will decide input
            Global.input =10;    //programmer will decide input
             
            Global.queueSize=25;  //program will decide queueSize
            setSize setside= new setSize();
            setside.setSize(Global.input,Global.queueSize);
                    
            System.out.println(ob);
            SecureReceiverConnector.aSecureReceiverConnector(ob);
            SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
            SecureReceiverConnector.t_AsynchronousMCReceiver.join();
                               }catch(InterruptedException e)
                                  {
                                    System.out.println("InterruptedException caught");
                                                }
              return "response";
                   }

	public static void main(String[] args) {
		SpringApplication.run(receiverConnector.class, args);
	}

}

/*class Global{
     
         public static MessageQueue senderComponentQueue;
         public static ByteMessageQueue q2;
         public static ByteMessageQueue q3;
         public static ByteMessageQueue q4;
      //public static MessageQueue receiverComponentQueue;
     
        //public static MBRSecretKey mbrSecretKey = new MBRSecretKey();
        //public static MBRPublicKey mbrPublicKey = new MBRPublicKey();
         public static int input;
         public static int queueSize;
    }*/
    
 class setSize{
                 Global global = new Global();
         
                public void setSize(int a,int b) {
             
                            //Global.senderComponentQueue = new MessageQueue(b);
                            //Global.q2 = new ByteMessageQueue(b);
                            // Global.q3 = new ByteMessageQueue(b);
                            Global.q4 = new ByteMessageQueue(b);
                            Global.receiverComponentQueue = new MessageQueue(b);
                        }
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

class Message {
    String messageName = null;
    String messageContent = null;

    SecretKey secretKey = null;
    String senderID=null;
    String userRole=null;
    PrivateKey privateKey = null;
}

class StringMessage {
    String messageName = null;
    String messageContent = null;
         
    String secretKey = null;
    String senderID=null;
    String userRole=null;
    String privateKey = null;
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

        //String encsecretKey = Base64.getEncoder().encodeToString(message.secretKey.getEncoded());
        //String encprivateKey = Base64.getEncoder().encodeToString(message.privateKey.getEncoded());

        JSONObject msgobj = new JSONObject();
        msgobj.put("messageName", message.messageName);
        msgobj.put("messageContent", message.messageContent);
       // msgobj.put("hashedValue", message.hashedValue);
        //msgobj.put("signature", message.signature);
        msgobj.put("senderID", message.senderID);
        msgobj.put("userRole", message.userRole);


        up = (up + 1) % maxCount; // to place next item in

        if (messageCount == 1){
            //notify(); //change notify function to http post function
            try{
                RestTemplate resttemp = new RestTemplate();
                String baseurl = "http://127.0.0.1:80/sendmessage";
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

    public String post(Message requestMsg){
        try{
            String posturl = "http://127.0.0.1:80/sendmessage";
            Gson gson = new Gson(); 
            StringMessage strrequestMsg = new StringMessage();
        URL url = new URL (posturl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String jsonInputString = null;

        String encsecretKey = Base64.getEncoder().encodeToString(requestMsg.secretKey.getEncoded());
        
        String encprivateKey = Base64.getEncoder().encodeToString(requestMsg.privateKey.getEncoded());
        strrequestMsg.privateKey = encprivateKey;
        strrequestMsg.secretKey = encsecretKey;
        strrequestMsg.messageName = requestMsg.messageName;
        strrequestMsg.messageContent = requestMsg.messageContent;
        strrequestMsg.senderID = requestMsg.senderID;
        strrequestMsg.userRole = requestMsg.userRole;
        jsonInputString = gson.toJson(strrequestMsg);
        
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

    public StringMessage get(Message msg) {

        StringMessage message;
        message = new StringMessage();

        String secretKey = Base64.getEncoder().encodeToString(msg.secretKey.getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(msg.privateKey.getEncoded());
         
        message.messageName = msg.messageName;
        message.messageContent = msg.messageContent;
         
        message.secretKey = secretKey;
        message.privateKey = privateKey;
        message.senderID = msg.senderID;
        message.userRole = msg.userRole;

        /*
        message.messageName = msg.messageName;
        message.messageContent = msg.messageContent;

        message.secretKey = msg.signature;
        message.privateKey = msg.hashedValue;
        message.senderID = msg.senderID;
        message.userRole = msg.userRole;
        */
        return message;
    }
}

//added to store byteMessage as strings
class StringBMessage {
         String messageName = null;
         String messageContent = null;
     
         String senderID=null;
         String userRole=null;
         String signature = null;
         String hashedValue = null;
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

class KeyRequestMessage {
    String messageName = null;
}


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


class MBRPublicKey {  //Message Buffer and Response using KeyRequestMessage class, return public key

    private PublicKey key;
    private KeyRequestMessage keyRequestMessage;
    private boolean messageBufferFull = false;
    private boolean responseBufferFull = false;


    synchronized PublicKey send(KeyRequestMessage keyRequestMessage)
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
    synchronized void reply(PublicKey publicKey)
    {
        this.key = publicKey;
        responseBufferFull = true;
        notify();
    }
}


class Global{

   // public static MessageQueue senderComponentQueue;
    //public static ByteMessageQueue q2;
    //public static ByteMessageQueue q3;
    //public static ByteMessageQueue q4;
    public static ByteMessageQueue q4;
    public static MessageQueue receiverComponentQueue;

    public static MBRSecretKey mbrSecretKey = new MBRSecretKey();
    public static MBRPublicKey mbrPublicKey = new MBRPublicKey();
    public static int input;
    public static int queueSize;
}
//Hashing_feature to check Integrity
        class hashValueGenration {
            public byte[] generate(byte[] msg) throws Exception {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(msg);
                byte[] mdbytes = md.digest();
                return (mdbytes);
            }
        }
        class hashValueVerification {
            public Boolean verify(byte[] hashValue, byte[] msg) throws Exception {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(msg);
                byte[] mdBytes = md.digest();

                if (MessageDigest.isEqual(hashValue, mdBytes))
                    return true;
                else
                    return false;
            }
        }
//Symmetric Encryption with DES
        class EncryptionEncryptor {
            Cipher c;

            byte[] encrypt(byte plainText[], SecretKey sk) throws Exception
            {
                try{
                    c = Cipher.getInstance("DES/ECB/PKCS5Padding");
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                c.init(Cipher.ENCRYPT_MODE, sk);
                return c.doFinal(plainText);
            }
        }

//Symmetric Encryption with DES
        class EncryptionDecryptor {
            Cipher c;

            byte[] decrypt(byte cyperText[], SecretKey sk) throws Exception
            {
                try{
                    c = Cipher.getInstance("DES/ECB/PKCS5Padding");
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                c.init(Cipher.DECRYPT_MODE, sk);
                return c.doFinal(cyperText);
            }
        }

//Digital Signature
        class DigitalSignatureSigner {
            public byte[] sign(byte[] plainText, PrivateKey privateKey) throws Exception
            {
                Signature signature = Signature.getInstance("SHAwithDSA");
                signature.initSign(privateKey);
                signature.update(plainText);
                return signature.sign();
            }
        }

        class DigitalSignatureVerifier{
            public boolean verify(byte[] plainText, PublicKey publicKey, byte[] ReceivedSignature) throws Exception
            {
                Signature signature = Signature.getInstance("SHAwithDSA");
                signature.initVerify(publicKey);
                signature.update(plainText);
                return signature.verify(ReceivedSignature);
            }
        }

class MyAuthenticator extends Authenticator
{
    protected PasswordAuthentication getPasswordAuthentication()
    {
        //Access database to get ID and Password

        return new PasswordAuthentication ( "michaelshin", "cs5332".toCharArray() );
    }

    boolean authenticate (String uname, String pass)
    {
        System.out.println("uname: "+uname +"pass: "+pass);
        PasswordAuthentication response = getPasswordAuthentication();

        if( uname.equals(response.getUserName()) && pass.equals(new String(response.getPassword())) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
//Authorization
class MyAuthorization
{
    boolean Authorization(String uname, String role, String message) {

        List<String> admin = Arrays.asList("ConfirmShipment", "makeOrder", "deleteOrder", "verifyOrder");
        List<String> endUser = Arrays.asList("ConfirmShipment", "makeOrder");
        List<String> verifier = Arrays.asList("verifyOrder");
        List<String> NotAssigned = Arrays.asList("","");
        if(message.length()>17){
            message = message.substring(0, message.length() - 3);
        }else  message = message.substring(0, message.length() - 2);

        boolean P =false;
         List<String> temp=NotAssigned;
        if(role.equals("admin")){temp=admin;}
        if(role.equals("endUser")){temp=endUser;}
        if(role.equals("verifier")){temp=verifier;}
        for (String element : temp) {

            if (element.contains(message)) {

                P = true;
                break;
            } else {
                P = false;
            }
        }
        return P;
    }
}




class SecureReceiverConnector {
    static Thread t_AsynchronousMCReceiver;
    static Thread t_SecurityReceiverCoordinator;    static EncryptionDecryptor ed;
    static DigitalSignatureVerifier dsv;
    static hashValueVerification hsv;
    static MyAuthenticator ath;
    static MyAuthorization atr;
    public static void aSecureReceiverConnector(String msg){
        try {

            ed = new EncryptionDecryptor();
            dsv = new DigitalSignatureVerifier();
            hsv = new hashValueVerification();
            ath = new MyAuthenticator();
            atr = new MyAuthorization();
            AsynchronousMCReceiver asynchronousMCReceiver = new AsynchronousMCReceiver();
            asynchronousMCReceiver.sendSecAsync();
            t_AsynchronousMCReceiver = asynchronousMCReceiver.t_AsynchronousMCReceiver;

            SecurityReceiverCoordinator securityReceiverCoordinator = new SecurityReceiverCoordinator(msg);

            securityReceiverCoordinator.sendSecAsync(ed,hsv, dsv, ath,atr);
            t_SecurityReceiverCoordinator = securityReceiverCoordinator.t_SecurityReceiverCoordinator;

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}

class AsynchronousMCReceiver implements Runnable {
    Thread t_AsynchronousMCReceiver;

    public void sendSecAsync() throws Exception{

        t_AsynchronousMCReceiver = new Thread(this, "AsynchronousMCReceiver");
        t_AsynchronousMCReceiver.start();

    }

    public void run()
    {

        int i = 0;
        ByteMessage byteMessage = new ByteMessage();
        //add Message object
        StringBMessage message = new StringBMessage();

        while(i<Global.input)
        {
            i++;
            //byteMessage = Global.q3.get();
            byteMessage = Global.q4.get(); //added

            //convert byteMessage to stringMessage for posting
            //message.messageContent = new String(byteMessage.messageContent);
            //message.messageName = new String(byteMessage.messageName);
            //message.senderID = new String(byteMessage.senderID);
            //message.signature = new String(byteMessage.signature);
            //message.hashedValue = new String(byteMessage.hashedValue);
            //message.userRole = new String(byteMessage.userRole);

            Global.q4.put(byteMessage);
           // Global.q4.put(message);
        }

    }
}



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

}


class SecurityReceiverCoordinator implements Runnable
{
    //added
    StringMessage msg = new StringMessage();
    public SecurityReceiverCoordinator(String strmsg){
                //added
                Gson gson = new Gson(); 
                //Keys keysobj = new Keys();
                this.msg = gson.fromJson(strmsg,StringMessage.class);
                //this.msg = msg;
                                   }
    Thread t_SecurityReceiverCoordinator;

    boolean result;

    Cipher desCipher;
    EncryptionDecryptor ed;

    SecretKey desKey;
    DigitalSignatureVerifier dsv;
    hashValueVerification hsv;
    MyAuthenticator ath;
    MyAuthorization atr;

 public void sendSecAsync(EncryptionDecryptor ed,hashValueVerification hsv, DigitalSignatureVerifier dsv, MyAuthenticator ath,MyAuthorization atr)
throws Exception{
        t_SecurityReceiverCoordinator = new Thread(this, "SecurityReceiverCoordinator");
        t_SecurityReceiverCoordinator.start();
        this.ed = ed;
        this.dsv = dsv;
        this.hsv = hsv;
        this.ath = ath;
        this.atr = atr;
    }
    public void run(){
        int i = 0;
        ByteMessage byteMessage = new ByteMessage();
        //StringMessage mess = new StringMessage();
        Message message =  new Message();
        KeyRequestMessage keyRequestMessage = new KeyRequestMessage();

        //added
        /* KEY REQUEST 
        KeyRequest kr = new KeyRequest();
        KeyMessageRequest kmr = new KeyMessageRequest();
        String keysstring  = kmr.get("http://127.0.0.1:8000/requestkey");
              //System.out.println(keysstring);
        Gson gson = new Gson(); 
        Keys keysobj = new Keys();
        keysobj = gson.fromJson(keysstring,Keys.class);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        byte[] secretKeyBytes = Base64.getDecoder().decode(keysobj.secretKey);
        byte[] publicKeyBytes = Base64.getDecoder().decode(keysobj.publicKey);
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "DES");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        //PublicKey priKey = keyFactory.generatePrivate(publicKeySpec);
        KEY REQUEST */

        while(i<Global.input)
        {
            i++;
            try {
               // KeyFactory keyFactory = KeyFactory.getInstance("DSA");
               // EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
               // PrivateKey priKey = keyFactory.generatePrivate(privateKeySpec);
                    
                 /* KEY REQUEST */
                 KeyRequest kr = new KeyRequest();
                 KeyMessageRequest kmr = new KeyMessageRequest();
                 String keysstring  = kmr.get("http://127.0.0.1:8000/requestkey");
                 //System.out.println(keysstring);
                 Gson gson = new Gson();
                 Keys keysobj = new Keys();
                 keysobj = gson.fromJson(keysstring,Keys.class);
                 KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                 byte[] secretKeyBytes = Base64.getDecoder().decode(keysobj.secretKey);
                 byte[] publicKeyBytes = Base64.getDecoder().decode(keysobj.publicKey);
                 SecretKey secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "DES");
                 EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                 PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
                 //PublicKey priKey = keyFactory.generatePrivate(publicKeySpec);
                 /*KEY REQUEST */

                //byteMessage = Global.q4.get();
                byteMessage = Global.q4.get();
      
                //keyRequestMessage.messageName = "Request Key";
                //SecretKey secretKey = Global.mbrSecretKey.send(keyRequestMessage); //mbr = message buffer and response
                //PublicKey publicKey = Global.mbrPublicKey.send(keyRequestMessage);
                message.messageContent = new String(byteMessage.messageContent);
                message.messageName = new String(byteMessage.messageName);
                   //Digital Signature verification
                    result = dsv.verify(byteMessage.messageContent, publicKey, byteMessage.signature);

                    if(!result)
                    {
                        System.out.println("Signature verification failure");
                        message.messageName = ("MessageName - Signature Failed");
                    }
                    System.out.println("Signature verification Passed");


                   //Integrity verification 
                    message.messageContent = new String(byteMessage.messageContent);
                    result = hsv.verify(byteMessage.hashedValue, byteMessage.messageContent ); //Hashing_feature
                    if(!result)
                    {
                        System.out.println("Integrity verification failure");
                    }
                    System.out.println("Integrity verification Passed");

                   //Decrypted messageContent
                    byteMessage.messageContent = ed.decrypt(byteMessage.messageContent, secretKey);
                    message.messageContent = new String(byteMessage.messageContent);
                    System.out.println("Decryption happened: "+ message.messageContent );
//                    message.messageName = new String(byteMessage.messageName);

                   //Decrypted senderID
                    byteMessage.senderID = ed.decrypt(byteMessage.senderID, secretKey);
                    message.senderID = new String(byteMessage.senderID);
 //                   System.out.println("Decryption senderID: "+ message.senderID );

                  //Decrypted userRole
                   byteMessage.userRole = ed.decrypt(byteMessage.userRole, secretKey);
                        message.userRole = new String(byteMessage.userRole);//take user role
                    System.out.println("Decryption userRole: "+ message.userRole );

                   //Authentication verification 
                    String user=message.senderID;
                    String[] array = user.split(" ");
                    String uname=array[0];
                    String pass=array[1];
//                    System.out.println("pass :"+pass);
                    boolean result = ath.authenticate(uname, pass);

                    if(result == true)
                    {
                        System.out.println("Authentication has been Success!!!!");
                    }
                    else
                    {
                        System.out.println("Authentication Failed!!!!");
                    }
                   //Authorization verification
                        String user1=message.userRole;
                        String[] array1 = user1.split(" ");
                        String uname1=array1[0];
                        String role=array1[1];
                         result = atr.Authorization(uname1, role, message.messageName);

                        if(result == true)
                        {
                            System.out.println("Authorization has been Success!!!!");
                        }
                        else
                        {
                            System.out.println("Sorry! you don't have access, Authorization Failed!!!!");
                        }

                //Global.receiverComponentQueue.put(message);
                Global.receiverComponentQueue.post(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


