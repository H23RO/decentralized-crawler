package ro.h23.dars.retrievalcore.service;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomMySQL8Dialect extends MySQL8Dialect {

    public CustomMySQL8Dialect() {
        super();
        registerFunction("regexp", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1 REGEXP ?2"));
    }
}
