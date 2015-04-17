package org.pentaho.di.core.database;

import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class DatabaseConnectUnitTest {

  static LoggingObjectInterface log = new SimpleLoggingObject( "junit", LoggingObjectType.GENERAL, null );
  String name = "testName";
  String jndiName = "testJNDIName";
  String fullJndiName = "jdbc/testJNDIName";
  String displayName = "testDisplayName";

  @BeforeClass
  public static void beforeClass() throws NamingException {
    if ( !NamingManager.hasInitialContextFactoryBuilder() ) {
      // If JNDI is not initialized, use simpleJNDI
      System.setProperty( Context.INITIAL_CONTEXT_FACTORY, "org.osjava.jndi.PropertiesFactory" );
      InitialContextFactoryBuilder simpleBuilder = new SimpleNamingContextBuilder();
      NamingManager.setInitialContextFactoryBuilder( simpleBuilder );
    }
  }

  @After
  public void after() throws NamingException {
    InitialContext ctx = new InitialContext();
    ctx.unbind( fullJndiName );
  }

  @Test
  public void testConnect2() throws SQLException, NamingException, KettleDatabaseException {
    InitialContext ctx = new InitialContext();

    DatabaseMeta meta = Mockito.mock( DatabaseMeta.class );
    Mockito.when( meta.getName() ).thenReturn( name );
    Mockito.when( meta.getDatabaseName() ).thenReturn( jndiName );
    Mockito.when( meta.getDisplayName() ).thenReturn( displayName );
    Mockito.when( meta.getAccessType() ).thenReturn( DatabaseMeta.TYPE_ACCESS_JNDI );
    Mockito.when( meta.environmentSubstitute( jndiName ) ).thenReturn( jndiName );

    Connection connection = mock( Connection.class );
    TestDataSource ds = new TestDataSource( connection );
    ctx.bind( fullJndiName, ds );

    Database db = new Database( log, meta );

    db.connect();
    Assert.assertEquals( connection, db.getConnection() );
  }

}
