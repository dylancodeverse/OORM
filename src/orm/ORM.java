package orm;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ORM
 */
public class ORM<T> {

    @SuppressWarnings("unchecked")
    public T[] select(Connection connection, boolean isTransactional) throws Exception {
        try {
            String request = "SELECT * FROM " + getClass().getSimpleName();
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);
            Field[] fields = getClass().getDeclaredFields();
            ArrayList<T> tLists = new ArrayList<T>();
            while (res.next()) {
                T t = ((T) getClass().getConstructor().newInstance());
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Object obj = res.getObject(fields[i].getName());
                        try {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), obj.getClass());
                            m.invoke(t, obj);
                        } catch (NoSuchMethodException e) {
                            fields[i].setAccessible(true);
                            fields[i].set(t, obj);
                        }
                    } catch (Exception e) {
                        // on s'en fout
                        System.out.println(e);
                        System.out.println("WARNING:" + fields[i].getName() + " column does not exist");
                    }
                }
                tLists.add(t);
            }

            T[] resultArray = (T[]) Array.newInstance(getClass(), tLists.size());
            return tLists.toArray(resultArray);
        } finally {
            if (!isTransactional) {
                connection.close();
            }
        }

    }

    @SuppressWarnings("unchecked")
    public T[] select(Connection connection, boolean isTransactional, String specialQuery) throws Exception {
        try {
            String request = specialQuery;
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);
            Field[] fields = getClass().getDeclaredFields();
            ArrayList<T> tLists = new ArrayList<T>();
            while (res.next()) {
                T t = ((T) getClass().getConstructor().newInstance());
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Object obj = res.getObject(fields[i].getName());
                        try {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), obj.getClass());
                            m.invoke(t, obj);
                        } catch (NoSuchMethodException e) {
                            fields[i].setAccessible(true);
                            fields[i].set(t, obj);
                        }
                    } catch (Exception e) {
                        // on s'en fout
                        System.out.println(e);
                        System.out.println("WARNING:" + fields[i].getName() + " column does not exist");
                    }
                }
                tLists.add(t);
            }

            T[] resultArray = (T[]) Array.newInstance(getClass(), tLists.size());
            return tLists.toArray(resultArray);
        } finally {
            if (!isTransactional) {
                connection.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public T[] selectWhere(Connection connection, boolean isTransactional, String where) throws Exception {
        try {
            String request = "SELECT * FROM " + getClass().getSimpleName() + " where " + where;
            System.out.println(request);
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);
            Field[] fields = getClass().getDeclaredFields();
            ArrayList<T> tLists = new ArrayList<T>();
            while (res.next()) {
                T t = ((T) getClass().getConstructor().newInstance());
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Object obj = res.getObject(fields[i].getName());
                        try {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), obj.getClass());
                            m.invoke(t, obj);
                        } catch (NoSuchMethodException e) {
                            fields[i].setAccessible(true);
                            fields[i].set(t, obj);
                        }
                    } catch (Exception e) {
                        // on s'en fout
                        System.out.println(e);
                        System.out.println("WARNING:" + fields[i].getName() + " column does not exist");
                    }
                }
                tLists.add(t);
            }

            T[] resultArray = (T[]) Array.newInstance(getClass(), tLists.size());
            return tLists.toArray(resultArray);
        } finally {
            if (!isTransactional) {
                connection.close();
            }
        }

    }

    public void insert(Connection connection, boolean isTransactional) throws Exception {
        String tableName = this.getClass().getSimpleName();
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");

        // Construction de la requête SQL avec des paramètres de requête
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" ");

        // Récupération des champs de la classe
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(this);
            if (value != null) {
                columns.append(field.getName()).append(",");
                values.append("?,");
            }
        }

        // Suppression de la virgule finale des chaînes de colonnes et de valeurs
        columns.deleteCharAt(columns.length() - 1);
        values.deleteCharAt(values.length() - 1);

        // Complétion de la requête SQL
        sql.append(columns).append(") VALUES ").append(values).append(")");

        // Préparation de la requête PreparedStatement
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int parameterIndex = 1;
            // Attribution des valeurs aux paramètres de requête
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value != null) {
                    statement.setObject(parameterIndex, value);
                    parameterIndex++;
                }
            }
            // Exécution de la requête
            statement.executeUpdate();
        }
    }

    public Integer insertWithGeneratedKeys(Connection connection, boolean isTransactional) throws Exception {
        try {
            connection.setAutoCommit(false);
            String request = "insert into " + getClass().getSimpleName();
            Field[] fields = getClass().getDeclaredFields();
            String values = "(";
            String columns = "(";
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (fields[i].get(this) != null) {
                    columns = columns + fields[i].getName() + ",";
                    if (!(fields[i].get(this) instanceof Number)) {
                        values = values + "'" + fields[i].get(this) + "'" + ",";
                    } else {
                        values = values + fields[i].get(this) + ",";
                    }
                }
            }
            columns = columns.substring(0, columns.lastIndexOf(",")) + ")";
            values = values.substring(0, values.lastIndexOf(",")) + ")";

            request = request + columns + "values" + values;

            Statement s = connection.createStatement();
            s.executeUpdate(request);
            ResultSet res = s.getGeneratedKeys();
            if (res.next()) {
                Integer idGenere = res.getInt(1);
                return idGenere;
            } else {
                throw new Exception("No key generated");
            }
        } finally {
            if (!isTransactional) {
                connection.commit();
                connection.close();
            }
        }
    }

    public void deleteWhere(Connection connection, boolean isTransactional, String condition) throws Exception {
        try {
            connection.setAutoCommit(false);

            String request = "delete from " + getClass().getSimpleName() + " where " + condition;
            connection.createStatement().executeUpdate(request);
        } finally {
            if (!isTransactional) {
                connection.commit();
                connection.close();
            }
        }
    }

    public void update(Connection connection, boolean isTransactional, String condition) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);

            StringBuilder request = new StringBuilder("UPDATE ").append(getClass().getSimpleName()).append(" SET ");
            Field[] fields = getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (fields[i].get(this) != null) {
                    request.append(fields[i].getName()).append(" = ?, ");
                }
            }
            request.deleteCharAt(request.lastIndexOf(",")).append(" WHERE ").append(condition);

            System.out.println(request);

            preparedStatement = connection.prepareStatement(request.toString());

            int parameterIndex = 1;
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value != null) {
                    if (value instanceof Number) {
                        preparedStatement.setObject(parameterIndex, value);
                    } else {
                        preparedStatement.setString(parameterIndex, value.toString());
                    }
                    parameterIndex++;
                }
            }

            preparedStatement.executeUpdate();

        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (!isTransactional) {
                connection.commit();
                connection.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public T[] select(Connection connection, Integer pageNumber, Integer nElementsPerPage, boolean isTransactional)
            throws Exception {
        try {
            if (pageNumber <= 0) {
                throw new Exception("Le numero de page ne doit etre inferieur ou egal a 0");
            }
            int offset = (pageNumber - 1) * nElementsPerPage;
            String request = "SELECT * FROM " + getClass().getSimpleName() + " LIMIT " + nElementsPerPage + " OFFSET "
                    + offset;

            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);

            Field[] fields = getClass().getDeclaredFields();
            ArrayList<T> tLists = new ArrayList<T>();

            while (res.next()) {
                T t = ((T) getClass().getConstructor().newInstance());
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Object obj = res.getObject(fields[i].getName());
                        try {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), obj.getClass());
                            m.invoke(t, obj);
                        } catch (NoSuchMethodException e) {
                            fields[i].setAccessible(true);
                            fields[i].set(t, obj);
                        }
                    } catch (Exception e) {
                        // on s'en fout
                        System.out.println(e);
                        System.out.println("WARNING:" + fields[i].getName() + " column does not exist");
                    }
                }
                tLists.add(t);
            }

            T[] resultArray = (T[]) Array.newInstance(getClass(), tLists.size());
            return tLists.toArray(resultArray);
        } finally {
            if (!isTransactional) {
                connection.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public T[] selectWhere(Connection connection, Integer pageNumber, Integer nElementsPerPage, boolean isTransactional,
            String where) throws Exception {
        try {
            if (pageNumber <= 0) {
                throw new Exception("Le numero de page ne doit etre inferieur ou egal a 0");
            }
            int offset = (pageNumber - 1) * nElementsPerPage;
            String request = "SELECT * FROM " + getClass().getSimpleName() + " WHERE " + where + " LIMIT "
                    + nElementsPerPage + " OFFSET " + offset;

            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);

            Field[] fields = getClass().getDeclaredFields();
            ArrayList<T> tLists = new ArrayList<T>();

            while (res.next()) {
                T t = ((T) getClass().getConstructor().newInstance());
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Object obj = res.getObject(fields[i].getName());
                        try {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), obj.getClass());
                            m.invoke(t, obj);
                        } catch (NoSuchMethodException e) {
                            fields[i].setAccessible(true);
                            fields[i].set(t, obj);
                        }
                    } catch (Exception e) {
                        // on s'en fout
                        System.out.println(e);
                        System.out.println("WARNING:" + fields[i].getName() + " column does not exist");
                    }
                }
                tLists.add(t);
            }

            T[] resultArray = (T[]) Array.newInstance(getClass(), tLists.size());
            return tLists.toArray(resultArray);
        } finally {
            if (!isTransactional) {
                connection.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public T[] select(Connection connection, Integer pageNumber, Integer nElementsPerPage, boolean isTransactional,
            String specialQuery) throws Exception {
        try {
            if (pageNumber <= 0) {
                throw new Exception("Le numero de page ne doit etre inferieur ou egal a 0");
            }
            int offset = (pageNumber - 1) * nElementsPerPage;
            String request = specialQuery + " LIMIT " + nElementsPerPage + " OFFSET " + offset;

            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);

            Field[] fields = getClass().getDeclaredFields();
            ArrayList<T> tLists = new ArrayList<T>();
            while (res.next()) {
                T t = ((T) getClass().getConstructor().newInstance());
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Object obj = res.getObject(fields[i].getName());
                        try {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), obj.getClass());
                            m.invoke(t, obj);
                        } catch (NoSuchMethodException e) {
                            fields[i].setAccessible(true);
                            fields[i].set(t, obj);
                        }
                    } catch (Exception e) {
                        // on s'en fout
                        System.out.println(e);
                        System.out.println("WARNING:" + fields[i].getName() + " column does not exist");
                    }
                }
                tLists.add(t);
            }

            T[] resultArray = (T[]) Array.newInstance(getClass(), tLists.size());
            return tLists.toArray(resultArray);
        } finally {
            if (!isTransactional) {
                connection.close();
            }
        }
    }


}