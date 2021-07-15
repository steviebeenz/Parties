package com.alessiodp.parties.common.storage;

import com.alessiodp.core.common.storage.sql.connection.MariaDBConnectionFactory;

public class LockedMariaDBConnectionFactory extends MariaDBConnectionFactory {

    private boolean hadInitialized = false;

    @Override
    public void init() {
        if (!hadInitialized) {
            super.init();
            hadInitialized = true;
        }
    }
}
