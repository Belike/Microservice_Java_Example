package externalTaskExample.payment;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.client.ExternalTaskClient;

public class CompensateDeductionWorker {
	
	public static void main(String [] args) {
		ExternalTaskClient client = ExternalTaskClient.create()
			      .baseUrl("http://localhost:8080/engine-rest")
			      .asyncResponseTimeout(1000)
			      .build();
		
	client.subscribe("compensateDeduction")
		.handler((externalTask, externalTaskService) ->	{
			Map<String, Object> variables = new HashMap<>();
			
			int remaining_amount = (int) externalTask.getVariable("remainingAmountToCharge");
			int amount = (int) externalTask.getVariable("amount");
			int credit = (int) externalTask.getVariable("credit");
			
			System.out.println("Compensate the credit");
			
			variables.put("credit", amount - remaining_amount);
			externalTaskService.complete(externalTask, variables);
		}).open();
	}

}
