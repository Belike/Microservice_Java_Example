package externalTaskExample.payment;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PaymentCompletedWorker {
	
	public static void main(String [] args) {
		ExternalTaskClient client = ExternalTaskClient.create()
			      .baseUrl("http://localhost:8080/engine-rest")
			      .asyncResponseTimeout(1000)
			      .build();
		
	client.subscribe("payment-complete")
		.handler((externalTask, externalTaskService) ->	{
			
			String businessKey = (String)externalTask.getBusinessKey();
			try {
				
				Map<String, Object> parameters = new HashMap<>();
				parameters.put("messageName", "payment-completed-message");
				parameters.put("businessKey", businessKey);
				parameters.put("resultEnabled", true);
			
				var objectMapper = new ObjectMapper();
				String requestBody;
				requestBody = objectMapper.writeValueAsString(parameters);
				
				System.out.println(requestBody);
				
				HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("http://localhost:8090/engine-rest/message"))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.build();
				HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
				externalTaskService.complete(externalTask);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).open();
	}

}
