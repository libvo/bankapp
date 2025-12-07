package ru.home.bankapp.config;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.util.Properties;

public class DBConfig {

  public static DSLContext createDSL() {
    try {
      Properties props = new Properties();
      props.load(DBConfig.class.getClassLoader().getResourceAsStream("application.properties"));

      PGSimpleDataSource ds = new PGSimpleDataSource();
      ds.setURL(props.getProperty("db.url"));
      ds.setUser(props.getProperty("db.user"));
      ds.setPassword(props.getProperty("db.password"));

      return DSL.using(ds, org.jooq.SQLDialect.POSTGRES);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load DB config", e);
    }
  }
}
