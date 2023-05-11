package dev.groupb.m306groupb.utils;

import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.FileType;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
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

public class SDATFileReader implements FileReader<SDATFile> {
    @Override
    public FileDate getFileDate(File file) {
        // Format looks like this: 2019-03-11T23:00:00Z
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            FileDate fileDate = new FileDate();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList interval = doc.getElementsByTagName("rsm:Interval");
            for (int i = 0; i < interval.getLength(); i++) {
                Node item = interval.item(i);
                item.normalize();

                NodeList children = item.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    child.normalize();

                    if (child.getNodeName().equals("rsm:StartDateTime")) {
                        fileDate.setStartDate(dateFormat.parse(child.getTextContent()));
                    } else if (child.getNodeName().equals("rsm:EndDateTime")) {
                        fileDate.setEndDate(dateFormat.parse(child.getTextContent()));
                    }
                }
            }

            NodeList instanceDocument = doc.getElementsByTagName("rsm:InstanceDocument");
            for (int i = 0; i < instanceDocument.getLength(); i++) {
                Node item = instanceDocument.item(i);
                item.normalize();

                NodeList children = item.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    child.normalize();

                    if (child.getNodeName().equals("rsm:Creation")) {
                        fileDate.setFileCreationDate(dateFormat.parse(child.getTextContent()));
                    }
                }
            }

            return fileDate;
        } catch (ParserConfigurationException | IOException | SAXException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SDATFile parseFile(File file) {
        return SDATFile.builder()
                .fileName(file.getName())
                .filePath(file.getAbsolutePath())
                .fileType(findFileType(file))
                .build();
    }

    public FileType findFileType(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList consumption = doc.getElementsByTagName("rsm:ConsumptionMeteringPoint");
            NodeList production = doc.getElementsByTagName("rsm:ProductionMeteringPoint");

            if (consumption.getLength() > 0)
                return FileType.Consumption;
            else if (production.getLength() > 0)
                return FileType.Production;

            throw new RuntimeException("File type not found");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
