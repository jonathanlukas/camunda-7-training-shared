package org.camunda.training;

import org.camunda.training.services.CustomerService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import javax.inject.Inject;
import javax.inject.Named;

@Named("deductCredit")
public class DeductCreditDelegate implements JavaDelegate {
  private final CustomerService service;

  @Inject
  public DeductCreditDelegate(CustomerService service) {
    this.service = service;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    // extract variables from process instance
    String customerId = (String) execution.getVariable("customerId");
    Double amount = (Double) execution.getVariable("orderTotal");
    // execute business logic using the variables
    Double openAmount = service.deductCredit(customerId, amount);
    Double customerCredit = service.getCustomerCredit(customerId);
    // save the results to the process instance
    execution.setVariable("openAmount", openAmount);
    execution.setVariable("customerCredit", customerCredit);
  }
}