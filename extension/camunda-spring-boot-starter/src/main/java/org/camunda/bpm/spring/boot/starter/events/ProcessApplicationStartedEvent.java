package org.camunda.bpm.spring.boot.starter.events;

public class ProcessApplicationStartedEvent extends ProcessApplicationEvent {

  private static final long serialVersionUID = 8052917038949847157L;

  public ProcessApplicationStartedEvent(Object source) {
    super(source);
  }

}
