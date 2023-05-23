package dev.groupb.m306groupb.utils;

import dev.groupb.m306groupb.model.ESLFile.ESLFile;
import dev.groupb.m306groupb.model.FileDate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ESLFileReader implements FileReader<ESLFile> {

    @Override
    public FileDate getFileDate(File file) {
        return null;
    }

    @Override
    public ESLFile parseFile(File file) {
        double[] obisValues = findValues(file);
        return ESLFile.builder()
                .highTariffConsumption(obisValues[0])
                .lowTariffConsumption(obisValues[1])
                .highTariffProduction(obisValues[2])
                .lowTariffProduction(obisValues[3])
                .build();
    }

    /*
     *   1-1:1.8.1 --> Index 0 (Consumption Hightariff)
     *   1-1:1.8.2 --> Index 1 (Consumption Lowtariff)
     *   1-1:2.8.1 --> Index 2 (Production Hightariff)
     *   1-1:2.8.2 --> Index 3 (Production Lowtariff)
     * */
    private double[] findValues(File file) {
        double[] obisValues = new double[4];
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();


            NodeList valuerow = doc.getElementsByTagName("ValueRow");
            for (int i = 0; i < valuerow.getLength(); i++) {
                Node item = valuerow.item(i);
                String obis = item.getAttributes().getNamedItem("obis").getNodeValue();

                switch (obis) {
                    case "1-1:1.8.1" -> {
                        String consumptionHighttariff = item.getAttributes().getNamedItem("value").getNodeValue();
                        obisValues[0] = Double.parseDouble(consumptionHighttariff);
                    }
                    case "1-1:1.8.2" -> {
                        String consumptionLowtariff = item.getAttributes().getNamedItem("value").getNodeValue();
                        obisValues[1] = Double.parseDouble(consumptionLowtariff);
                    }
                    case "1-1:2.8.1" -> {
                        String productionHightariff = item.getAttributes().getNamedItem("value").getNodeValue();
                        obisValues[2] = Double.parseDouble(productionHightariff);
                    }
                    case "1-1:2.8.2" -> {
                        String productionLowtariff = item.getAttributes().getNamedItem("value").getNodeValue();
                        obisValues[3] = Double.parseDouble(productionLowtariff);
                    }
                }
            }
            System.out.println(valuerow);

            return obisValues;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
