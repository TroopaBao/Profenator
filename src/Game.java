import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Game extends JFrame
{
	JTextField ct1, ct2, ct3;
	Home inicio;
	Preguntas preg;
	Respuesta resp;
	AgregarPersonaje agre;
	Finalizar fin;
	Datos D;
	
	public Game()
	{
		setSize(700, 950);
		setTitle("Profenator: The Game");
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);		
		
		
		inicio=new Home(this);
		inicio.setBounds(0, 0, 700, 950);
		add(inicio);
		inicio.setVisible(true);
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void Cambio(int ventana, Game game, Datos dat)
	{
		//Ventana  0.- Inicio    1.- Preguntas    2.- Respuesta   3.-  Insertar Nuevo Personaje  4.- Finalizar 
		if(ventana==0)
		{   //Inicio
			fin.removeAll();
			inicio=new Home(game);
			inicio.setBounds(0, 0, 700, 950);
			game.add(inicio);
			inicio.updateUI();
			dat=new Datos();
			dat.ventanactual=0;
		}
		if(ventana==1)
		{   //Preguntas			
			if(dat.ventanactual==0)
			{
				inicio.removeAll();
			}
			if(dat.ventanactual==2)
			{
				resp.removeAll();
				dat.cantnoc=0;
			}
			preg=new Preguntas(game, dat);
			preg.setBounds(0, 0, 700, 950);
			game.add(preg);
			preg.updateUI();
			dat.ventanactual=1;
		}
		if(ventana==2)
		{   //Respuesta
			preg.removeAll();
			resp=new Respuesta(game, dat);
			resp.setBounds(0, 0, 700, 950);
			game.add(resp);
			resp.updateUI();
			dat.ventanactual=2;
		}
		if(ventana==3)
		{   //Insertar Nuevo Personaje 
			resp.removeAll();
			agre=new AgregarPersonaje(game, dat);
			agre.setBounds(0, 0, 700, 950);
			game.add(agre);
			agre.updateUI();
			dat.ventanactual=3;
		}
		if(ventana==4)
		{   //Finalizar
			dat.fin=true;
			int pos=0;
			String mensaje="";
			if(dat.ventanactual==1)
			{ //5 No estoy seguro
				preg.removeAll();
				pos=2;
				mensaje="Para poder adivinar a tu profesor, debes conocerlo m�s... ";
			}
			if(dat.ventanactual==2)
			{ //Adivinamos su personaje
				resp.removeAll();
				pos=0;
				mensaje="�Genial! Si que soy muy bueno";
			}
			if(dat.ventanactual==3)
			{ //Agreg� su personaje
				agre.removeAll();
				pos=1;
				mensaje="�Vaya! S� que me la pusiste dif�cil...";
			}
			fin=new Finalizar(mensaje, pos, game);
			fin.setBounds(0, 0, 700, 950);
			game.add(fin);
			fin.updateUI();
			dat.ventanactual=4;
		}
	}
	public static void main(String[] args) {
		new Game();
	}

}

class Home extends JPanel
{
	JButton jugar;
	JLabel l1;
	Game game;
	Datos D;
	public Home(Game g)
	{
		setBackground(Color.orange);
		game=g;
		jugar=new JButton("Jugar");
		l1=new JLabel("Piensa en tu personaje del ITJ, tratar� de adivinarlo...");
		setLayout(null);
		
		l1.setFont(new Font(Font.SANS_SERIF, 10, 25));
		
		l1.setBounds(50, 200, 600, 100);
		jugar.setBounds(200, 500, 300, 300);
		
		add(l1); add(jugar);
		
		jugar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				D=new Datos();
				D.Conexi�n();
				D.data.CloseConn();
				if(D.data.entrar)
				{
					System.out.println("Hola...");
					game.Cambio(1, game, D);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "No hubo conexi�n con la base de datos..." );
				}
			}
		});
	}
}

class Preguntas extends JPanel
{
	JButton bsi, bno, bnoc;
	JLabel nopregunta;
	JTextArea pregunta;
	Game g;
	Datos D;
	int contadorcomplemento=0, cont=0, contno=0, contnoc=0, contpregunta=1, ordenpreguntas[], ordeny[]=new int [1], posx=0, posy=0, tama�opciones=0;
	boolean banderarial=false,preguntacabello=false, cabellotiene=false, preguntaespecialidad=false,
			unapregunta=false, numpreg=false, sinopreg=false, opcion4=false, opcion8=false;
	String opciones_consulta[], complemento="";
	
