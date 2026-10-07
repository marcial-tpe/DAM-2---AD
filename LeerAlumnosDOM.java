import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.nio.file.Path;
import java.nio.file.Files;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LeerAlumnosDOM {
    public static void main(String[] args) {

        Path ficheroXML = Path.of("Datos","entrada","fichero.xml"); System.out.println(ficheroXML);
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(ficheroXML.toFile());
            NodeList alumnos = documento.getElementsByTagName("alumno");
            for (int i=0;i< alumnos.getLength();i++) {

                Element alumno = (Element) alumnos.item(i);
                System.out.println("Alumno: "+i);
                String nombre = alumno.getElementsByTagName("nombre")
                        .item(0).getTextContent();
                System.out.println("Nombre: "+nombre);
                String edad = alumno.getElementsByTagName("edad")
                        .item(0).getTextContent(); System.out.println("Edad: "+edad);

            }
        } catch (Exception exception) {

            throw new RuntimeException(exception);

        }
    }
}
