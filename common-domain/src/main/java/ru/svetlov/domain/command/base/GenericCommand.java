package ru.svetlov.domain.command.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public abstract class GenericCommand implements Serializable {
    protected CommandType command;
    protected Object[] parameters;
}