	public Preguntas(Game game, Datos datos)
	{
		D=datos;
		g=game;
		
		setBackground(Color.yellow);
		bsi=new JButton("S�");
		bno=new JButton("No");
		bnoc=new JButton("No estoy seguro");
		
		
		ordenpreguntas=D.AleatorioX(D.preguntas);
		pregunta=new JTextArea();
		for (int i = 0; i < ordenpreguntas.length; i++) {
			posx=i;
			D.data.OpenConn();
			opciones_consulta=D.ConsultasPosicion(ordenpreguntas[i], complemento);
			D.data.CloseConn();
			
			if(opciones_consulta.length>1)
			{ //Podemos preguntarle al usuario
				//System.out.println("Vector x: "+ordenpreguntas[i]+", D.preguntas tam: "+D.preguntas[ordenpreguntas[i]].length+", D.opciones[posx]: "+D.opciones[ordenpreguntas[i]].length);
				if(D.preguntas[ordenpreguntas[i]].length==D.opciones[ordenpreguntas[i]].length)
				{//El mismo n�mero de opciones es el mismo n�mero de respuestas
					numpreg=true;
					ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
					pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], ordeny[posy], ""));
					break;
				}
				if(D.preguntas[ordenpreguntas[i]].length==1 && D.opciones[ordenpreguntas[i]].length==2)
				{ //Una sola pregunta, se responde con Si y No
					unapregunta=true;
					pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, ""));
					break;
				}
				if(ordenpreguntas[i]==4)
				{ //Color de cabello 
					opcion4=true;
					for (int j = 0; j < opciones_consulta.length; j++) {
						if(opciones_consulta[j].equalsIgnoreCase("No tiene"))
						{ //Buscar si hay un "No tiene" 
							preguntacabello=true;
							pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, ""));
							break;
						}
					}
					if(!preguntacabello)
					{//En la consulta todos tienen cabello 
						ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
						pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 1, D.opciones[4][ordeny[posy]]+"?"));
					}
					break;
				}
				if(ordenpreguntas[i]==8)
				{ //Especialidad
					opcion8=true;
					ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
					pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, D.areaespecial[ordeny[posy]]+"?"));
					break;
				}
			}
		}
		System.out.print("Vector Y: ");
		for (int i = 0; i < ordeny.length; i++) {
			System.out.print(ordeny[i]+" ");
		}
		System.out.println();
		nopregunta=new JLabel("Pregunta No. "+contpregunta);
		setLayout(null);
		
		pregunta.setOpaque(false);
		pregunta.setEditable(false);
		
		pregunta.setFont(new Font(Font.MONOSPACED, 10, 40));
		nopregunta.setFont(new Font(Font.DIALOG_INPUT, 10, 20));
		
		pregunta.setBounds(50, 150, 600, 400);
		bsi.setBounds(50, 650, 150, 150);
		bno.setBounds(280, 650, 150, 150);
		bnoc.setBounds(500, 650, 150, 150);
		nopregunta.setBounds(50, 50, 600, 60);
		add(pregunta); add(nopregunta); add(bsi); add(bno); add(bnoc);
		
		bsi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				boolean agrega=false;
				//System.out.println("Posx al entrar al evento: "+posx);
				//try
				{
					//System.out.println("Evento S�");
					if(numpreg)
					{//El mismo n�mero de opci�n es el mismo n�mero de respuesta
						D.resp[posx][0]=D.opciones[ordenpreguntas[posx]][ordeny[posy]];
						D.resp[posx][1]="Usuario";
						posx++;
						contadorcomplemento++;
						posy=0;
						numpreg=false;
						agrega=true;
						//System.out.println("Opci�n numpreg");
					}
					if(unapregunta)
					{ //1 sola pregunta, se responde con si y no 
						D.resp[posx][0]="Si";
						D.resp[posx][1]="Usuario";
						posx++;
						contadorcomplemento++;
						posy=0;
						unapregunta=false;
						agrega=true;
						//System.out.println("Opci�n unapregunta");
					}
					if(opcion4)	
					{ //Color de cabello 
						//System.out.println("Opci�n posx: 4");
						if(preguntacabello) //Existe una posibilidad de que el usuario responda de que el personaje tiene cabello
						{
							//System.out.println("Preguntacabello is true");
							if(cabellotiene) //El usuario ya seleccion� un color de cabello 
							{
								//System.out.println("Cabellotiene is true");
								D.resp[posx][0]=D.opciones[4][ordeny[posy]];
								D.resp[posx][1]="Usuario";
								posx++;
								contadorcomplemento++;
								posy=0;
								cabellotiene=false;
								preguntacabello=false;
								opcion4=false;
								agrega=true;
							}
							else //Apenas respondi� que S� tiene cabello 
							{
								D.data.OpenConn();
								opciones_consulta=D.ConsultasPosicion(4, complemento);
								D.data.CloseConn();
								ordeny=D.AleatorioY(opciones_consulta, 4);
								//System.out.println("Cabellotiene is false");
								if(ordeny.length>1) //Contiene m�s de un elemento, podemos preguntarle al usuario
								{
									//System.out.println("Contiene m�s de 1 elemento");
									cabellotiene=true;
									String preg=D.AjustarPreguntas(ordenpreguntas[posx], 1, D.opciones[4][ordeny[posy]]+"?");
									pregunta.setText(preg);
									contpregunta++;
									banderarial=true;
								}
								else //Solo queda 1 elemento, por lo tanto no hay necesidad de preguntar
								{
									D.resp[posx][0]=opciones_consulta[0];
									D.resp[posx][1]="Ultima Opcion";
									posx++;
									posy=0;
									preguntacabello=false;
									opcion4=false;
									//System.out.println("Solo hay 1 elemento");
								}
							}
						}
						else
						{ //En la consulta no est� la opci�n "No tiene"; Por lo tanto ya est� afirmando el color
							D.resp[posx][0]=D.opciones[4][ordeny[posy]];
							D.resp[posx][1]="Usuario";
							posx++;
							contadorcomplemento++;
							posy=0;
							opcion4=false;
							agrega=true;
							//System.out.println("Preguntacabello is false");
						}
					}
					if(opcion8)
					{ // Especialidad
						//System.out.println("Opci�n posx: 8");
						D.resp[posx][0]=D.opciones[ordenpreguntas[posx]][ordeny[posy]];
						D.resp[posx][1]="Usuario";
						posx++;
						contadorcomplemento++;
						posy=0;
						opcion8=false;
						agrega=true;
					}
					
					
					
					if(!banderarial)
					{
						System.out.println("Contador complemento: "+contadorcomplemento);
						if(agrega)
						{
							if(contadorcomplemento==1)
							{
								complemento=" where "+D.variablesql[ordenpreguntas[posx-1]]+"='"+D.resp[posx-1][0]+"' ";
							}
							else
							{
								complemento+="and "+D.variablesql[ordenpreguntas[posx-1]]+"='"+D.resp[posx-1][0]+"' ";
							}
						}
						
						System.out.println("Complemento: "+complemento);
						D.data.OpenConn();
						String opcionesprofe[]=D.ConsultasProfe(0, complemento);
						D.data.CloseConn();
						
						
						if(opcionesprofe.length==1 || posx>=ordenpreguntas.length)
						{ //S�lo tienes 1 posible resultado, mostrarlo 
							D.nombrepersonaje=opcionesprofe[0];
							System.out.println("Encontr� a un profe... Posx: "+posx);
							posx=0;
							game.Cambio(2, g, D);
						}
						//System.out.println("posx antes del for: "+posx);
						for (int i = posx; i < ordenpreguntas.length; i++) 
						{
							posx=i;
							//System.out.println("posx despu�s del for: "+posx);
							D.data.OpenConn();
							String opciones_consulta[]=D.ConsultasPosicion(ordenpreguntas[i], complemento);
							D.data.CloseConn();
							
							if(opciones_consulta.length>1)
							{ //Podemos preguntarle al usuario
								//System.out.println("Vector X: "+ordenpreguntas[i]+", D.preguntas tam: "+D.preguntas[ordenpreguntas[i]].length+", D.opciones[posx]: "+D.opciones[ordenpreguntas[i]].length);
								if(D.preguntas[ordenpreguntas[i]].length==D.opciones[ordenpreguntas[i]].length)
								{//El mismo n�mero de opciones es el mismo n�mero de respuestas
									numpreg=true;
									ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
									pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], ordeny[posy], ""));
									contpregunta++;
									break;
								}
								if(D.preguntas[ordenpreguntas[i]].length==1 && D.opciones[ordenpreguntas[i]].length==2)
								{ //Una sola pregunta, se responde con Si y No
									unapregunta=true;
									pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, ""));
									contpregunta++;
									break;
								}
								if(ordenpreguntas[i]==4)
								{ //Color de cabello
									opcion4=true;
									for (int j = 0; j < opciones_consulta.length; j++) {
										if(opciones_consulta[j].equalsIgnoreCase("No tiene"))
										{ //Buscar si hay un "No tiene" 
											preguntacabello=true;
											pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, ""));
											contpregunta++;
											break;
										}
									}
									if(!preguntacabello)
									{//En la consulta todos tienen cabello 
										ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
										pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 1, D.opciones[4][ordeny[posy]]+"?"));
										contpregunta++;
									}
									break;
								}
								if(ordenpreguntas[i]==8)
								{ //Especialidad
									opcion8=true;
									ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
									pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, D.areaespecial[ordeny[posy]]+"?"));
									contpregunta++;
									break;
								}
							}
						}
					//System.out.println("Contador (posici�n): "+posx);
					}
					else
					{
						banderarial=false;
					}
					System.out.println("(Si) Posx: "+posx+", contnoc: "+D.cantnoc+", tama�ox: "+ordenpreguntas.length);
				}
				/*catch (ArrayIndexOutOfBoundsException ex) {
					System.out.println("Se sali� del arreglo");
					game.Cambio(2, g, D);
				}*/
				nopregunta.setText("Pregunta No. "+contpregunta);
			}
		});
		bno.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean agrega=false, banarial=false; //Agrega es para los elementos que se pondr�n en la consulta 
				//(preguntadas al usuario); banarial para pasar al siguiente posx
				posy++;
				System.out.println("Posy: "+posy+", totalY: "+ordeny.length);
				if(numpreg)
				{ //El mismo n�mero de opci�n es el mismo n�mero de respuesta
					if(posy<ordeny.length-1)
					{ //A�n queda m�s de 1 opci�n, volvemos a preguntar
						pregunta.setText(D.AjustarPreguntas(ordenpreguntas[posx], ordeny[posy], ""));
						contpregunta++;
					}
					else
					{ //Solo queda 1 opci�n, por lo tanto lo ponemos como respuesta por �ltima opci�n
						D.resp[posx][0]=D.opciones[ordenpreguntas[posx]][ordeny[posy]];
						D.resp[posx][1]="Ultima Opcion";
						posx++;
						posy=0;
						unapregunta=false;
						banarial=true;
						if(ordenpreguntas[posx-1]==0)
						{//Masculino o Femenino
							contadorcomplemento++;
							agrega=true;
						}
					}
				}
				if(unapregunta)
				{ //Una sola pregunta, se responde con si o no 
					D.resp[posx][0]="No";
					D.resp[posx][1]="Usuario";
					posx++;
					contadorcomplemento++;
					posy=0;
					unapregunta=false;
					agrega=true;
					banarial=true;
				}
				if(opcion4)
				{ //Color de cabello 
					if(preguntacabello)
					{ //Le hicieron la pregunta de que si tiene cabello
						if(cabellotiene)
						{ //No es el color de cabello que el desea
							if(posy<ordeny.length-1)
							{ //A�n queda m�s de 1 opci�n, volvemos a preguntar
								pregunta.setText(D.AjustarPreguntas(ordenpreguntas[posx], 1, D.opciones[4][ordeny[posy]]+"?"));
								contpregunta++;
							}
							else
							{ //Solo queda 1 opci�n, por lo tanto lo ponemos como respuesta por �ltima opci�n
								D.resp[posx][0]=D.opciones[4][ordeny[posy]];
								D.resp[posx][1]="Ultima Opcion";
								posx++;
								posy=0;
								unapregunta=false;
								banarial=true;
							}
						}
						else
						{//Afirma que el personaje no tiene cabello
							D.resp[posx][0]="No tiene";
							D.resp[posx][1]="Usuario";
							posx++;
							contadorcomplemento++;
							posy=0;
							opcion4=false;
							agrega=true;
							banarial=true;
						}
					}
					else
					{ //No le hicieron la pregunta de que si tiene cabello
						if(posy<ordeny.length-1)
						{ //A�n queda m�s de 1 opci�n, volvemos a preguntar
							pregunta.setText(D.AjustarPreguntas(ordenpreguntas[posx], 1, D.opciones[4][ordeny[posy]]+"?"));
							contpregunta++;
						}
						else
						{ //Solo queda 1 opci�n, por lo tanto lo ponemos como respuesta por �ltima opci�n
							D.resp[posx][0]=D.opciones[4][ordeny[posy]];
							D.resp[posx][1]="Ultima Opcion";
							posx++;
							posy=0;
							unapregunta=false;
							banarial=true;
						}
					}
				}
				if(opcion8)
				{//Especialidad
					if(posy<ordeny.length-1)
					{ //A�n queda m�s de 1 opci�n, volvemos a preguntar
						pregunta.setText(D.AjustarPreguntas(ordenpreguntas[posx], 0, D.areaespecial[ordeny[posy]]+"?"));
						contpregunta++;
					}
					else
					{ //Solo queda 1 opci�n, por lo tanto lo ponemos como respuesta por �ltima opci�n
						D.resp[posx][0]=D.opciones[ordenpreguntas[posx]][ordeny[posy]];
						D.resp[posx][1]="Ultima Opcion";
						posx++;
						posy=0;
						unapregunta=false;
						banarial=true;
					}
				}
				
				if(agrega)
				{
					if(contadorcomplemento==1)
					{
						complemento=" where "+D.variablesql[ordenpreguntas[posx-1]]+"='"+D.resp[posx-1][0]+"' ";
					}
					else
					{
						complemento+="and "+D.variablesql[ordenpreguntas[posx-1]]+"='"+D.resp[posx-1][0]+"' ";
					}
					
					System.out.println("Complemento: "+complemento);
					D.data.OpenConn();
					String opcionesprofe[]=D.ConsultasProfe(0, complemento);
					D.data.CloseConn();
					
					
					if(opcionesprofe.length==1 || posx>=ordenpreguntas.length)
					{ //S�lo tienes 1 posible resultado, mostrarlo 
						D.nombrepersonaje=opcionesprofe[0];
						System.out.println("Encontr� a un profe... Posx: "+posx);
						posx=0;
						game.Cambio(2, g, D);
					}

				}
				
				if(banarial)
				{

					for (int i = posx; i < ordenpreguntas.length; i++) 
					{
						posx=i;
						//System.out.println("posx despu�s del for: "+posx);
						D.data.OpenConn();
						String opciones_consulta[]=D.ConsultasPosicion(ordenpreguntas[i], complemento);
						D.data.CloseConn();
						
						if(opciones_consulta.length>1)
						{ //Podemos preguntarle al usuario
							numpreg=false; unapregunta=false; opcion4=false; opcion8=false;
							//System.out.println("Vector X: "+ordenpreguntas[i]+", D.preguntas tam: "+D.preguntas[ordenpreguntas[i]].length+", D.opciones[posx]: "+D.opciones[ordenpreguntas[i]].length);
							if(D.preguntas[ordenpreguntas[i]].length==D.opciones[ordenpreguntas[i]].length)
							{//El mismo n�mero de opciones es el mismo n�mero de respuestas
								numpreg=true;
								ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
								pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], ordeny[posy], ""));
								contpregunta++;
								break;
							}
							if(D.preguntas[ordenpreguntas[i]].length==1 && D.opciones[ordenpreguntas[i]].length==2)
							{ //Una sola pregunta, se responde con Si y No
								unapregunta=true;
								pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, ""));
								contpregunta++;
								break;
							}
							if(ordenpreguntas[i]==4)
							{ //Color de cabello
								opcion4=true;
								for (int j = 0; j < opciones_consulta.length; j++) {
									if(opciones_consulta[j].equalsIgnoreCase("No tiene"))
									{ //Buscar si hay un "No tiene" 
										preguntacabello=true;
										pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, ""));
										contpregunta++;
										break;
									}
								}
								if(!preguntacabello)
								{//En la consulta todos tienen cabello 
									ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
									pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 1, D.opciones[4][ordeny[posy]]+"?"));
									contpregunta++;
								}
								break;
							}
							if(ordenpreguntas[i]==8)
							{ //Especialidad
								opcion8=true;
								ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
								pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, D.areaespecial[ordeny[posy]]+"?"));
								contpregunta++;
								break;
							}
						}
					}
				}
				
				nopregunta.setText("Pregunta No. "+contpregunta);
			}
		});
		bnoc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				D.cantnoc++;
				if(D.cantnoc==5)
					game.Cambio(4, g, D);
				else
				{
					posx++; posy=0;
					for (int i = posx; i < ordenpreguntas.length; i++) 
					{
						posx=i;
						//System.out.println("posx despu�s del for: "+posx);
						D.data.OpenConn();
						String opciones_consulta[]=D.ConsultasPosicion(ordenpreguntas[i], complemento);
						D.data.CloseConn();
						
						if(opciones_consulta.length>1)
						{ //Podemos preguntarle al usuario
							numpreg=false; unapregunta=false; opcion4=false; opcion8=false;
							//System.out.println("Vector X: "+ordenpreguntas[i]+", D.preguntas tam: "+D.preguntas[ordenpreguntas[i]].length+", D.opciones[posx]: "+D.opciones[ordenpreguntas[i]].length);
							if(D.preguntas[ordenpreguntas[i]].length==D.opciones[ordenpreguntas[i]].length)
							{//El mismo n�mero de opciones es el mismo n�mero de respuestas
								numpreg=true;
								ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
								pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], ordeny[posy], ""));
								contpregunta++;
								break;
							}
							if(D.preguntas[ordenpreguntas[i]].length==1 && D.opciones[ordenpreguntas[i]].length==2)
							{ //Una sola pregunta, se responde con Si y No
								unapregunta=true;
								pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, ""));
								contpregunta++;
								break;
							}
							if(ordenpreguntas[i]==4)
							{ //Color de cabello
								opcion4=true;
								for (int j = 0; j < opciones_consulta.length; j++) {
									if(opciones_consulta[j].equalsIgnoreCase("No tiene"))
									{ //Buscar si hay un "No tiene" 
										preguntacabello=true;
										pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, ""));
										contpregunta++;
										break;
									}
								}
								if(!preguntacabello)
								{//En la consulta todos tienen cabello 
									ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
									pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 1, D.opciones[4][ordeny[posy]]+"?"));
									contpregunta++;
								}
								break;
							}
							if(ordenpreguntas[i]==8)
							{ //Especialidad
								opcion8=true;
								ordeny=D.AleatorioY(opciones_consulta, ordenpreguntas[i]);
								pregunta.setText(D.AjustarPreguntas(ordenpreguntas[i], 0, D.areaespecial[ordeny[posy]]+"?"));
								contpregunta++;
								break;
							}
						}
					}
					
					nopregunta.setText("Pregunta No. "+contpregunta);
					//System.out.println("(No Se) Posx: "+posx+", contnoc: "+D.cantnoc+", opciones_consulta: "+ordenpreguntas.length);
				}
				if(cont>=ordenpreguntas.length)
				{
					System.out.println("Cambio (No se)");
					game.Cambio(2, g, D);
				}
			}
		});
		
	}

}

