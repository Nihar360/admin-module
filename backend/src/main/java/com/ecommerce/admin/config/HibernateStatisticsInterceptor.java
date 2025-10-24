package com.ecommerce.admin.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

/**
 * Aspect to monitor query execution and detect N+1 problems
 * This will log statistics after each service method execution
 */
@Slf4j
@Aspect
@Component
public class HibernateStatisticsInterceptor {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public HibernateStatisticsInterceptor(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
    
    @Around("execution(* com.ecommerce.admin.service.OrderService.getOrders(..))")
    public Object logQueryStatistics(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // Get Hibernate statistics
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        
        // Clear previous statistics
        stats.clear();
        
        // Execute the method
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Log statistics
        logStatistics(stats, executionTime, joinPoint.getSignature().toShortString());
        
        return result;
    }
    
    private void logStatistics(Statistics stats, long executionTime, String methodName) {
        log.info("=================================================");
        log.info("Query Statistics for: {}", methodName);
        log.info("=================================================");
        log.info("Execution Time: {} ms", executionTime);
        log.info("Queries Executed: {}", stats.getQueryExecutionCount());
        log.info("Entities Loaded: {}", stats.getEntityLoadCount());
        log.info("Collections Loaded: {}", stats.getCollectionLoadCount());
        log.info("Cache Hits: {}", stats.getSecondLevelCacheHitCount());
        log.info("Cache Misses: {}", stats.getSecondLevelCacheMissCount());
        
        // Detect potential N+1 problem
        if (stats.getQueryExecutionCount() > 5) {
            log.warn("⚠️  POTENTIAL N+1 PROBLEM DETECTED!");
            log.warn("⚠️  {} queries executed - consider using JOIN FETCH or EntityGraph", 
                     stats.getQueryExecutionCount());
        } else {
            log.info("✅ Query count is optimal ({})", stats.getQueryExecutionCount());
        }
        
        // Log query execution details
        String[] queries = stats.getQueries();
        if (queries != null && queries.length > 0) {
            log.info("Executed Queries:");
            for (String query : queries) {
                log.info("  - {}: {} executions", 
                         query, 
                         stats.getQueryStatistics(query).getExecutionCount());
            }
        }
        
        log.info("=================================================");
    }
}


/**
 * Alternative: Simple logging filter to count queries per request
 */
@Slf4j
@Component
class QueryCountFilter implements jakarta.servlet.Filter {
    
    private static final ThreadLocal<Integer> queryCount = ThreadLocal.withInitial(() -> 0);
    
    @Override
    public void doFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            jakarta.servlet.FilterChain chain) 
            throws java.io.IOException, jakarta.servlet.ServletException {
        
        queryCount.set(0);
        
        try {
            chain.doFilter(request, response);
        } finally {
            int count = queryCount.get();
            if (count > 0) {
                log.info("Total queries executed in this request: {}", count);
                if (count > 5) {
                    log.warn("⚠️  High query count detected: {} queries", count);
                }
            }
            queryCount.remove();
        }
    }
    
    public static void incrementQueryCount() {
        queryCount.set(queryCount.get() + 1);
    }
}