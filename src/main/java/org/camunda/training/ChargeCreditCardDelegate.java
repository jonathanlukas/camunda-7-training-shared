package org.camunda.training;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.training.services.CreditCardService;

import javax.inject.Inject;
import javax.inject.Named;

@Named("chargeCreditCard")
public class ChargeCreditCardDelegate implements JavaDelegate {
  private final CreditCardService creditCardService;

  @Inject
  public ChargeCreditCardDelegate(CreditCardService creditCardService) {
    this.creditCardService = creditCardService;
  }

  @Override
  public void execute(DelegateExecution execution) {
    // extract variables from process instance
    String cardNumber = (String) execution.getVariable("cardNumber");
    String cvc = (String) execution.getVariable("CVC");
    if (cvc.equals("789")) {
      throw new RuntimeException("CVC invalid!");
    }
    String expiryData = (String) execution.getVariable("expiryDate");
    Double amount = (Double) execution.getVariable("openAmount");
    // execute business logic using the variables
    creditCardService.chargeAmount(cardNumber, cvc, expiryData, amount);
  }
}