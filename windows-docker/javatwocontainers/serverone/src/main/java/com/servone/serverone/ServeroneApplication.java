package com.servone.serverone;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity; 

//import java.lang.Object;
//import com.servone.serverone.details.Details;


@SpringBootApplication
@RestController
public class ServeroneApplication {
   @GetMapping("/serverone") 
   public String home() {
                      try{
                      //start here
                      RestTemplate resttemp = new RestTemplate();
                      String baseurl = "http://127.0.0.1:8008/sendmessage";
                      URI uri = new URI(baseurl);
                      //JSONObject detail = new JSONObject();
                      //detail.put("1", "This is a request from serverone");
                      //Details detail = new Details("1","This is a request from serverone");
                      String detail ="This is a request from server one";
                      ResponseEntity<String> result = resttemp.postForEntity(uri,detail,String.class);
                      if (result.getStatusCodeValue() == 201){//check response results 
                          System.out.println("Successfully sent request ");
                          }
                      //end here
                     return "This is serverone";
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


