package Controllers.Logic;

/**
 *
 * @author evgero
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Home
 */
public class EstablishConnection {
    
    public Connection EstablishDBConnection(){
    
       Connection conn = null;
       //String host = "jdbc:derby://localhost:1527/Employees";
       String uName = "APP";
       String uPass = "Turtle10#";
       //String host = "jdbc:derby://10.1.1.168:1527/C:/derby_10/employees";  
       //String host = "jdbc:derby://localhost:1527/C:/payara41/javadb/employees";
       //String host = "jdbc:derby://localhost:1527/C:/derby_10/employees";
       String host = "jdbc:derby://10.1.1.29:1527/C:/derby_10/employees";
        
        /*MysqlDataSource dataSource = new MysqlDataSource();
        //dataSource.setUser("scott");
        //dataSource.setPassword("tiger");
        dataSource.setURL("//localhost:3306/C:/mysql/employees");
        //dataSource.setServerName("myDBHost.example.org");

        Connection conn = dataSource.getConnection();*/
        try {
       /*Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance(); */ 
       //Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
       Class.forName("org.apache.derby.jdbc.ClientDriver");
       /*DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());*/
                
       conn = DriverManager.getConnection(host, uName, uPass);
       //con = DriverManager.getConnection(host);
        }
        catch (SQLException err){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", err.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
       
        }
       catch (ClassNotFoundException err1){
            FacesMessage msg1 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Class Not Found", err1.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg1);
       }
       return conn;
}
}


