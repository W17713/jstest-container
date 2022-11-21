package com.servone.serverone;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity; 

import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


//import java.lang.Object;
//import com.servone.serverone.details.Details;


@SpringBootApplication
@RestController
public class ServeroneApplication {
   @GetMapping("/customer") 
   public String home() {
                      try{
                            //Parameters
                            Global.input =1;    //programmer will decide input
                            CustomerComponent customerComponent = new CustomerComponent();
                            customerComponent.t_CustomerComponent.join();
                     return "This is the customer server";
                        }catch(Exception e){
                            String errorMessage = null;
                            errorMessage = e.getMessage();
                            return errorMessage;
                        }
                         }


	public static void main(String[] args) {
		SpringApplication.run(ServeroneApplication.class, args);
	}

}

class Message {
    String messageName = null;
    String messageContent = null; 
     }

class Global{
        public static int input; //How many messages will be sent?
        public static String rsp = new String(); //added
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
            /*var uri = new URI("http://localhost:80/messageone");
            var client = new HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(uri).POST(BodyPublishers.ofString(message)).header("Content-type","octet-stream").build();
            var postresponse = client.send(request,BodyHandlers.discarding());
            String Status = new String(postresponse.status);
            System.out.println("Status is: " +Status+"\n\n");*/
            try{
            RestTemplate resttemp = new RestTemplate();
            String baseurl = "http://127.0.0.1:3000/sendmessage";
            URI uri = new URI(baseurl);
            //JSONObject detail = new JSONObject();
                      //detail.put("1", "This is a request from serverone");
                      //Details detail = new Details("1","This is a request from serverone");
            //String detail ="This is a request from server one";
            ResponseEntity<String> result = resttemp.postForEntity(uri,message.messageContent,String.class);
            Global.rsp=result.getBody();
            System.out.println(Global.rsp);
            //String Status = new String(result.getStatusCodeValue());
            int Status = result.getStatusCodeValue();
            if (Status == 201){//check response results 
                    System.out.println("Status is: " +Status+"\n\n");
                        }
        }catch(Exception e){
             String errorMessage = null;
             errorMessage = e.getMessage();
             //return errorMessage;
             System.out.println(errorMessage);
             }
    }
}



