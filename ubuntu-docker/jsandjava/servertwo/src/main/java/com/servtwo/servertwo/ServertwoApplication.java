package com.servtwo.servertwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject; 
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@SpringBootApplication
public class ServertwoApplication {
     @GetMapping("/servertwo")
     public String home(){
         return "This is servertwo";
         }

     //Handling post request
     //@PostMapping(path="/sendmessage",consumes = "any", produces = "application/octet-stream")
     @PostMapping("/sendmessage")
     public String insert(@RequestBody String ob)
          {
          // Storing the incoming data in the list
               //Data.add(new Details(ob.number, ob.name));
               //String message = ob.toString();
               System.out.println("message received "+ob);
               return "Data received. This is a response from server two";
                   }

	public static void main(String[] args) {
		SpringApplication.run(ServertwoApplication.class, args);
	}

}
