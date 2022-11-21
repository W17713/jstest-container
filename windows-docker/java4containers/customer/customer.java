//import java.net.http.HttpRequest;
import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
//added
import java.net.*;


class Message {
          String messageName = null;
          String messageContent = null; 
           }
 /*           
class MBR1 {  //Message Buffer and Response using Message Class
              
           private Response response;
           private Message message;
           private boolean messageBufferFull = false;
           private boolean responseBufferFull = false;
                   
           synchronized Response send(Message message)
                         {
                                  this.message = message;
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
                                                      //this.response = response;
                                                       responseBufferFull = false;
                                                        return response;
                                                     }
             synchronized Message receive()
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
                                               return message;
                                            }
             synchronized void reply(Response response)
                                              {
                                                       this.response = response;
                                                        responseBufferFull = true;
                                                         notify();
                                                      }
             }*/

 /*class Response {
           byte[] status = null; 
          }*/


class Global{
         //public static MBR1 buff1 = new MBR1(); //between sender component and se    curity sender coordinator
         //public static MBR buff2 = new MBR(); // between sender coordinator and S    MCWR sender
         //public static MBR buff3 = new MBR(); //between SMCWR sender and SMCWR re    ceiver
         //public static MBR buff4 = new MBR(); //between SMCWR receiver and securi    ty receiver coordinator
         //public static MBR buff5 = new MBR(); //to send byteMessage between secur    ity receiver coordinator and receiver component
         public static int input; //How many messages will be sent?
     
     }


public class customer{ //Main

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
        //Response response = new Response();
        while(i<Global.input)
        {
            i++;
            message.messageName = new String("Place Requisition "+ i);
            message.messageContent = new String("Requisition Order Number: " + i);

            //response = Global.buff1.send(message);
            
            //String Status = new String(response.status);
            //System.out.println("Status is: " +Status+"\n\n");
        }
        //Replaced with post request
        var uri = new URI("http://localhost:80/messageone");
        var client = new HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(uri).POST(BodyPublishers.ofString(message)).header("Content-type","octet-stream").build();
        var postresponse = client.send(request,BodyHandlers.discarding());
        String Status = new String(postresponse.status);
        System.out.println("Status is: " +Status+"\n\n");
    }
}





