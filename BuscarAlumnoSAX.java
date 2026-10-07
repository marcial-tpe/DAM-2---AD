import java.nio.file.Path;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BuscarAlumnoSAX {

    public static void main(String[] args) {

        Path ficheroXML =
                Path.of("Datos", "entrada", "fichero.xml");
        String nombreBuscado = "Laura";
        try {

            SAXParserFactory factory =
                    SAXParserFactory.newInstance();
            SAXParser parser =
                   factory.newSAXParser();
            Buscador handler =
                    new Buscador(nombreBuscado);
            parser.parse(ficheroXML.toFile(), handler);
        } catch (SAXException e) {
            if (e.getMessage().equals("ENCONTRADO")) {
                System.out.println("Búsqueda terminada.");
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Buscador extends DefaultHandler {

        private String nombreBuscado;
        private boolean leyendoNombre = false;

        public Buscador(String nombreBuscado) {
            this.nombreBuscado = nombreBuscado;
        }

        @Override
        public void startElement(
                String uri,
                String localName,
                String qName,
                Attributes attributes)
                throws SAXException {

            if (qName.equals("nombre")) {
                leyendoNombre = true;
            }
        }

        @Override
        public void characters(
                char[] ch,
                int start,
                int length)
                throws SAXException {

            if (leyendoNombre) {

                String nombre =
                        new String(ch, start, length).trim();

                System.out.println("Leyendo: " + nombre);

                if (nombre.equalsIgnoreCase(nombreBuscado)) {

                    System.out.println(
                            "Encontrado: " + nombre
                    );

                    // DETENEMOS SAX
                    throw new SAXException("ENCONTRADO");
                }
            }
        }

        @Override
        public void endElement(
                String uri,
                String localName,
                String qName)
                throws SAXException {

            if (qName.equals("nombre")) {
                leyendoNombre = false;
            }
        }
    }
}
