package com.ylz.jckdemo.database;

public class MysqlFileSystem extends BaseDbFileSystem {
    @Override
    public String databaseType() {
        return "mysql";
    }
}
