package Controllers.Logic;

/**
 *
 * @author evgero
 */

import java.sql.Connection;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

/**
 *
 * @author Home
 */
public class EstablishConnection {
    
    public Connection EstablishDBConnection(){
    
       Connection conn = null;
      
       String uName = "APP";
       String uPass = "Turtle10#";       
        
        /*MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("scott");
        dataSource.setPassword("tiger");
        dataSource.setURL("//localhost:3306/C:/mysql/employees");
        dataSource.setServerName("myDBHost.example.org");
        Connection conn = dataSource.getConnection();*/
        
        // Use the already made DataSource with Connection Pool to Connect
        
        try {
        javax.naming.Context context = new javax.naming.InitialContext ();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup ("jdbc/timerDerbyDatasource");
        conn = dataSource.getConnection (uName, uPass);
        }
        catch (SQLException err){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", err.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
       
        }
       catch (NamingException err1){
            FacesMessage msg1 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Class Not Found", err1.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg1);
       }
       return conn;
}
}


