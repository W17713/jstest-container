import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class receiver{ //Main

public static void main(String args[]) throws Exception {

        /*KeyGenerator keygen = KeyGenerator.getInstance("DES");
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
        setside.setSize(Global.input,Global.queueSize);*/
        //SenderComponent senderComponent = new SenderComponent(secretKey, privateKey);
        //SecureSenderConnector.aSecureSenderConnector();

        //SecureReceiverConnector.aSecureReceiverConnector();

        //PublicKeyRepository publicKeyRepository = new PublicKeyRepository(publicKey);
        ReceiverComponent receiverComponent = new ReceiverComponent(secretKey);
        //senderComponent.t_senderComponent.join();
        //SecureSenderConnector.t_SecuritySenderCoordinator.join();
        //SecureSenderConnector.t_AsynchronousMCSender.join();
        //SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
        //SecureReceiverConnector.t_AsynchronousMCReceiver.join();
        receiverComponent.t_receiverComponent.join();
        //publicKeyRepository.t_PublicKeyRepository.join();

}
}


class ReceiverComponent implements Runnable
{
    Thread t_receiverComponent;

    SecretKey secretKey;
    public ReceiverComponent(SecretKey sk)
    {
        t_receiverComponent = new Thread(this, "ReceiverComponent");
        t_receiverComponent.start();
        this.secretKey = sk;
    }

    public void run()
    {
        int i = 0;
        Message message = new Message();
        KeyRequestMessage keyRequestMessage = new KeyRequestMessage();

        while(i<Global.input)
        {
            i++;


            keyRequestMessage = Global.mbrSecretKey.receive();

            if (keyRequestMessage.messageName.equals("Request Key"))
            {
                Global.mbrSecretKey.reply(secretKey);
            }


            message = Global.receiverComponentQueue.get();
            System.out.println("ReceiverComponent: messageName = " + message.messageName);
            System.out.println("ReceiverComponent: messageContent = " + message.messageContent + "\n");

        }
    }
}



