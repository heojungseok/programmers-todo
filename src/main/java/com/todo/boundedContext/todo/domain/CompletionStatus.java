package com.todo.boundedContext.todo.domain;

// DB에는 "Y"/"N" 문자열로 저장한다(@Enumerated(EnumType.STRING)).
public enum CompletionStatus {
    Y, N
}
