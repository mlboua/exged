package writer.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLUtils {
    public static String toPrettyString(final String xml, final int indent) {
        try {
            final String vxml = xml.replace("&", " ");
            // Turn xml string into a document
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(vxml.getBytes("utf-8"))));

            // Remove whitespaces outside tags
            document.normalize();
            document.setXmlStandalone(true);
            final XPath xPath = XPathFactory.newInstance().newXPath();
            final NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Setup pretty print options
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

            // Return pretty print xml string
            final StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toMinifyString(final String xml) {
        try {
            final String vxml = xml.replace("&", " ");
            // Turn xml string into a document
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(vxml.getBytes("utf-8"))));

            // Remove whitespaces outside tags
            document.normalize();
            final XPath xPath = XPathFactory.newInstance().newXPath();
            final NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Setup pretty print options
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 0);
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "false");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            // Return pretty print xml string
            final StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validateAgainstXSD(final String xml, final String xsdfile) {
        try {
            final InputStream xsd = new FileInputStream(new File(xsdfile));
            final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final Schema schema = factory.newSchema(new StreamSource(xsd));
            final Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(xml.getBytes())));
            return true;
        } catch (final Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
