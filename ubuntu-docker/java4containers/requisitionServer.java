import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SMCWR_TD_RPLY{ //Main

public static void main(String args[]) throws Exception {

       //Parameters
        Global.input =10;    //programmer will decide input

        //CustomerComponent customerComponent = new CustomerComponent();
        //SecureSenderConnector.aSecureSenderConnector();

        //SecureReceiverConnector.aSecureReceiverConnector();


        RequisitionSever requisitionSever = new RequisitionSever();
        //customerComponent.t_CustomerComponent.join();
        //SecureSenderConnector.t_SecuritySenderCoordinator.join();
        //SecureSenderConnector.t_SynchronousMCWithReplySender.join();
        //SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
        //SecureReceiverConnector.t_SynchronousMCWithReplyReceiver.join();
        requisitionSever.t_RequisitionSever.join();
     }
 }


class RequisitionSever implements Runnable
{
    Thread t_RequisitionSever;

    {
        t_RequisitionSever = new Thread(this, "RequisitionSever");
        t_RequisitionSever.start();
    }
    public void run()
    {
        int i = 0;
        while(i< (Global.input * 2))
        {
         i++;
        Message message = new Message();
        ByteMessage byteMessage = new ByteMessage();
        Response response = new Response();

            byteMessage = Global.buff5.receive();
            String PlaceRequisition = new String(byteMessage.messageName);
            String RequisitionOrder = new String(byteMessage.messageContent);

            System.out.println("AT RequisitionSever: " +  PlaceRequisition + " & "+ RequisitionOrder);
            response.status = ("Reply form a Receiver: Status Success").getBytes();
            Global.buff5.reply(response);
        }
    }
}



