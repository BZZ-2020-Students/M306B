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
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ESLFileReader implements FileReader<ESLFile> {

    @Override
    public FileDate getFileDate(File file) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalStuff.ESL_DATE_FORMAT);

        try {
            FileDate fileDate = new FileDate();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList headerList = doc.getElementsByTagName("Header");
            for (int i = 0; i < headerList.getLength(); i++) {
                Node creationDate = headerList.item(i);
                creationDate.normalize();
                String created = creationDate.getAttributes().getNamedItem("created").getNodeValue();
                //System.out.println(created);
                fileDate.setFileCreationDate(dateFormat.parse(created));
            }

            NodeList timePeriodList = doc.getElementsByTagName("TimePeriod");
            for (int j = 0; j < timePeriodList.getLength(); j++) {
                Node timePeriod = timePeriodList.item(j);
                String end = timePeriod.getAttributes().getNamedItem("end").getNodeValue();
                System.out.println(end);
                fileDate.setEndDate(dateFormat.parse(end));
                break;
            }

            return fileDate;
        } catch (ParserConfigurationException | IOException | SAXException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ESLFile parseFile(File file) {
        getFileDate(file);
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

            return obisValues;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
