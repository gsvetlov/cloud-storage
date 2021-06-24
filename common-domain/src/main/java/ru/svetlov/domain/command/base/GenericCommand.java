package ru.svetlov.domain.command.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class GenericCommand implements Serializable {
    protected String command;
    protected Object[] parameters;
}
