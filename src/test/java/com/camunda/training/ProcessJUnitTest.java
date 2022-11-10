package com.camunda.training;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.camunda.training.ChargeCreditCardDelegate;
import org.camunda.training.DeductCreditDelegate;
import org.camunda.training.SendPaymentCompletionDelegate;
import org.camunda.training.SendPaymentRequestDelegate;
import org.camunda.training.services.CreditCardService;
import org.camunda.training.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Deployment(resources = "payment_process.bpmn")
@ExtendWith(ProcessEngineCoverageExtension.class)
public class ProcessJUnitTest {
  @BeforeEach
  public void setup() {
    Mocks.register("deductCredit", new DeductCreditDelegate(new CustomerService()));
    Mocks.register("chargeCreditCard", new ChargeCreditCardDelegate(new CreditCardService()));
    Mocks.register("paymentRequest", new SendPaymentRequestDelegate());
    Mocks.register("paymentCompletion", new SendPaymentCompletionDelegate());
  }

  @Test
  public void testHappyPath() {
    Mocks.register("paymentCompletion", (JavaDelegate) execution -> {});
    // Create a HashMap to put in variables for the process instance
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("orderTotal", 30.00);
    variables.put("customerId", "cust20");
    variables.put("expiryDate", "12/24");
    variables.put("CVC", "123");
    // Start process with Java API and variables
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PaymentProcess", "order0", variables);
    // assert that the process is waiting at charge credit card
    assertThat(processInstance).isWaitingAt("Activity_Charge_Credit_Card");
    execute(job());
    // Make assertions on the process instance
    assertThat(processInstance).isEnded().hasPassed("Activity_Charge_Credit_Card");

  }

  @Test
  public void testCreditSufficient(){Map<String, Object> variables = new HashMap<>();
    Mocks.register("paymentCompletion", (JavaDelegate) execution -> {});
    variables.put("openAmount", 0);
    ProcessInstance processInstance = runtimeService()
        .createProcessInstanceByKey("PaymentProcess")
        .businessKey("order1")
        .startAfterActivity("Activity_Deduct_Amount")
        .setVariables(variables)
        .execute();
    assertThat(processInstance)
        .isEnded()
        .hasNotPassed("Activity_Charge_Credit_Card");
  }

  @Test
  @Deployment(resources = "order_process.bpmn")
  public void testOrderProcess() {
    // I will not start the other process now
    Mocks.register("paymentRequest", (JavaDelegate) execution -> {});
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("OrderProcess", "Test 1", withVariables(
        "orderTotal",             30.00,
        "customerId", "cust30",
        "cardNumber",        "1234 5678",
        "CVC",        "123",
        "expiryDate",        "09/24"
    ));
    runtimeService().correlateMessage("paymentCompletedMessage");
    assertThat(processInstance).isEnded();
  }

  @Test
  public void testInvalidCVC(){
    Mocks.register("paymentCompletion", (JavaDelegate) execution -> {});
    // Create a HashMap to put in variables for the process instance
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("orderTotal", 30.00);
    variables.put("customerId", "cust20");
    variables.put("cardNumber", "1234 5678");
    variables.put("CVC", "789");
    variables.put("expiryDate", "09/24");
    // Start process with Java API and variables
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PaymentProcess", variables);
    // try to execute credit card payment
    assertThat(processInstance).isWaitingAt("Activity_Charge_Credit_Card");
    RuntimeException exception = assertThrows(RuntimeException.class, () -> execute(job()));
    assertThat(exception).hasMessage("CVC invalid!");
  }
}
