package com.cfo.reporting.exception;

import lombok.Data;

@Data
public class RepositoryExceptionEvent {

    private final String repositoryName;
    private final String methodName;
    private final Exception exception;


    public RepositoryExceptionEvent(String repositoryName, String methodName, Exception exception) {
        this.repositoryName = repositoryName;
        this.methodName = methodName;
        this.exception = exception;
    }
}
