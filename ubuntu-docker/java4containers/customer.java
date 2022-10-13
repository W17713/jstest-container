import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SMCWR_TD_RPLY{ //Main

public static void main(String args[]) throws Exception {

       //Parameters
        Global.input =10;    //programmer will decide input

        CustomerComponent customerComponent = new CustomerComponent();
        //SecureSenderConnector.aSecureSenderConnector();

        //SecureReceiverConnector.aSecureReceiverConnector();


        //RequisitionSever requisitionSever = new RequisitionSever();
        customerComponent.t_CustomerComponent.join();
        //SecureSenderConnector.t_SecuritySenderCoordinator.join();
        //SecureSenderConnector.t_SynchronousMCWithReplySender.join();
        //SecureReceiverConnector.t_SecurityReceiverCoordinator.join();
        //SecureReceiverConnector.t_SynchronousMCWithReplyReceiver.join();
        //requisitionSever.t_RequisitionSever.join();
     }
 }
class CustomerComponent  implements Runnable {
    Thread t_CustomerComponent;
    CustomerComponent(
)    {
        t_CustomerComponent = new Thread(this, "SenderComponent");
        t_CustomerComponent.start();
    }
    public void run()
    {
        int i = 0;

        Message message = new Message();
        Response response = new Response();
        while(i<Global.input)
        {
            i++;
            message.messageName = new String("Place Requisition "+ i);
            message.messageContent = new String("Requisition Order Number: " + i);

            response = Global.buff1.send(message);
            String Status = new String(response.status);
            System.out.println("Status is: " +Status+"\n\n");
        }
    }
}





