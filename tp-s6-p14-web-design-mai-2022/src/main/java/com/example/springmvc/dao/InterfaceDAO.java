package com.example.springmvc.dao;

import java.util.List;

public interface InterfaceDAO {

    public void save(Object o) throws Exception;
    public <T extends Object> List<T> getAll(Class<T> clazz);
    public <T extends Object> T getById(Class<T> clazz, Integer id) throws Exception;
    public void update(Object o) throws Exception;
    public void delete(Object o) throws Exception;

    public <T extends Object> List<T> getAllByPagination(Class<T> clazz, Integer offset, Integer limit);
}
