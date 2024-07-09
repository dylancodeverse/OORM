package orm;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import orm.annotations.Id;
import orm.annotations.Ignore;
import orm.annotations.Table;

/**
 * ORM
 */
public class ORM<T> {

    private String className;

    public ORM() {
        setClassName(this.getClass());
    }

    private String getClassName() {
        return className;
    }

    public void setClassName(Class<?> class1) {
        if (class1.isAnnotationPresent(Table.class)) {
            setClassName(class1.getAnnotation(Table.class).name());
        } else {
            setClassName(class1.getSimpleName());
        }
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @SuppressWarnings("unchecked")
    public T[] select(Connection connection, boolean isTransactional) throws Exception {
        try {
            String request = "SELECT * FROM " + getClassName();
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
                        } catch (NullPointerException e1) {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), fields[i].getType());
                            m.invoke(t, obj);
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

            String request = "SELECT * FROM " + getClassName() + " where " + where;
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
                        } catch (NullPointerException e1) {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), fields[i].getType());
                            m.invoke(t, obj);
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
        if (isTransactional) {
            connection.setAutoCommit(false);
        }
        String tableName = this.getClassName();
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");

        // Construction de la requête SQL avec des paramètres de requête
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" ");

        // Récupération des champs de la classe
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            boolean isAnnotationPresent = field.isAnnotationPresent(Ignore.class);
            Object value = field.get(this);
            if (value != null && !isAnnotationPresent) {
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
        try (PreparedStatement statement = connection.prepareStatement(sql.toString(),
                Statement.RETURN_GENERATED_KEYS)) {
            int parameterIndex = 1;
            // Attribution des valeurs aux paramètres de requête
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(this);
                boolean isAnnotationPresent = field.isAnnotationPresent(Ignore.class);

                if (value != null && !isAnnotationPresent) {
                    statement.setObject(parameterIndex, value);
                    parameterIndex++;
                }
            }

            System.out.println(statement.toString());
            // Exécution de la requête
            try {
                statement.executeUpdate();
            } catch (Exception e) {
                if (isTransactional) {
                    throw e;
                } else {
                    connection.close();
                    throw e;
                }
            }

            try {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Id idAnnotation = field.getAnnotation(Id.class);
                        if (idAnnotation != null) {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + field.getName().substring(0, 1).toUpperCase()
                                            + field.getName().substring(1), field.getType());
                            m.invoke(this, keys.getObject(1));
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                if (!isTransactional) {
                    connection.close();
                }
                throw e;

            }
        }
    }

    
    public void insertBatch(Connection connection, boolean isTransactional, T[] objects) throws Exception {
        if (objects == null || objects.length == 0) {
            throw new IllegalArgumentException("The array of objects to insert cannot be null or empty.");
        }

        if (isTransactional) {
            connection.setAutoCommit(false);
        }

        // Assuming all objects are of the same type
        String tableName = objects[0].getClass().getSimpleName();
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder();

        // Récupération des champs de la classe
        Field[] fields = objects[0].getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Ignore.class)) {
                columns.append(field.getName()).append(",");
            }
        }

        // Suppression de la virgule finale des chaînes de colonnes
        columns.deleteCharAt(columns.length() - 1);
        columns.append(")");

        // Construction des placeholders de valeurs
        StringBuilder placeholder = new StringBuilder("(");
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Ignore.class)) {
                placeholder.append("?,");
            }
        }
        placeholder.deleteCharAt(placeholder.length() - 1).append(")");

        for (int i = 0; i < objects.length; i++) {
            values.append(placeholder).append(",");
        }
        values.deleteCharAt(values.length() - 1);

        // Complétion de la requête SQL
        StringBuilder sql = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(columns)
                .append(" VALUES ")
                .append(values);

        // Préparation de la requête PreparedStatement
        try (PreparedStatement statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            int parameterIndex = 1;
            for (T obj : objects) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (!field.isAnnotationPresent(Ignore.class)) {
                        statement.setObject(parameterIndex, field.get(obj));
                        parameterIndex++;
                    }
                }
            }

            System.out.println(statement.toString());
            // Exécution de la requête
            try {
                statement.executeUpdate();
            } catch (Exception e) {
                if (isTransactional) {
                    connection.rollback();
                    throw e;
                } else {
                    connection.close();
                    throw e;
                }
            }

            // Attribution des valeurs générées aux champs annotés par @Id
            try (ResultSet keys = statement.getGeneratedKeys()) {
                for (T obj : objects) {
                    if (keys.next()) {
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (field.isAnnotationPresent(Id.class)) {
                                Method m = obj.getClass().getDeclaredMethod("set" + 
                                    field.getName().substring(0, 1).toUpperCase() + 
                                    field.getName().substring(1), field.getType());
                                m.invoke(obj, keys.getObject(1));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (!isTransactional) {
                    connection.close();
                }
                throw e;
            }
        }
    }

    public void deleteWhere(Connection connection, boolean isTransactional, String condition) throws Exception {
        try {
            connection.setAutoCommit(false);

            String request = "delete from " + getClassName() + " where " + condition;
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

            StringBuilder request = new StringBuilder("UPDATE ").append(getClassName()).append(" SET ");
            Field[] fields = getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                boolean isAnnotationPresent = fields[i].isAnnotationPresent(Ignore.class);
                if (fields[i].get(this) != null && !isAnnotationPresent) {
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
                boolean isAnnoted = field.isAnnotationPresent(Ignore.class);
                if (value != null && !isAnnoted) {
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
            String request = "SELECT * FROM " + getClassName() + " LIMIT " + nElementsPerPage + " OFFSET "
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
                        } catch (NullPointerException e1) {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), fields[i].getType());
                            m.invoke(t, obj);
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
            String request = "SELECT * FROM " + getClassName() + " WHERE " + where + " LIMIT "
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
                        } catch (NullPointerException e1) {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), fields[i].getType());
                            m.invoke(t, obj);
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
                        } catch (NullPointerException e1) {
                            Method m = getClass()
                                    .getDeclaredMethod("set" + fields[i].getName().substring(0, 1).toUpperCase()
                                            + fields[i].getName().substring(1), fields[i].getType());
                            m.invoke(t, obj);
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

    public void updateById(Connection connection, boolean isTransactional) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);

            StringBuilder request = new StringBuilder("UPDATE ").append(getClassName()).append(" SET ");
            Field[] fields = getClass().getDeclaredFields();
            String condition = null;

            for (Field field : fields) {
                field.setAccessible(true);
                Id idAnnotation = field.getAnnotation(Id.class);
                if (idAnnotation != null) {
                    condition = field.getName() + " = ?";
                } else {
                    boolean isAnnotationPresent = field.isAnnotationPresent(Ignore.class);
                    if (field.get(this) != null && !isAnnotationPresent) {
                        request.append(field.getName()).append(" = ?, ");
                    }
                }
            }

            Objects.requireNonNull(condition, "No field annotated with @Id found");

            request.deleteCharAt(request.lastIndexOf(",")).append(" WHERE ").append(condition);

            preparedStatement = connection.prepareStatement(request.toString());

            int parameterIndex = 1;
            Object idValue = null;
            for (Field field : fields) {
                field.setAccessible(true);
                Id idAnnotation = field.getAnnotation(Id.class);
                if (idAnnotation != null) {
                    idValue = field.get(this);
                } else {
                    Object value = field.get(this);
                    boolean isAnnotationPresent = field.isAnnotationPresent(Ignore.class);

                    if (value != null && !isAnnotationPresent) {
                        preparedStatement.setObject(parameterIndex, value);
                        parameterIndex++;
                    }
                }
            }
            preparedStatement.setObject(parameterIndex, idValue);
            System.out.println(preparedStatement.toString());
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

}