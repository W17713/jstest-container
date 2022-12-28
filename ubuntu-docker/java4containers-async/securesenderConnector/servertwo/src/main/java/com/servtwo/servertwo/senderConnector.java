package com.servtwo.servertwo;

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
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.DESKeySpec;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Hex;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;

@RestController
@SpringBootApplication
public class senderConnector {
          @GetMapping("/securesender")
          public String home(){
              try{
                    //Global.input =10;
                    //Global.queueSize=25;  //program will decide queueSize
                   // setSize setside= new setSize();
                    //setside.setSize(Global.input,Global.queueSize);

                   return "This is the secure sender server";
              }catch(Exception e){
              String errorMessage = null;
              errorMessage = e.getMessage();
              System.out.println(errorMessage);
              return errorMessage;
              }
                   }
          
          
          @PostMapping("/sendmessage")
          public String insert(@RequestBody String ob)
                    {
                        try{
                            Global.input =1;
                            Global.queueSize=25;  //program will decide queueSize
                            setSize setside= new setSize();
                            setside.setSize(Global.input,Global.queueSize);
                            //Global.input =10;
                            //System.out.println("received body");
                            //System.out.println(ob);
                            SecureSenderConnector.aSecureSenderConnector(ob);
                            //senderComponent.t_senderComponent.join();
                            SecureSenderConnector.t_SecuritySenderCoordinator.join();
                            SecureSenderConnector.t_AsynchronousMCSender.join();
                        }catch(InterruptedException e)
                                {
                                     System.out.println("InterruptedException caught");
                                                                    }
                          return "response";
                    }
 
        public static void main(String[] args) {
             SpringApplication.run(senderConnector.class, args);
         }
        }

 class setSize{
            Global global = new Global();
     
            public void setSize(int a,int b) {
         
                        //Global.senderComponentQueue = new MessageQueue(b);
                        Global.q2 = new ByteMessageQueue(b);
                        Global.q3 = new MessageQueue(b);
                        //Global.q4 = new ByteMessageQueue(b);
                        //Global.receiverComponentQueue = new MessageQueue(b);
                    }
        }

