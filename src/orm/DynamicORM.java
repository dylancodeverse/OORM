package orm;

import java.sql.Connection;

public class DynamicORM<T> extends DynamicConnection<T> {

    protected String defaultFileName = "dynamic-connection.txt";

    @Override
    public Connection getConnection() {
        // mitady anle informations de connection anatinle projet

        throw new UnsupportedOperationException("Unimplemented method 'getConnection'");
    }

}
