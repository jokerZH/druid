package com.alibaba.druid.stat;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.alibaba.druid.util.JMXUtils;


/* TODO */
public class JdbcConnectionEntry implements JdbcConnectionEntryMBean {
    private long id;
    private long establishTime;
    private long establishNano;
    private Date connectTime;
    private long connectTimespanNano;
    private Exception connectStackTraceException;

    private String lastSql;
    private Exception lastStatementStatckTraceException;
    protected Throwable lastError;
    protected long lastErrorTime;
    private final String dataSource;

    public JdbcConnectionEntry(String dataSource, long connectionId) {
        this.id = connectionId;
        this.dataSource = dataSource;
    }

    public void reset() {
        this.lastSql = null;
        this.lastStatementStatckTraceException = null;
        this.lastError = null;
        this.lastErrorTime = 0;
    }

    public Date getEstablishTime() {
        if (establishTime <= 0) {
            return null;
        }
        return new Date(establishTime);
    }
    public void setEstablishTime(long establishTime) { this.establishTime = establishTime; }
    public long getEstablishNano() { return establishNano; }
    public void setEstablishNano(long establishNano) { this.establishNano = establishNano; }
    public Date getConnectTime() { return connectTime; }
    public void setConnectTime(Date connectTime) { this.connectTime = connectTime; }
    public long getConnectTimespanNano() { return connectTimespanNano; }
    public void setConnectTimespanNano(long connectTimespanNano) { this.connectTimespanNano = connectTimespanNano; }
    public String getLastSql() { return lastSql; }
    public void setLastSql(String lastSql) { this.lastSql = lastSql; }

    public String getConnectStackTrace() {
        if (connectStackTraceException == null) {
            return null;
        }

        StringWriter buf = new StringWriter();
        connectStackTraceException.printStackTrace(new PrintWriter(buf));
        return buf.toString();
    }

    public void setConnectStackTrace(Exception connectStackTraceException) {
        this.connectStackTraceException = connectStackTraceException;
    }

    public String getLastStatementStatckTrace() {
        if (lastStatementStatckTraceException == null) {
            return null;
        }

        StringWriter buf = new StringWriter();
        lastStatementStatckTraceException.printStackTrace(new PrintWriter(buf));
        return buf.toString();
    }

    public void setLastStatementStatckTrace(Exception lastStatementStatckTrace) {
        this.lastStatementStatckTraceException = lastStatementStatckTrace;
    }

    public void error(Throwable lastError) {
        this.lastError = lastError;
        this.lastErrorTime = System.currentTimeMillis();
    }

    public Date getLastErrorTime() {
        if (lastErrorTime <= 0) {
            return null;
        }
        return new Date(lastErrorTime);
    }

    private static String[] indexNames = {"ID", "ConnectTime", "ConnectTimespan", "EstablishTime",
            "AliveTimespan", "LastSql", "LastError", "LastErrorTime",
            "ConnectStatckTrace", "LastStatementStackTrace", "DataSource"};
    private static String[] indexDescriptions = indexNames;

    public static CompositeType getCompositeType() throws JMException {
        OpenType<?>[] indexTypes = new OpenType<?>[]{SimpleType.LONG, SimpleType.DATE, SimpleType.LONG,
                SimpleType.DATE, SimpleType.LONG,

                SimpleType.STRING, JMXUtils.getThrowableCompositeType(), SimpleType.DATE, SimpleType.STRING,
                SimpleType.STRING,

                SimpleType.STRING};

        return new CompositeType("ConnectionStatistic", "Connection Statistic", indexNames, indexDescriptions,
                indexTypes);
    }

    public String getDataSource() { return this.dataSource; }

    public CompositeDataSupport getCompositeData() throws JMException {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("ID", id);
        map.put("ConnectTime", getConnectTime());
        map.put("ConnectTimespan", getConnectTimespanNano() / (1000 * 1000));
        map.put("EstablishTime", getEstablishTime());
        map.put("AliveTimespan", (System.nanoTime() - getEstablishNano()) / (1000 * 1000));

        map.put("LastSql", getLastSql());
        map.put("LastError", JMXUtils.getErrorCompositeData(this.lastError));
        map.put("LastErrorTime", getLastErrorTime());
        map.put("ConnectStatckTrace", getConnectStackTrace());
        map.put("LastStatementStackTrace", getLastStatementStatckTrace());

        map.put("DataSource", this.getDataSource());

        return new CompositeDataSupport(getCompositeType(), map);
    }
}
