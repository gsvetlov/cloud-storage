package ru.svetlov.domain.command.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class GenericCommand {
    protected String command;
    protected Object[] parameters;
}
