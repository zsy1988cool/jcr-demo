package com.ylz.jckdemo.database;

import org.apache.jackrabbit.core.persistence.PMContext;

import static com.ylz.jckdemo.repository.RepositoryBuilder.getDatabaseType;

public class MySqlPersistenceManager extends DbPersistenceManager {

    /**
     * {@inheritDoc}
     */
    public void init(PMContext context) throws Exception {
        // init default values
        if (getDriver() == null) {
            setDriver("org.gjt.mm.mysql.Driver");
        }
        if (getDatabaseType() == null) {
            setDatabaseType("mysql");
        }
        super.init(context);
    }

}
