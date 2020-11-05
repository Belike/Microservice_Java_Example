package externalTaskExample.payment;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeductCreditWorker {
	
	private final Logger LOGGER = LoggerFactory.getLogger(DeductCreditWorker.class.getName());
	
	public static void main(String [] args) {
		ExternalTaskClient client = ExternalTaskClient.create()
			      .baseUrl("http://localhost:8080/engine-rest")
			      .asyncResponseTimeout(1000)
			      .build();
		
	client.subscribe("creditDeducting")
		.handler((externalTask, externalTaskService) ->	{
			Map<String, Object> variables = new HashMap<>();
			
			int amount = (int) externalTask.getVariable("amount");
			int credit = (int) externalTask.getVariable("credit");
			
			System.out.println("Deducting existing credit");
			
			if(credit - amount >= 0) {
				variables.put("creditSufficient", true);
				variables.put("credit", credit-amount);
			}else {
				variables.put("creditSufficient", false);
				variables.put("credit", 0);
				variables.put("remainingAmountToCharge", amount-credit);
			}
			
			externalTaskService.complete(externalTask, variables);
		}).open();
	}

}