class Respuesta extends JPanel 
{
	Game g;
	Respuesta resp;
	Preguntas pren;
	
	JButton bsi, bno; 
	JLabel l1, l2;
	URL url; 
	ImageIcon img;
	Datos D;
	
	int fase;
	public Respuesta(Game game, Datos datos)
	{
		fase=0;
		D=datos;
		g=game;
		
		setLayout(null);
		setBackground(Color.green);
		
		String ruta="fotos/base.jpg";
		URL is = getClass().getResource(ruta);
		System.out.println("URL: "+is);
		img=new ImageIcon(is);
		
		
		bsi=new JButton("S�");
		bno=new JButton("No");
		l1=new JLabel("Nombre de Profesor/Docente");
		l2=new JLabel("�Es correcto? ");
		JLabel l3=new JLabel(img);
		
		if(D.nombrepersonaje!=null || D.nombrespecialidad!="")
		{
			l1.setText(D.nombrepersonaje);
		}
		
		bsi.setBounds(150, 500, 150, 150);
		bno.setBounds(320, 500, 150, 150);
		l1.setBounds(150, 400, 550, 50);
		l2.setBounds(150, 450, 350, 50);
		l3.setBounds(150, 20, 300, 360);
		
		l1.setFont(new Font(Font.SANS_SERIF, 20, 25));
		l2.setFont(new Font(Font.MONOSPACED, 10, 30));
		
		add(bsi); add(bno); add(l1); add(l2); add(l3);
		
		bsi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fase==0)
				{
					game.Cambio(4, g, D);
				}
				if(fase==1)
				{
					game.Cambio(1, g, D);
				}
			}
		});
		
		bno.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				l1.setText("");
				if(fase==1)
				{
					game.Cambio(3, g, D);
				}
				else
				{
					fase++;
					l2.setText("�Desea Continuar?");
				}
			}
		});
	}
}

