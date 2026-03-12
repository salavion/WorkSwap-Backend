package org.workswap.core.testers;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryCounter implements StatementInspector {

    private static final Logger logger = LoggerFactory.getLogger(QueryCounter.class);
    private int count = 0;

    @Override
    public String inspect(String sql) {
        count++;
        logger.debug("Query #{}: {}", count/* , sql */);
        return sql;
    }

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}
