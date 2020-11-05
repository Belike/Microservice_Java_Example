package externalTaskExample.payment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChargeCreditCardWorker {
	private final Logger LOGGER = LoggerFactory.getLogger(ChargeCreditCardWorker.class.getName());
	
	public static void main(String [] args) {
		ExternalTaskClient client = ExternalTaskClient.create()
			      .baseUrl("http://localhost:8080/engine-rest")
			      .asyncResponseTimeout(1000)
			      .build();
		
	client.subscribe("chargeCredit")
		.handler((externalTask, externalTaskService) ->	{
			Map<String,Object> variables = new HashMap<>();
			
			int amount = (int) externalTask.getVariable("remainingAmountToCharge");
			System.out.println("Charging Credit Card for: "+amount);
			int random = ThreadLocalRandom.current().nextInt(0,10);
			
			if(random >= 5) {
				if(random > 6) {
					variables.put("resolvable", true);
				}else {
					variables.put("resolvable", false);
				}
				externalTaskService.handleBpmnError(externalTask, "CreditCardFailure", "Couldn't be resolved", variables);
			}else {
				externalTaskService.complete(externalTask);
			}
		}).open();
	}

}