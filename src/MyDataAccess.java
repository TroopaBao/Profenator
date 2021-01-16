import java.sql.*;

public class MyDataAccess {

	private String _usuario="vil";
	private String _pwd= "secreta";
	private static String _bd="profenator";
	//static String _url = "jdbc:mysql://localhost/"+_bd;
	static String _url="jdbc:mysql://localhost/"+_bd+"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	public Connection conn = null;
	public boolean entrar=false;
	public MyDataAccess() {
		System.out.println("Conectando...");
		try{
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(_url, _usuario, _pwd);
			System.out.println("Conexi�n a base de datos  . . . Ok");
		
			entrar=true;
		}
		catch(SQLException ex)
		{
			System.out.println("Hubo un problema al intentar conectarse a la base de datos"+_url);
			entrar=false;
		}
		catch(ClassNotFoundException ex)
		{
			System.out.println(ex);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}            
	}
	
	public ResultSet getQuery(String _query)
	{
		Statement state = null;
		ResultSet resultado = null;
		try{
			state = (Statement) conn.createStatement();
			resultado = state.executeQuery(_query);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		
		}
		return resultado;
	}
	
	public void setQuery(String _query){

		Statement state = null;
		
		try{			
			state=(Statement) conn.createStatement();
			state.execute(_query);

         } catch (SQLException e){
            e.printStackTrace();
       }
	}
	
	public void OpenConn()
	{
		try{
			conn = DriverManager.getConnection(_url, _usuario, _pwd);
			System.out.println("Conexi�n a base de datos  . . . Ok");
		}
		catch(SQLException ex)
		{
			System.out.println("Hubo un problema al intentar conectarse a la base de datos"+_url);
		}
	}
	
	public void CloseConn()
	{
		if (conn != null) 
		{
			try 
			{
	            conn.close();
	        }
			catch ( Exception e ) 
			{
	            System.out.println( e.getMessage());
	        } 
        }
		
	}
	
	public static void main(String[] args) {
		MyDataAccess conexion = new MyDataAccess();
		
		ResultSet resultado;
		String nombres, especialidad;
		
		resultado = conexion.getQuery("select nombre, areaespecial from Profesores");
		
		try {
			while(resultado.next()){
				nombres = resultado.getString("nombre");
				especialidad=resultado.getString("areaespecial");
				System.out.println("nombre: "+nombres+"/ Especialidad: "+especialidad);
			}
			conexion.CloseConn();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}