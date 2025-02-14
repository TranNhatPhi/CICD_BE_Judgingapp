package com.project.judging.Mapper;

public interface Mapper<Entity, Dto> {
    Dto toDto(Entity entity);
    Entity toEntity(Dto dto);
}
