package com.alibaba.druid.stat;


import java.util.Date;

public interface JdbcConnectionEntryMBean {

    Date getEstablishTime();

    long getEstablishNano();

    Date getConnectTime();

    long getConnectTimespanNano();

    String getLastSql();

    String getConnectStackTrace();

    String getLastStatementStatckTrace();

    Date getLastErrorTime();

    void reset();
}
