/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Home
 */
public class EstablishConnection {
    
    public Connection EstablishDBConnection(Connection conn){
    
       //Connection con = null;
       //String host = "jdbc:derby://localhost:1527/Employees";
       String uName = "APP";
       String uPass = "Turtle10#";
       //String host = "jdbc:derby://10.1.1.168:1527/C:/derby_10/employees";  
       //String host = "jdbc:derby://localhost:1527/C:/payara41/javadb/employees";
       String host = "jdbc:derby://localhost:1527/C:/derby_10/employees";
       //String host = "jdbc:derby://10.1.1.23:1527/C:/derby_10/employees";
        
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
        ErrorStage.showErrorStage(err.getMessage());
    }
       catch (ClassNotFoundException err1){
        ErrorStage.showErrorStage(err1.getMessage()); 
       }
       return conn;
}
}

