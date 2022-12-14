import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


 class Message {
             String messageName = null;
             String messageContent = null; 
             }

 class ByteMessage {
           byte[] messageName = null;
            byte[] messageContent = null;
          }

 class Response {
          byte[] status = null;  
                   }
                    
 class MBR {  //Message Buffer and Response using ByteMessage class
          private ByteMessage byteMessage;
          private Response response;
          private boolean messageBufferFull = false;
          private boolean responseBufferFull = false;
                          
          synchronized Response send(ByteMessage byteMessage)
                                {
                                         this.byteMessage = byteMessage;
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
                                                              return response;
                                                           }

   synchronized ByteMessage receive()
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
                                   return byteMessage;
                               }

    synchronized void reply(Response response)
                              {
                                       this.response= response;
                                       responseBufferFull = true;
                                       notify();
                                   } 
                               }


class Global{
         //public static MBR1 buff1 = new MBR1(); //between sender component and security sender coordinator
         //public static MBR buff2 = new MBR(); // between sender coordinator and SMCWR sender
         //public static MBR buff3 = new MBR(); //between SMCWR sender and SMCWR receiver
         //public static MBR buff4 = new MBR(); //between SMCWR receiver and security receiver coordinator
         public static MBR buff5 = new MBR(); //to send byteMessage between security receiver coordinator and receiver component
         public static int input; //How many messages will be sent?
     }

public class requisitionServer{ //Main

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



