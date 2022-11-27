import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
        //ReceiverComponent receiverComponent = new ReceiverComponent(secretKey);
        senderComponent.t_senderComponent.join();
        //SecureSenderConnector.t_SecuritySenderCoordinator.join();
        //SecureSenderConnector.t_AsynchronousMCSender.join();
        //SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
        //SecureReceiverConnector.t_AsynchronousMCReceiver.join();
        //receiverComponent.t_receiverComponent.join();
        publicKeyRepository.t_PublicKeyRepository.join();

}
}

class setSize{
    Global global = new Global();

   public void setSize(int a,int b) {

        Global.senderComponentQueue = new MessageQueue(b);
        Global.q2 = new ByteMessageQueue(b);
        Global.q3 = new ByteMessageQueue(b);
        Global.q4 = new ByteMessageQueue(b);
        Global.receiverComponentQueue = new MessageQueue(b);
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

