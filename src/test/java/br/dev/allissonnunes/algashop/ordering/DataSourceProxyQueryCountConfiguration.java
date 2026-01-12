package br.dev.allissonnunes.algashop.ordering;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryCountStrategy;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.listener.ThreadQueryCountHolder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@TestConfiguration
public class DataSourceProxyQueryCountConfiguration {

    @Bean
    QueryCountStrategy queryCountStrategy() {
        return new ThreadQueryCountHolder();
    }

    @Bean
    QueryTypeExtractor queryTypeExtractor() {
        return new JSqlParserQueryTypeExtractor();
    }

    @Bean
    QueryExecutionListener datasourceQueryCountListener(final QueryCountStrategy queryCountStrategy, final QueryTypeExtractor queryTypeExtractor) {
        final DataSourceQueryCountExecutionListener dataSourceQueryCountExecutionListener = new DataSourceQueryCountExecutionListener();
        dataSourceQueryCountExecutionListener.setQueryCountStrategy(queryCountStrategy);
        dataSourceQueryCountExecutionListener.setQueryTypeExtractor(queryTypeExtractor);
        return dataSourceQueryCountExecutionListener;
    }

    @FunctionalInterface
    public interface QueryTypeExtractor {

        QueryType extract(String query);

    }

    public static class DefaultQueryTypeExtractor implements QueryTypeExtractor {

        @Override
        public QueryType extract(final String query) {
            return QueryUtils.getQueryType(query);
        }

    }

    public static class JSqlParserQueryTypeExtractor implements QueryTypeExtractor {

        @Override
        public QueryType extract(final String query) {
            try {
                final Statement stmt = CCJSqlParserUtil.parse(query);

                return switch (stmt) {
                    case Select _ -> QueryType.SELECT;
                    case Insert _ -> QueryType.INSERT;
                    case Update _ -> QueryType.UPDATE;
                    case Delete _ -> QueryType.DELETE;
                    default -> QueryType.OTHER;
                };
            } catch (JSQLParserException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static class DataSourceQueryCountExecutionListener implements QueryExecutionListener {

        private QueryCountStrategy queryCountStrategy = new ThreadQueryCountHolder();

        private QueryTypeExtractor queryTypeExtractor = new DefaultQueryTypeExtractor();

        @Override
        public void beforeQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {

        }

        @Override
        public void afterQuery(final ExecutionInfo execInfo, final List<QueryInfo> queryInfoList) {
            final String dataSourceName = execInfo.getDataSourceName();

            final QueryCount count = this.queryCountStrategy.getOrCreateQueryCount(dataSourceName);

            // increment db call
            count.incrementTotal();
            if (execInfo.isSuccess()) {
                count.incrementSuccess();
            } else {
                count.incrementFailure();
            }

            // increment elapsed time
            final long elapsedTime = execInfo.getElapsedTime();
            count.incrementTime(elapsedTime);

            // increment statement type
            count.increment(execInfo.getStatementType());

            // increment query count
            for (final QueryInfo queryInfo : queryInfoList) {
                final String query = queryInfo.getQuery();
                final QueryType type = this.queryTypeExtractor.extract(query);
                count.increment(type);
            }
        }

        public QueryCountStrategy getQueryCountStrategy() {
            return queryCountStrategy;
        }

        public void setQueryCountStrategy(final QueryCountStrategy queryCountStrategy) {
            this.queryCountStrategy = queryCountStrategy;
        }

        public QueryTypeExtractor getQueryTypeExtractor() {
            return queryTypeExtractor;
        }

        public void setQueryTypeExtractor(final QueryTypeExtractor queryTypeExtractor) {
            this.queryTypeExtractor = queryTypeExtractor;
        }

    }

}
