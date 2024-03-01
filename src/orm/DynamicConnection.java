package orm;

import java.sql.Connection;

public abstract class DynamicConnection<T> extends ORM<T> {

    public abstract Connection getConnection();

    protected T[] select() throws Exception {
        return select(getConnection(), false);
    }

    protected T[] select(String specialQuery) throws Exception {
        return select(getConnection(), false, specialQuery);
    }

    protected T[] selectWhere(String where) throws Exception {
        return selectWhere(getConnection(), false, where);
    }

    protected void insert() throws Exception {
        insert(getConnection(), false);
    }

    protected void deleteWhere(String where) throws Exception {
        deleteWhere(getConnection(), false, where);
    }

    protected void update(String condition) throws Exception {
        update(getConnection(), false, condition);
    }

    protected T[] selectPagination(Integer pageNumber, Integer elementsPerPage) throws Exception {
        return select(getConnection(), pageNumber, elementsPerPage, false);
    }

    protected T[] selectPaginationWhere(String condition, Integer pageNumber, Integer elementsPerPage)
            throws Exception {
        return selectWhere(getConnection(), pageNumber, elementsPerPage, false, condition);
    }

    protected T[] selectPaginationSpecialQuery(String query, Integer pageNumber, Integer elementsPerPage)
            throws Exception {
        return selectWhere(getConnection(), pageNumber, elementsPerPage, false, query);
    }
}
