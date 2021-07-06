package ru.svetlov.domain.command.base.annotations;

import ru.svetlov.domain.command.base.CommandType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ACommandHandler {
    CommandType command() default CommandType.UNKNOWN;
}
