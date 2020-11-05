package externalTaskExample;

import javax.management.RuntimeErrorException;

import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import externalTaskExample.payment.DeductCreditWorker;

public class InvoiceExampleCall {
	
private final Logger LOGGER = LoggerFactory.getLogger(DeductCreditWorker.class.getName());
	
	public static void main(String [] args) {
		ExternalTaskClient client = ExternalTaskClient.create()
			      .baseUrl("http://localhost:8080/engine-rest")
			      .asyncResponseTimeout(1000)
			      .build();
		
	client.subscribe("invoiceExample")
		.handler((externalTask, externalTaskService) ->	{
			try {
				Boolean shouldFail = externalTask.getVariable("shouldFail");
				System.out.println("This is the invoice handling : ShouldFail is set to: " + shouldFail);
			
				if(shouldFail) {
					throw new Exception();
				}else {
					System.out.println("Completing the ExternalTask: "+externalTask.getId());
					externalTaskService.complete(externalTask);
				}
				}catch (Exception e) {
					var remainingRetries = externalTask.getRetries();
					if(remainingRetries == null) remainingRetries = 3;
					System.out.println("RemainingRetries are: "+ (--remainingRetries));
					externalTaskService.handleFailure(externalTask, "This is an error in the code", "details", remainingRetries, 0);
				}
		}).open();
	}

}