class AgregarPersonaje extends JPanel
{
	/*  Nombre (String), sexo (M y F), ocupacion (Docente, D/C), 
	 *  estatura (B, M, A), tez (Morena, Blanca, Negra), 
	 *  colorcabello (Vector), lentes (S y N), estricto (S y N),
	 *  delgado (S y N), areaespecial (Vector), 
	 *  vestimenta (F, C, Amb), computadora (S y N)
	 * 
	 * 
	 */
	JLabel preg[]=new JLabel[12]; 
	JTextField nombre, colorcabello, areaespecial; 
	JButton aceptar;
	JRadioButton masculino, femenino, docente, docentemas, baja, media, alta, morena, blanca, negra, lentesi, lentesno, estrictosi, estrictono,
					delgadosi, delgadono, formal, casual, ambas, compusi, compuno;
	ButtonGroup sexo, ocupacion, estatura, tez, lentes, estricto, delgado, vestimenta, computadora;
	public AgregarPersonaje(Game game, Datos dat)
	{
		setBackground(Color.gray);
		
		setLayout(null);
		
		for (int i = 0; i < preg.length; i++)
		{
			preg[i]=new JLabel("");
		}
				
		sexo=new ButtonGroup();
		ocupacion=new ButtonGroup();
		estatura=new ButtonGroup();
		tez=new ButtonGroup();
		lentes=new ButtonGroup();
		estricto=new ButtonGroup();
		delgado=new ButtonGroup();
		vestimenta=new ButtonGroup();
		computadora=new ButtonGroup();
		
		preg[0].setText("Nombre: "); preg[0].setBounds(10, 10, 120, 30); add(preg[0]);
		nombre=new JTextField(); nombre.setBounds(10, 50, 240, 30); add(nombre);
		preg[1].setText("Sexo: "); preg[1].setBounds(10, 110, 120, 30); add(preg[1]);
		masculino=new JRadioButton("M"); masculino.setBounds(10, 140, 40, 30); add(masculino); 
		femenino=new JRadioButton("F"); femenino.setBounds(120, 140, 40, 30); add(femenino);
		preg[2].setText("Ocupaci�n: "); preg[2].setBounds(10, 200, 120, 30); add(preg[2]);
		docente=new JRadioButton("Docente"); docente.setBounds(10, 240, 120, 30); add(docente);
		docentemas=new JRadioButton("Docente y m�s"); docentemas.setBounds(140, 240, 120, 30); add(docentemas);
		preg[3].setText("Estatura: "); preg[3].setBounds(10, 280, 60, 30); add(preg[3]);
		baja=new JRadioButton("Baja"); baja.setBounds(10, 310, 90, 30); add(baja);
		media=new JRadioButton("Media"); media.setBounds(110, 310, 90, 30); add(media);
		alta=new JRadioButton("Alta"); alta.setBounds(210, 310, 90, 30); add(alta);
		preg[4].setText("Tez: "); preg[4].setBounds(10, 360, 120, 30); add(preg[4]);
		morena=new JRadioButton("Morena"); morena.setBounds(10, 400, 90, 30); add(morena);
		blanca=new JRadioButton("Blanca"); blanca.setBounds(110, 400, 90, 30); add(blanca);
		negra=new JRadioButton("Negra"); negra.setBounds(210, 400, 90, 30); add(negra);
		
		preg[5].setText("Color de cabello: "); preg[5].setBounds(350, 10, 120, 30); add(preg[5]);
		colorcabello=new JTextField(); colorcabello.setBounds(350, 40, 190, 30); add(colorcabello);
		preg[6].setText("�Usa Lentes? "); preg[6].setBounds(350, 90, 120, 30); add(preg[6]);
		lentesi=new JRadioButton("S�"); lentesi.setBounds(350, 120, 40, 30); add(lentesi);
		lentesno=new JRadioButton("No"); lentesno.setBounds(400, 120, 40, 30); add(lentesno);
		preg[7].setText("�Es estricto?"); preg[7].setBounds(350, 160, 120, 30); add(preg[7]);
		estrictosi=new JRadioButton("S�"); estrictosi.setBounds(350, 200, 40, 30); add(estrictosi);
		estrictono=new JRadioButton("No"); estrictono.setBounds(400, 200, 40, 30); add(estrictono);
		preg[8].setText("�Es delgado?"); preg[8].setBounds(350, 250, 120, 30); add(preg[8]);
		delgadosi=new JRadioButton("S�"); delgadosi.setBounds(350, 280, 40, 30); add(delgadosi);
		delgadono=new JRadioButton("No"); delgadono.setBounds(400, 280, 40, 30); add(delgadono);
		preg[9].setText("Especialidad: "); preg[9].setBounds(350, 330, 120, 30); add(preg[9]);
		areaespecial=new JTextField(); areaespecial.setBounds(350, 360, 120, 30); add(areaespecial);
		preg[10].setText("Tipo de Vestimenta: "); preg[10].setBounds(350, 410, 120, 30); add(preg[10]);
		formal=new JRadioButton("Formal"); formal.setBounds(350, 440, 120, 30); add(formal);
		casual=new JRadioButton("Casual"); casual.setBounds(480, 440, 120, 30); add(casual);
		ambas=new JRadioButton("Ambas"); ambas.setBounds(610, 440, 120, 30); add(ambas);
		preg[11].setText("�Usa computadora en clase?"); preg[11].setBounds(350, 480, 180, 30); add(preg[11]);
		compusi=new JRadioButton("S�"); compusi.setBounds(350, 510, 40, 30); add(compusi);
		compuno=new JRadioButton("No"); compuno.setBounds(400, 510, 40, 30); add(compuno);
		aceptar=new JButton("Aceptar"); aceptar.setBounds(250, 600, 160, 90); add(aceptar);
		
		sexo.add(masculino); sexo.add(femenino);
		ocupacion.add(docente); ocupacion.add(docentemas);
		estatura.add(baja); estatura.add(media); estatura.add(alta);
		tez.add(morena); tez.add(blanca); tez.add(negra);
		lentes.add(lentesi); lentes.add(lentesno);
		estricto.add(estrictosi); estricto.add(estrictono);
		delgado.add(delgadosi); delgado.add(delgadono);
		vestimenta.add(casual); vestimenta.add(formal); vestimenta.add(ambas);
		computadora.add(compusi); computadora.add(compuno);
		
		
		
		aceptar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String sexo="", ocupacion="", estatura="", tez="", lentes="", estricto="", delgado="", vestimenta="", computadora="";
				try
				{
					if(masculino.isSelected())
					{
						sexo="M";
					}
					else
					{
						sexo="F";
					}
					if(docente.isSelected())
					{
						ocupacion="Docente";
					}
					if(docentemas.isSelected())
					{
						ocupacion="D/C";
					}
					if(baja.isSelected())
					{
						estatura="Baja";
					}
					if(media.isSelected())
					{
						estatura="Media";
					}
					if(alta.isSelected())
					{
						estatura="Alta";
					}
					if(morena.isSelected())
					{
						tez="Morena";
					}
					if(blanca.isSelected())
					{
						tez="Blanca";
					}
					if(negra.isSelected())
					{
						tez="Negra";
					}
					if(lentesi.isSelected())
					{
						lentes="Si";
					}
					if(lentesno.isSelected())
					{
						lentes="No";
					}
					if(estrictosi.isSelected())
					{
						estricto="Si";
					}
					if(estrictono.isSelected())
					{
						estricto="No";
					}
					if(delgadosi.isSelected())
					{
						delgado="Si";
					}
					if(delgadono.isSelected())
					{
						delgado="No";
					}
					if(formal.isSelected())
					{
						vestimenta="Formal";
					}
					if(casual.isSelected())
					{
						vestimenta="Casual";
					}
					if(ambas.isSelected())
					{
						vestimenta="Ambas";
					}
					if(compusi.isSelected())
					{
						computadora="Si";
					}
					if(compuno.isSelected())
					{
						computadora="No";
					}
					
					String cad="insert into Profesores (nombre, sexo, ocupacion, estatura, tez, colorcabello, lentes, estricto, delgado, areaespecial, "
							+ " vestimenta, computadora) values ('"+nombre.getText().toString()+"', '"+sexo+"', '"+ocupacion+"', '"+estatura+"', '"+tez+"', '"
							+colorcabello.getText().toString()+"', '"+lentes+"', '"+estricto+"', '"+delgado+"', '"+areaespecial.getText().toString()+"', '"+vestimenta+"', '"
							+computadora+"')";
					System.out.println(cad);
					dat.data.OpenConn();
					dat.data.setQuery(cad);
					dat.data.CloseConn();
					game.Cambio(4, game, dat);
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, "Pos no se insert� :'v ");
				}
				
			}
		});
	}
}

