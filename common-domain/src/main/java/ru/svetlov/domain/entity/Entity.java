package ru.svetlov.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Entity {
    protected int id;

    protected Entity(int id) {
        this.id = id;
    }
}
