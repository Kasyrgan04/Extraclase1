
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;
import java.io.*;

// Interfaz grafica gracias a @PildorasInformaticas en youtube

public class Servidor  {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//mimarco es una instancia de marcoservidor
		MarcoServidor mimarco=new MarcoServidor();
		//Exit on close mata el proceso al cerrar la ventana
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
	}	
}
//Esta clase crea la ventana del servidor
//Runnable crea el hilo necesario para siempre escuchar el socket
class MarcoServidor extends JFrame implements Runnable{	
	
	public MarcoServidor(){
		
		setBounds(1200,300,280,350);				
			
		JPanel milamina= new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		milamina.add(areatexto,BorderLayout.CENTER);
		
		add(milamina);
		//Hace que siempre se muestre la ventana
		setVisible(true);
		//añade el hilo a la ventana
		Thread hilo=new Thread(this);
		//Inicializa el hilo
		hilo.start();
		
		}
	
	private	JTextArea areatexto;

	@Override
	/*Este metodo mantiene abierto el puerto y lo escucha
	  en segundo plano usando hilos
	 */
	public void run() {
		// TODO Auto-generated method stub
		
		int maxConnections=10; //numero maximo de conexiones aceptadas
		Socket[] sockets= new Socket[maxConnections];
		
		try {
			//Abre el puerto
			ServerSocket servidor=new ServerSocket(9999);
			
			String nick,ip,texto;
			int puerto;
			
			Envio mensaje_recibido;
			
			ArrayList<Integer> puertos=new ArrayList<Integer>();
			puertos.add(9090);
			
			//Ciclo infinito para mantner siempre el socket abierto
			while(true) {
				
				Socket misocket=servidor.accept();
				//Recibe el flujo de datos
				ObjectInputStream flujo_entrada=new ObjectInputStream(misocket.getInputStream());
				//Mete los datos recibos dentro del flujo
				mensaje_recibido=(Envio) flujo_entrada.readObject();
				//Se obtienen los datos contenidos en el flujo
				nick=mensaje_recibido.getNick();
				ip=mensaje_recibido.getIp();
				texto=mensaje_recibido.getTexto();
				puerto=Integer.parseInt(mensaje_recibido.getPuerto());
				
				if(puerto!= puertos.get(puertos.size()-1)) {
					puertos.add(puerto);
				}
				//Añade el texto a la ventana
					
				areatexto.append("\n" +nick+": "+texto+" (para "+ip+")");
					
				//Socket para enviar los datos al destinatario
				for(int i=0; i<puertos.size();i++) {
					Socket destinatario=new Socket(ip,puertos.get(i));
					//Para enviar el paquete
					ObjectOutputStream mensaje_saliente=new ObjectOutputStream(destinatario.getOutputStream());
					//Mete los datos recibidos en el paquete saliente
					mensaje_saliente.writeObject(mensaje_recibido);
					mensaje_saliente.close();
					//Cierra el socket que comunica el puerto de salida
					destinatario.close();
				}
				//Cierra la conexion
				misocket.close();
			
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