class Keys {
         String secretKey = null;
         String publicKey = null;
         String privateKey = null;
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
    public void put(StringBMessage message) {
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
        msgobj.put("hashedValue", message.hashedValue);
        msgobj.put("signature", message.signature);
        msgobj.put("senderID", message.senderID);
        msgobj.put("userRole", message.userRole);


        up = (up + 1) % maxCount; // to place next item in

        if (messageCount == 1){
            //notify(); //change notify function to http post function
            try{
                RestTemplate resttemp = new RestTemplate();
                String baseurl = "http://127.0.0.1:8080/sendmessage";
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
        public String post(StringBMessage requestMsg){//edited for ssC
            try{
                String posturl = "http://127.0.0.1:8080/sendmessage";
                Gson gson = new Gson(); 
                StringBMessage strrequestMsg = new StringBMessage();
            URL url = new URL (posturl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = null;
    
            //String encsecretKey = Base64.getEncoder().encodeToString(requestMsg.secretKey.getEncoded());
            
            //String encsignature = Base64.getEncoder().encodeToString(requestMsg.signature.getEncoded());
            //strrequestMsg.signature = requestMsg.signature;
            //strrequestMsg.hashedValue = requestMsg.hashedValue;
            //strrequestMsg.messageName = requestMsg.messageName;
            //strrequestMsg.messageContent = requestMsg.messageContent;
            //strrequestMsg.senderID = requestMsg.senderID;
            //strrequestMsg.userRole = requestMsg.userRole;
            //jsonInputString = gson.toJson(strrequestMsg);
            jsonInputString = gson.toJson(requestMsg);

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
    //}

    //public synchronized Message get() {
    public StringMessage get(Message msg) {
        StringMessage message;
        message = new StringMessage();
        //byte[] decodedSecretKey = Base64.getDecoder().decode(msg.secretKey);
        //byte[] decodedPrivateKey = Base64.getDecoder().decode(msg.privateKey);
        //SecretKey secretKey = new SecretKeySpec(decodedSecretKey, 0, decodedSecretKey.length, "AES"); 
        //SecretKey privateKey = new SecretKeySpec(decodedPrivateKey, 0, decodedPrivateKey.length, "AES"); 
        
        String secretKey = Base64.getEncoder().encodeToString(msg.secretKey.getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(msg.privateKey.getEncoded());

        message.messageName = msg.messageName;
        message.messageContent = msg.messageContent;

        message.secretKey = secretKey;
        message.privateKey = privateKey;
        message.senderID = msg.senderID;
        message.userRole = msg.userRole;
        /*
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
            notify();*/
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

//added to store byteMessage as strings
class StringBMessage {
    String messageName = null;
    String messageContent = null;

    String senderID=null;
    String userRole=null;
    String signature = null;
    String hashedValue = null;
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

    public static MessageQueue senderComponentQueue;
    public static ByteMessageQueue q2;
    public static MessageQueue q3; // change from ByteMessageQueue to MessageQueue
    public static ByteMessageQueue q4;
    //public static MessageQueue receiverComponentQueue;

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
class SecureSenderConnector {

    static Thread t_SecuritySenderCoordinator;
    static Thread t_AsynchronousMCSender;

    static EncryptionEncryptor ee;

    static DigitalSignatureSigner dss;

    static hashValueGenration hs;

    static void aSecureSenderConnector(String msg){
        try {
            //System.out.println("Received message");
            //System.out.println(msg);

            ee = new EncryptionEncryptor();
            dss = new DigitalSignatureSigner();
            hs = new hashValueGenration();
            SecuritySenderCoordinator securitySenderCoordinator = new SecuritySenderCoordinator(msg);
            securitySenderCoordinator.sendSecAsync(hs, ee, dss);
            t_SecuritySenderCoordinator = securitySenderCoordinator.t_SecuritySenderCoordinator;

            AsynchronousMCSender asynchronousMCSender = new AsynchronousMCSender();
            asynchronousMCSender.sendSecAsync();
            t_AsynchronousMCSender = asynchronousMCSender.t_AsynchronousMCSender;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

class SecuritySenderCoordinator implements Runnable {

    StringMessage msg = new StringMessage();
    //String msg = new String();
    public SecuritySenderCoordinator(String strmsg){
                //added
                Gson gson = new Gson(); 
                //Keys keysobj = new Keys();
                this.msg = gson.fromJson(strmsg,StringMessage.class);
                //this.msg = msg;
    }
                

    Thread t_SecuritySenderCoordinator;

    EncryptionEncryptor ee;
    DigitalSignatureSigner dss;
    hashValueGenration hs;
public void sendSecAsync(hashValueGenration hs, EncryptionEncryptor ee, DigitalSignatureSigner dss)
throws Exception
    {
        t_SecuritySenderCoordinator = new Thread(this, "SecuritySenderCoordinator");
        t_SecuritySenderCoordinator.start();
        this.ee = ee;
        this.dss = dss;
        this.hs = hs;

    }

    public void run()
    {
        int i=0;
        StringMessage message = new StringMessage();
        ByteMessage byteMessage = new ByteMessage();

        //added
        //byte[] secretKeyBytes = Base64.getDecoder().decode(message.secretKey);
        //byte[] privateKeyBytes = Base64.getDecoder().decode(message.privateKey);
        
        //SecretKey secKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "DES"); 
        //PrivateKey priKey = new DSAPrivateKeySpec(privateKey, 0, privateKey.length, "DSA"); 
        /*
        try{
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        try{
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
      
        PrivateKey priKey = keyFactory.generatePrivate(privateKeySpec);
                }catch(InvalidKeySpecException e){
                         System.out.println("InvalidKeySpecException caught");
                             }
        }catch(NoSuchAlgorithmException e){
                 System.out.println("NoSuchAlgorithmException caught");
             }*/
        //PrivateKey priKey = keyFactory.generatePrivate(privateKeySpec);
        //System.out.println("Running secure sender coordinator");
        while(i<Global.input)
        {
            i++;

            //message=Global.senderComponentQueue.get(this.msg);
            message=this.msg;

           
            try {

                 //added
                    /*Gson gson = new Gson();
                    Keys keysobj = new Keys();
                    keysobj = gson.fromJson(keysstring,Keys.class);*/
                    //System.out.println(message.messageContent);

                    byte[] secretKeyBytes = Base64.getDecoder().decode(message.secretKey);
                    byte[] privateKeyBytes = Base64.getDecoder().decode(message.privateKey);
                  
                    SecretKey secKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "DES");
                    //DESKeySpec secKey = new DESKeySpec(secretKeyBytes, 0);
                    KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                    PrivateKey priKey = keyFactory.generatePrivate(privateKeySpec);
                    //System.out.println("secretKey on ssc"+secKey);
                    //System.out.println("privateKey on ssc"+priKey);

                    byteMessage.messageContent = (message.messageContent).getBytes();
                    byteMessage.messageName = (message.messageName).getBytes();

                    //System.out.println("Byte message content "+byteMessage.messageContent);

                    byteMessage.messageContent = ee.encrypt(byteMessage.messageContent, secKey);
                    //System.out.println("bytemessagecontent "+byteMessage.messageContent);
                    System.out.println("Encrypted messageContent!");

                    byteMessage.senderID = (message.senderID).getBytes();
                    byteMessage.senderID = ee.encrypt(byteMessage.senderID, secKey);
                    System.out.println("Encrypted senderID!");
                    //System.out.println(byteMessage.senderID);

                    byteMessage.userRole = (message.userRole).getBytes();
                    byteMessage.userRole = ee.encrypt(byteMessage.userRole, secKey);
                    System.out.println("Encrypted userRole!");

                    byteMessage.hashedValue = hs.generate(byteMessage.messageContent);
                    System.out.println("Hashed value! ");

                    byteMessage.signature = dss.sign(byteMessage.messageContent,priKey);
                    System.out.println("Signed messageContent! ");

                //System.out.println(byteMessage);
                Global.q2.put(byteMessage);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}

class AsynchronousMCSender implements Runnable {
    Thread t_AsynchronousMCSender;

    public void sendSecAsync() throws Exception{
        t_AsynchronousMCSender = new Thread(this, "AsynchronousMCSender");
        t_AsynchronousMCSender.start();
    }

    public void run()
    {
        int i = 0;
        ByteMessage byteMessage = new ByteMessage();
        //add Message object
        StringBMessage message = new StringBMessage();
        //System.out.println("Running sendsecAsync");
        Hex hex = new Hex();
        while(i<Global.input)
        {
            i++;
            byteMessage = Global.q2.get();
            //convert byteMessage to stringMessage for posting
            try{
            //message.messageContent = new String(byteMessage.messageContent,StandardCharsets.UTF_8);
            //System.out.println("mc on ssc "+byteMessage.messageContent);
            String mcstring = Base64.getEncoder().encodeToString(byteMessage.messageContent);
            message.messageContent = Base64.getUrlEncoder().encodeToString(mcstring.getBytes());
            //System.out.println("mc after encoding "+message.messageContent);
           // message.messageContent = hex.encodeHexString(byteMessage.messageContent);
            }catch(Exception e){
                System.out.println(e.getMessage());}
            //message.messageName = Base64.getEncoder().encodeToString(byteMessage.messageName);
            message.messageName = new String(byteMessage.messageName);
            message.senderID = Base64.getEncoder().encodeToString(byteMessage.senderID);
            //message.senderID = new String(byteMessage.senderID);
            //message.signature = new String(byteMessage.signature,"utf-8");
            message.signature = Base64.getEncoder().encodeToString(byteMessage.signature);
            //System.out.println("sig on ssc "+message.signature);
            message.hashedValue = Base64.getEncoder().encodeToString(byteMessage.hashedValue);
            //message.hashedValue = new String(byteMessage.hashedValue);
            message.userRole = Base64.getEncoder().encodeToString(byteMessage.userRole);
            //message.userRole = new String(byteMessage.userRole);

            //Global.q3.put(message); //replace with post request
            //System.out.println(message.messageContent);
            Global.q3.post(message);
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


