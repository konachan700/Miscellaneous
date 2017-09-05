package org.foximager.db;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.foximager.dao.StorageFile;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class Hibernate {
    private static enum ThreadState {
        LOCKED, FREE
    }
    
    private volatile static Hibernate
            myInstance = null;
    
    private volatile static boolean
            inited = false;
    
    private final ConcurrentHashMap<Thread, Session>
            openedSessions = new ConcurrentHashMap<>();
    
    private final ConcurrentHashMap<Thread, ThreadState>
            lockedThreads = new ConcurrentHashMap<>();
    
    private final SessionFactory
            sessionFactory;
    
    private Hibernate(File storageDir) {
        try {
            final Properties prop = new Properties();
            
            prop.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            prop.setProperty("hibernate.connection.url", "jdbc:h2:" + storageDir.getAbsolutePath() + File.separator + "database");
            prop.setProperty("hibernate.hbm2ddl.auto", "update");
            prop.setProperty("hibernate.connection.username", "foximager");
            prop.setProperty("hibernate.connection.password", "1234567890");
            prop.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            
            prop.setProperty("connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
            prop.setProperty("hibernate.c3p0.acquire_increment", "1");
            prop.setProperty("hibernate.c3p0.idle_test_period", "30");
            prop.setProperty("hibernate.c3p0.min_size", "1");
            prop.setProperty("hibernate.c3p0.max_size", "2");
            prop.setProperty("hibernate.c3p0.max_statements", "50");
            prop.setProperty("hibernate.c3p0.timeout", "0");
            prop.setProperty("hibernate.c3p0.acquireRetryAttempts", "1");
            prop.setProperty("hibernate.c3p0.acquireRetryDelay", "250");
            
            prop.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
            prop.setProperty("hibernate.current_session_context_class", "thread");
            
            final Configuration conf = new Configuration()                    
                    .addProperties(prop)
                    .addAnnotatedClass(StorageFile.class)
                    .configure();

            final StandardServiceRegistry sr = new StandardServiceRegistryBuilder()
                    .applySettings(conf.getProperties())
                    .build();
            
            sessionFactory = conf.buildSessionFactory(sr);
            
         } catch (org.hibernate.HibernateException ex) {
             throw new ExceptionInInitializerError(ex);
        } 
    }
    
    private Session getOrCreateSession() {
        if (!inited) 
            throw new RuntimeException("Hibernate isn't init now!");
        
        if (!openedSessions.containsKey(Thread.currentThread())) {
            try {
                synchronized (openedSessions) {
                    final Session currSession = sessionFactory.openSession();
                    openedSessions.put(Thread.currentThread(), currSession);

                    if (!lockedThreads.containsKey(Thread.currentThread())) {
                        lockedThreads.put(Thread.currentThread(), ThreadState.FREE);
                    }

                    return currSession;
                }
            } catch (Throwable ex) {
                 throw new RuntimeException("Can't create new hibernate session.");
            }
        } else {
            return openedSessions.get(Thread.currentThread());
        }
    }
    
    private synchronized boolean isThreadLocked() {
        return (lockedThreads.get(Thread.currentThread()) == ThreadState.LOCKED);
    }
    
    private synchronized void lockThread(ThreadState lock) {
        lockedThreads.put(Thread.currentThread(), lock);
    }
    
    private synchronized void closeSessions() {
        inited = false;
        openedSessions.values().forEach(session -> {
            session.close();
        });
        sessionFactory.close();
    }

    public static void init(File storageDir) {
        if (myInstance == null) 
            myInstance = new Hibernate(storageDir);
        inited = true;
    }
    
    public static boolean isInit() {
        return inited;
    }
    
    public static void dispose() {
        myInstance.closeSessions();
    }
    
    public static void beginTransaction() {
        myInstance.lockThread(ThreadState.LOCKED); 
        myInstance.getOrCreateSession().beginTransaction();
    }

    public static void endTransaction() {
        myInstance.getOrCreateSession().getTransaction().commit();
        myInstance.lockThread(ThreadState.FREE);
    }
    
    public static void pushObjectInTransaction(Object o) {
        myInstance.getOrCreateSession().save(o);
    }
    
    public static synchronized void pushObject(Object o) throws HibernateException {
        if (myInstance.isThreadLocked()) {
            throw new HibernateException("Thread locked for another transaction now");
        }
        
        beginTransaction();
        pushObjectInTransaction(o);
        endTransaction();
    }
    
    public static void removeObjectInTransaction(Object o) {
        myInstance.getOrCreateSession().delete(o);
    }
    
    public static synchronized void removeObject(Object o) throws HibernateException {
        if (myInstance.isThreadLocked()) {
            throw new HibernateException("Thread locked for another transaction now");
        }
        
        beginTransaction();
        removeObjectInTransaction(o);
        endTransaction();
    }
    
    public static List getFullList(Class type) {
        return myInstance.getOrCreateSession()
                .createCriteria(type)
                .list();
    }
    
    public static List getPartOfList(Class type, int from, int to) {
        return myInstance.getOrCreateSession()
                .createCriteria(type)
                .setFirstResult(from)
                .setMaxResults(to)
                .list();
    }
    
    public static Session getSession() {
        return myInstance.getOrCreateSession();
    }
}
