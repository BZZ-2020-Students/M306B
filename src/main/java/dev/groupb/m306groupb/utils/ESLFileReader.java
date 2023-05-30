package dev.groupb.m306groupb.utils;

import dev.groupb.m306groupb.model.ESLFile.ESLFile;
import dev.groupb.m306groupb.model.FileDate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.util.Arrays;
import java.util.Objects;

public class ESLFileReader implements FileReader<ESLFile> {
    @Override
    public FileDate getFileDate(File file) {
        throw new UnsupportedOperationException();
    }

    public FileDate getFileDate(File file, int index) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalStuff.ESL_DATE_FORMAT);
        try {
            FileDate fileDate = new FileDate();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList headerList = doc.getElementsByTagName("Header");
            Node creationDate = headerList.item(0);
            creationDate.normalize();
            String created = creationDate.getAttributes().getNamedItem("created").getNodeValue();
            fileDate.setFileCreationDate(dateFormat.parse(created));

            NodeList timePeriodList = doc.getElementsByTagName("TimePeriod");
            Node timePeriod = timePeriodList.item(index);
            String end = timePeriod.getAttributes().getNamedItem("end").getNodeValue();
            fileDate.setEndDate(dateFormat.parse(end));

            return fileDate;
        } catch (ParserConfigurationException | IOException | SAXException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int amountOfEslFiles(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList timePeriodList = doc.getElementsByTagName("TimePeriod");
            return timePeriodList.getLength();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ESLFile parseFile(File file) {
        throw new UnsupportedOperationException();
    }

    public ESLFile parseFile(File file, int index) {
        Double[] obisValues = findValues(file, index);

        // if all values null, return null
        if (Arrays.stream(obisValues).allMatch(Objects::isNull)) {
            return null;
        }

        return ESLFile.builder()
                .fileName(file.getName())
                .filePath(file.getAbsolutePath())
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
    private Double[] findValues(File file, int index) {
        Double[] obisValues = new Double[4];
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            Element timePeriod = (Element) doc.getElementsByTagName("TimePeriod").item(index);
            NodeList valueRows = timePeriod.getElementsByTagName("ValueRow");
            for (int j = 0; j < valueRows.getLength(); j++) {
                Element valueRow = (Element) valueRows.item(j);
                String value = valueRow.getAttribute("value");
                String obis = valueRow.getAttribute("obis");
                switch (obis) {
                    case "1-1:1.8.1" -> {
                        obisValues[0] = Double.parseDouble(value);
                    }
                    case "1-1:1.8.2" -> {
                        obisValues[1] = Double.parseDouble(value);
                    }
                    case "1-1:2.8.1" -> {
                        obisValues[2] = Double.parseDouble(value);
                    }
                    case "1-1:2.8.2" -> {
                        obisValues[3] = Double.parseDouble(value);
                    }
                }
            }

            return obisValues;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
