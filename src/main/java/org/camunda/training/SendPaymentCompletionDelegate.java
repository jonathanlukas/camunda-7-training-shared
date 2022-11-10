package org.camunda.training;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@Named("paymentCompletion")
public class SendPaymentCompletionDelegate implements JavaDelegate {

  private static final Logger LOG = LoggerFactory.getLogger(SendPaymentRequestDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    execution
        .getProcessEngineServices()
        .getRuntimeService()
        .createMessageCorrelation("paymentCompletedMessage")
        .processInstanceBusinessKey(execution.getBusinessKey())
        .correlate();
  }
}