class Finalizar extends JPanel
{
	
	ImageIcon img;
	JLabel l1, l2;
	JButton inicio;
	public Finalizar(String mensaje, int pos, Game game)
	{
		setBackground(Color.green);
		System.out.println("Pos: "+pos);
		setLayout(null);
		
		URL is = getClass().getResource("encontrado.jpg");;
		switch(pos)
		{
			case 0:
				 is = getClass().getResource("fotos/encontrado.jpg");
			break;
			case 1:
				 is = getClass().getResource("fotos/noencontrado.jpg");
			break;
			case 2:
				 is = getClass().getResource("fotos/nose.jpg");
				 break;
		}
		System.out.println("URL: "+is);
		img=new ImageIcon(is);
		l1=new JLabel(mensaje);
		l2=new JLabel(img);
		inicio=new JButton("�Quieres jugar otra vez?");
		
		l2.setBounds(60, 30, 450, 450);
		l1.setBounds(60, 500, 400, 155);
		inicio.setBounds(60, 700, 450, 150);
		
		add(l1); add(l2); add(inicio); 
		
		inicio.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Datos dat=new Datos();
				game.Cambio(0, game, dat);
			}
		});
	}
}

class Datos
{	//Inicio de pregunta: �Tu personaje...?
	Preguntas preg;
	SQLClass data;
	String preguntas[][]={{"�Tu personaje es de g�nero masculino?", "�Tu personaje es de g�nero femenino?"}, //0.- G�nero
	{"�Tu personaje tiene alguna otra ocupaci�n adem�s de la docencia?", "�Tu personaje s�lamente es docente?"}, //1.- Docencia
	{"�Tu personaje es de estatura alta?", "�Tu personaje es de estatura baja?", "�Tu personaje es de estatura media?"}, //2.- Estatura 
	{"�Tu personaje es de tez morena?", "�Tu personaje es de tez blanca?","�Tu personaje es de tez negra?"}, //3.- Tez
	{"�Tu personaje tiene cabello?","�El color del cabello de tu personaje es:  "}, //4.- Cabello
	{"�Tu personaje usa lentes?"}, //5.- Lentes
	{"�Tu personaje es estricto?"}, //6.- Estricto
	{"�Tu personaje es delgado?"}, //7.- Delgado
	{"�Tu personaje se especializa en la �rea de: "}, //8.- Area de la carrera
	{"�Tu personaje usa vestimenta formal?", "�Tu personaje usa vestimenta casual?", "�Tu personaje usa vestimenta usa vestimenta casual y formal?"}, //9.- Vestimenta
	{"�Tu personaje usa computadora en clases?"}}; //10.- Usa Computadora
	
