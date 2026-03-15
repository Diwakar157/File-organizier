package com.smartfileorganizer.util;

import com.smartfileorganizer.model.FileCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Parses the filetypes.xml configuration file and builds
 * the extension-to-category mapping used by the organizer service.
 */
public class XMLParser {

    private static final Logger logger = LoggerUtil.getLogger();

    private XMLParser() {
    }

    /**
     * Parses the XML configuration and returns a list of FileCategory objects.
     */
    public static List<FileCategory> parseCategories() {
        List<FileCategory> categories = new ArrayList<>();

        try (InputStream is = XMLParser.class.getResourceAsStream("/filetypes.xml")) {
            if (is == null) {
                logger.severe("filetypes.xml not found in resources");
                return categories;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Disable external entities for security
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();

            NodeList categoryNodes = document.getElementsByTagName("category");

            for (int i = 0; i < categoryNodes.getLength(); i++) {
                Element categoryElement = (Element) categoryNodes.item(i);
                String categoryName = categoryElement.getAttribute("name");

                FileCategory fileCategory = new FileCategory(categoryName);

                NodeList extensionNodes = categoryElement.getElementsByTagName("extension");
                for (int j = 0; j < extensionNodes.getLength(); j++) {
                    String ext = extensionNodes.item(j).getTextContent().trim().toLowerCase();
                    fileCategory.addExtension(ext);
                }

                categories.add(fileCategory);
                logger.fine("Loaded category: " + fileCategory);
            }

            logger.info("Loaded " + categories.size() + " file categories from XML configuration");

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.severe("Failed to parse filetypes.xml: " + e.getMessage());
        }

        return categories;
    }

    /**
     * Builds a HashMap mapping each file extension to its category name.
     */
    public static Map<String, String> buildExtensionMap(List<FileCategory> categories) {
        Map<String, String> extensionMap = new HashMap<>();
        for (FileCategory category : categories) {
            for (String ext : category.getExtensions()) {
                extensionMap.put(ext, category.getName());
            }
        }
        return extensionMap;
    }
}
