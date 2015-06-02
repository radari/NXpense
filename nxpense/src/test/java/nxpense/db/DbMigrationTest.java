package db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DbMigrationTest {

	/*
	 * This test is intended to validate syntax and consistency across SQL migration files. 
	 */
	@Test
	public void test_01_FywayMigration() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:migration-test");

		Flyway flyway = new Flyway();
		flyway.setLocations("classpath:/db/migration");
		flyway.setDataSource(dataSource);
		flyway.migrate();

		// Uncomment the following code block to open the H2 GUI console to browse the resulting 
		// generated schema
//		try {
//			org.h2.tools.Server.createTcpServer().start();
//			java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:migration-test");
//			org.h2.tools.Server.startWebServer(conn);
//		} catch (java.sql.SQLException e) {
//			e.printStackTrace();
//		}
	}

	/*
	 *  This test is intended to validate that the domain classes are consistent with the 
	 *  underlying database schema structure, as the DB schema is created through migration scripts 
	 *  instead of having it automatically generated by the persistence provider.  
	 */
	@Test
	public void test_02_DomainDefinitionValidation() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("nxpense");
		EntityManager em = emf.createEntityManager();
		em.close();
		emf.close();
	}
}