	String areaespecial[]={"Bases de Datos", "Programaci�n", "Redes", "Electr�nica", "Investigaci�n de Proyectos", "Desarrollo Web"};
	String colorcabello[]= {"No tiene", "Negro","Blanco", "Caf�", "Rubio", "Rojo", "Otro"};
	
	String opciones[][]={{"M", "F"}, //G�nero
			{"D/C", "Docente"}, //Alg�n cargo extra
			{"Alta", "Baja", "Media"}, //Estatura
			{"Morena", "Blanca", "Negra"}, //Color de piel
			{"Negro","Blanco", "Caf�", "Rubio", "Rojo", "Otro"}, //Color de cabello 
			{"Si", "No"}, //Usa lentes
			{"Si", "No"}, //Es estricto
			{"Si", "No"}, //Es Delgado
			{"Bases de Datos", "Programaci�n", "Redes", "Electr�nica", "Investigaci�n de Proyectos", "Desarrollo Web"}, //Area de la carrera
			{"Formal", "Casual", "Ambas"}, //Vestimenta
			{"Si", "No"}, //Usa computadora
			{"Sistemas", "Bioqu�mica", "Arquitectura", "Industrial", "Inform�tica", "Contabilidad", "Gesti�n Empresarial", "Administraci�n"}
	};
	
