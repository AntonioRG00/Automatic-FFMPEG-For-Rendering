import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
//Author Antonio Rodríguez Gundermann 25/10/2020
//Clase principal que crea un proceso con el jar de la clase InvocarProceso para cada .mp4 encontrado recursivamente.
public class ConvertidorFFMPEG {
	public static void main(String[]args) {
		
		//Creamos un directorio para guardar los renderizados
		new File("renderizados").mkdir();
		
		//Clase para la búsqueda recursiva
		BusquedaRecursiva br = new BusquedaRecursiva();
		
		try {
			//Buscamos recursivamente desde donde se ejecuto el .jar
			br.busquedaRecursiva(System.getProperty("user.dir"), ".mp4");
			ArrayList<File> videos = br.getArrayResultado(); //Array con todos los .mp4
			
			//Antes de ejecutar el programa hay archivos que pueden contener espacios, con lo cual no nos interesa
			//Ej: '2020-06-01 playa.mp4' ese espacio lo tomára como que 2020-06-01 es el archivo y playa.mp4 es un parámetro
			Comparator<File> comparadorModificacion = Comparator.comparing(File::lastModified);
			videos.sort(comparadorModificacion.reversed());
			
			//Le cambiamos el nombre a uno entendible por el programa sin mover el archivo de directorio
			//El nombre será vid seguido de un número que se le asigna según su fecha de modificación
			String pathAbsolutoPadre;
			for(int i=0 ; i<videos.size() ; i++) {
				pathAbsolutoPadre = new File(videos.get(i).getParent()).getAbsolutePath();
				videos.get(i).renameTo(new File(pathAbsolutoPadre + "\\" + "vid"+i+".mp4"));
			}
			
			//Al haber cambiado el nombre de los ficheros hay que refrescar el array con los videos
			br = new BusquedaRecursiva();
			br.busquedaRecursiva(System.getProperty("user.dir"), ".mp4");
			videos = br.getArrayResultado();
			
			//Ejecutamos un proceso de renderizado por cada .mp4
			Path folder = Paths.get(new File("renderizados").getAbsolutePath());
			long oldSize, newSize;
			for(int i=0 ; i<videos.size() ; i++) {
				ProcessBuilder pb = new ProcessBuilder("java","-cp","ConvertidorFFMPEG.jar","InvocarProceso", 
						videos.get(i).getAbsolutePath(), //Video a procesar
						new File(videos.get(i).getParent()).getAbsolutePath() + "\\renderizados" + "\\" + videos.get(i).getName());  //Nombre del video ya procesado

				Process proc = pb.start();
				proc.waitFor();
				//Vamos comprobando cada cierto tiempo que haya acabado el renderizado del proceso
				do {
					oldSize = Files.walk(folder)
							  .filter(p -> p.toFile().isFile())
							  .mapToLong(p -> p.toFile().length())
							  .sum();
					
					TimeUnit.SECONDS.sleep(7);
					
					newSize = Files.walk(folder)
							  .filter(p -> p.toFile().isFile())
							  .mapToLong(p -> p.toFile().length())
							  .sum();
				}while(oldSize < newSize);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

class BusquedaRecursiva{
	private ArrayList<File> arrayResultado;
	
	public BusquedaRecursiva() {
		arrayResultado = new ArrayList<File>();
	}
	
	//Busca recursivamente desde el string directorio pasado por parámetro en adelante y devolverá todos los
	//archivos que contengan el keyword pasado por parámetro
	public void busquedaRecursiva(String directorio, String keyword) throws IOException{
		File f = new File(directorio);
		
		if(!f.isDirectory()) throw new IOException(directorio + " no es un directorio");
		
		File[] contenido = f.listFiles();
		for(int i=0 ; i<contenido.length ; i++) {
			if(contenido[i].isDirectory()) busquedaRecursiva(contenido[i].getAbsolutePath(), keyword);
			else if(contenido[i].getName().contains(keyword)) arrayResultado.add(contenido[i]);
		}
	}

	public ArrayList<File> getArrayResultado() {
		return arrayResultado;
	}
}