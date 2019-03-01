package org.hit.fintech2018.katz;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XMLParserISO8583 {

    Map<Integer, ISO8583Field> buildFieldsArray()
            throws IOException, SAXException, ParserConfigurationException {
        HashMap<Integer, ISO8583Field> result = new HashMap<>();
        try {
            File file = new File("src/org/hit/fintech2018/katz/ISO8583fields.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("bitfield");

            for (int i = 0; i < nodeList.getLength(); i++) {

                int fieldId = Integer.parseInt(nodeList.item(i).getAttributes()
                        .getNamedItem("id").getNodeValue());

                int fieldLength = Integer.parseInt(nodeList.item(i).getAttributes()
                        .getNamedItem("length").getNodeValue());

                String fieldName = nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue();

                String fieldClass = nodeList.item(i).getAttributes().getNamedItem("class").getNodeValue();
                boolean fieldIsFixed = Boolean.parseBoolean(nodeList.item(i).getAttributes().
                        getNamedItem("fixed").getNodeValue());

                ISO8583Field iso8583Field = new ISO8583Field(
                        fieldId,
                        fieldLength,
                        fieldName,
                        fieldClass,
                        fieldIsFixed);
                result.put(fieldId, iso8583Field);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }
}
