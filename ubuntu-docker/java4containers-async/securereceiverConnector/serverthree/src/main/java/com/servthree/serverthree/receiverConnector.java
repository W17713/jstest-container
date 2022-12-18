package com.servthree.serverthree;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity; 

import java.security.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;


import java.net.Authenticator;
import java.net.PasswordAuthentication;


@RestController
@SpringBootApplication
public class receiverConnector {
     @GetMapping("/securereceiver")
     public String home(){

         return "This is the secure receiver server";
         }

     //Handling post request
     //@PostMapping(path="/sendmessage",consumes = "any", produces = "application/octet-stream")
     @PostMapping("/sendmessage")
     public String insert(@RequestBody Message ob)
     //public String insert(@RequestBody Map<String, Object> ob)
          {
            try{
            Global.input =1;    //programmer will decide input    
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
    public static void aSecureReceiverConnector(Message msg){
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
    Message msg = new Message();
    public SecurityReceiverCoordinator(Message msg){
                                       this.msg = msg;
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
        //byte[] secretKeyBytes = Base64.getDecoder().decode(this.msg.secretKey);
        //byte[] privateKeyBytes = Base64.getDecoder().decode(this.msg.privateKey);
         
        //SecretKey secKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "DES");

        while(i<Global.input)
        {
            i++;
            try {
               // KeyFactory keyFactory = KeyFactory.getInstance("DSA");
               // EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
               // PrivateKey priKey = keyFactory.generatePrivate(privateKeySpec);

                //byteMessage = Global.q4.get();
                byteMessage = Global.q4.get();
                

                keyRequestMessage.messageName = "Request Key";
                SecretKey secretKey = Global.mbrSecretKey.send(keyRequestMessage); //mbr = message buffer and response
                PublicKey publicKey = Global.mbrPublicKey.send(keyRequestMessage);
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

                Global.receiverComponentQueue.put(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