	String variables[]= {"sexo", "ocupacion", "estatura", "tez", "colorcabello", "lentes", "estricto", "delgado", "areaespecial", "vestimenta", "computadora"};
	String variablesql[]={"sexo", "ocupacion", "estatura", "tez", "colorcabello",
			"lentes", "estricto", "delgado", "areaespecial", "vestimenta", "computadora", "caresp1", "caresp2", "caresp3"};
	String resp[][]=new String[12][2];
	int esp[]= new int [3], nopregunta=1, cont=0; //Respuestas Normales y Especiales
	int saltos[]={0, 1, 2, 5, 9, 10, 11, 12, 13, 14, 15, 18}, cantnoc=0, countelements;
	int ventanactual=0;
	boolean fin=false;
	
	String nombrepersonaje="", nombrespecialidad="", urlfoto="";
	
	public void Conexi�n()
	{
		data=new SQLClass();
	}
	
	public String[] ConsultasPosicion(int pos, String complement) 
	{	
		int cont=0;
		String elemento="", cad="select distinct "+variablesql[pos]+" from Profesores "+complement;
		ResultSet resultado=data.getQuery(cad);
		String vecky[]=new String[cont];
		try
		{
			while(resultado.next())
			{
				elemento=resultado.getString(variablesql[pos]);
				cont++;
				String newvecky[]=new String[cont];
				for (int i = 0; i < vecky.length; i++) {
					newvecky[i]=vecky[i];
				}
				newvecky[vecky.length]=elemento;
				vecky=newvecky;
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error en la consulta... ");
		}
		return vecky;
	}
	
	public String[] ConsultasProfe(int pos, String complement) 
	{
		
		int cont=0;
		String elemento="", cad="";
		if(pos==0)
		{
			cad="select distinct nombre from Profesores "+complement;
		}
		if(pos==1)
		{
			cad="select distinct areaespecial from Profesores "+complement;
		}
		ResultSet resultado=data.getQuery(cad);
		String vecky[]=new String[cont];
		try
		{
			while(resultado.next())
			{
				if(pos==0)
					elemento=resultado.getString("nombre");
				if(pos==1)
					elemento=resultado.getString("areaespecial");
				cont++;
				String newvecky[]=new String[cont];
				for (int i = 0; i < vecky.length; i++) {
					newvecky[i]=vecky[i];
				}
				newvecky[vecky.length]=elemento;
				vecky=newvecky;
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error en la consulta... ");
		}
		return vecky;
	}
	
	public String UrlFoto(String complement)
	{
		int cont=0;
		String elemento="", cad="", url="";
			cad="select distinct id from Profesores "+complement;
		ResultSet resultado=data.getQuery(cad);
		try
		{
			while(resultado.next())
			{
				elemento=resultado.getString("id");
				url="fotos/"+elemento+".jpg";
			}
		}
		catch(SQLException e)
		{
			System.out.println("Error en la consulta... ");
		}
		return url;
	}
	
	public int[] AleatorioY(String vecky[], int var)
	{
		System.out.print("Vecky: ");
		for (int i = 0; i < vecky.length; i++) {
			System.out.print(vecky[i]+", ");
		}
		System.out.println();
		int elementos=vecky.length;
		
		//Corregir el m�todo, rumores con el vector respaldo
		int posy[]=new int[elementos];
		String respuestas[]=opciones[var];
		

		if(elementos>1)
		{//Si hay m�s de un elemento, sacar las posiciones de los elementos y guardarlas en un vector
			int v=0;
			for (int i = 0; i < elementos; i++, v++) 
			{
				for (int j = 0; j < respuestas.length; j++)
				{
					if(vecky[i].equals(respuestas[j]))
					{
						posy[v]=j;
						break;
					}
					if(var==4 && vecky[i].equalsIgnoreCase("No tiene"))
					{
						v--;
						break;
					}
				}
			}
			elementos=v;
			int[] respaldo=new int [elementos];
			java.util.Random r=new java.util.Random();
			
			for (int i = 0; i < respaldo.length; i++) {
				respaldo[i]=-1;
			}
			for (int i = 0; i < elementos; i++) 
			{
				int x=posy[r.nextInt(elementos)];
				boolean entra=true;
				for (int j = 0; j < respaldo.length; j++) {
					if(respaldo[j]==x)
					{
						i--;
						entra=false;
						break;
					}
				}
				if(entra)
					respaldo[i]=x;
			}
			posy=respaldo;
		}
		return posy;
	}
	
	public int[] AleatorioX(String[][] vecky)
	{
		System.out.print("Vector X: ");
		int tama�opreguntas=0;
		for (int i = 0; i < vecky.length; i++) {
			for (int j = 0; j < vecky[i].length; j++) {
				//System.out.print(vecky[i][j]);
			}
			//System.out.println();
			tama�opreguntas++;
		}
		java.util.Random r=new java.util.Random();
		int pos[]=new int [tama�opreguntas];
		
		for (int i = 0; i < pos.length; i++) {
			pos[i]=-1;
		}
		for (int i = 0; i < tama�opreguntas; i++) {
			boolean insertarelemento=true;
			int x=r.nextInt(tama�opreguntas);
			
			for (int j = 0; j < pos.length; j++)
			{
				if(pos[j]==x)
				{
					i--;
					insertarelemento=false;
					break;
				}
			}
			
			if(insertarelemento)
			{
				pos[i]=x;
				System.out.print(pos[i]+" ");
			}
				
		}
		System.out.println();
		return pos;
	}
	
	public String AjustarPreguntas(int x, int y, String agregar)
	{		
		try
		{
			int cont=0, posespacio=0, j=0;
			String cad="", nuevacad="", nuevoelemento=preguntas[x][y]+agregar;
			for (int i = 0; i < nuevoelemento.length(); i++) 
			{
				if(cont<=20)
				{
					cad=nuevoelemento.substring(i, i+1);
					if(cad.equals(" "))
					{
						posespacio=i;
					}
					cont++;
				}
				else
				{
					i=posespacio;
					nuevacad+=nuevoelemento.substring(j, i+1)+"\n";
					cont=0;
					j=i;
				}
			}
			nuevacad+=nuevoelemento.substring(j, nuevoelemento.length())+"\n";
			return nuevacad;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return "";	
		}
	}
	
}

class SQLClass 
{

	private String _usuario="vil";
	private String _pwd= "secreta";
	private static String _bd="profenator";
	//static String _url = "jdbc:mysql://localhost/"+_bd;
	static String _url="jdbc:mysql://localhost/"+_bd+"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	public Connection conn = null;
	public boolean entrar=false;
	public SQLClass() {
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
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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
}