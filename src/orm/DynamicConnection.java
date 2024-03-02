package orm;

import java.sql.Connection;

public abstract class DynamicConnection<T> extends ORM<T> {

    public abstract Connection getConnection() throws Exception;

    public T[] select() throws Exception {
        return select(getConnection(), false);
    }

    public T[] select(String specialQuery) throws Exception {
        return select(getConnection(), false, specialQuery);
    }

    public T[] selectWhere(String where) throws Exception {
        return selectWhere(getConnection(), false, where);
    }

    public void insert() throws Exception {
        insert(getConnection(), false);
    }

    public void deleteWhere(String where) throws Exception {
        deleteWhere(getConnection(), false, where);
    }

    public void update(String condition) throws Exception {
        update(getConnection(), false, condition);
    }

    public T[] selectPagination(Integer pageNumber, Integer elementsPerPage) throws Exception {
        return select(getConnection(), pageNumber, elementsPerPage, false);
    }

    public T[] selectPaginationWhere(String condition, Integer pageNumber, Integer elementsPerPage)
            throws Exception {
        return selectWhere(getConnection(), pageNumber, elementsPerPage, false, condition);
    }

    public T[] selectPaginationSpecialQuery(String query, Integer pageNumber, Integer elementsPerPage)
            throws Exception {
        return selectWhere(getConnection(), pageNumber, elementsPerPage, false, query);
    }

    public void updateById() throws Exception {
        updateById(getConnection(), false);
    }
}
