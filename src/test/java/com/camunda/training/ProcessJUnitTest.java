package com.camunda.training;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.camunda.training.ChargeCreditCardDelegate;
import org.camunda.training.DeductCreditDelegate;
import org.camunda.training.services.CreditCardService;
import org.camunda.training.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.assertj.core.api.Assertions.*;

@Deployment(resources = "payment_process.bpmn")
@ExtendWith(ProcessEngineCoverageExtension.class)
public class ProcessJUnitTest {
  @BeforeEach
  public void setup() {
    Mocks.register("deductCredit", new DeductCreditDelegate(new CustomerService()));
    Mocks.register("chargeCreditCard", new ChargeCreditCardDelegate(new CreditCardService()));
  }

  @Test
  public void testHappyPath() {
    // Create a HashMap to put in variables for the process instance
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("orderTotal", 30.00);
    variables.put("customerId", "cust20");
    variables.put("expiryDate", "12/24");
    // Start process with Java API and variables
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PaymentProcess", variables);
    // Make assertions on the process instance
    assertThat(processInstance).isEnded().hasPassed("Activity_Charge_Credit_Card");

  }

}
