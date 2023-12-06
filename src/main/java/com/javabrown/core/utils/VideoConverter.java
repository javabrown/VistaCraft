package com.javabrown.core.utils;

import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.plugins.jpeg.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VideoConverter {

    public static void convertToVideo(List<BufferedImage> frames, String outputFilePath, double frameRate)
            throws IOException {
        ImageWriter writer = null;
        ImageOutputStream ios = null;

        try {
            File outputFile = new File(outputFilePath);
            ios = ImageIO.createImageOutputStream(outputFile);

            ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_3BYTE_BGR);
            ImageWriteParam writeParam = createWriteParam(imageTypeSpecifier, frameRate,
                    frames.get(0).getWidth(), frames.get(0).getHeight());

            // Choose a suitable format, e.g., "avi" or "mp4"
            String outputFormat = "mov";
            Iterator<ImageWriter> writerIter = ImageIO.getImageWriters(imageTypeSpecifier, outputFormat);
            if (writerIter.hasNext()) {
                writer = writerIter.next();
                writer.setOutput(ios);

                writer.prepareWriteSequence(null);

                for (BufferedImage image : frames) {
                    IIOImage iioImage = new IIOImage(image, null,
                            createMetadata(imageTypeSpecifier, frameRate, image.getWidth(), image.getHeight()));
                    writer.writeToSequence(iioImage, writeParam);
                }

                writer.endWriteSequence();
            } else {
                throw new UnsupportedOperationException("No suitable writer found for format: " + outputFormat);
            }
        } finally {
            if (ios != null) {
                ios.close();
            }
            if (writer != null) {
                writer.dispose();
            }
        }
    }

    private static ImageWriteParam createWriteParam(ImageTypeSpecifier imageTypeSpecifier, double frameRate, int width, int height) {
        ImageWriteParam writeParam = new JPEGImageWriteParam(Locale.getDefault());

        IIOMetadata metadata = createMetadata(imageTypeSpecifier, frameRate, width, height);
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        //writeParam.setCompressionType(metadata.getCompressionTypes()[0]);
        writeParam.setCompressionQuality(1.0f);
        //writeParam.setLocale(Locale.getDefault());

        return writeParam;
    }

    private static IIOMetadata createMetadata(ImageTypeSpecifier imageTypeSpecifier, double frameRate, int width, int height) {
        IIOMetadata metadata = createDefaultMetadata(imageTypeSpecifier, width, height);
        setVideoAttributes(metadata, frameRate);
        return metadata;
    }

    private static IIOMetadata createDefaultMetadata(ImageTypeSpecifier imageTypeSpecifier, int width, int height) {
        ImageTypeSpecifier type = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_3BYTE_BGR);
        IIOMetadata metadata = createStreamMetadata(type);

        // Add necessary information for consistency
        Element tree = (Element) metadata.getAsTree(metadata.getNativeMetadataFormatName());
        IIOMetadataNode dimension = new IIOMetadataNode("Dimension");
        IIOMetadataNode imageDescriptor = new IIOMetadataNode("ImageDescriptor");

        dimension.setAttribute("pixelAspectRatio", "1:1");
        imageDescriptor.setAttribute("imageWidth", Integer.toString(width));
        imageDescriptor.setAttribute("imageWidth", Integer.toString(height));

        imageDescriptor.setAttribute("imageHeight", Integer.toString(height));

        tree.appendChild(dimension);
        tree.appendChild(imageDescriptor);

        try {
            metadata.setFromTree(metadata.getNativeMetadataFormatName(), tree);
        } catch (IIOInvalidTreeException e) {
            throw new RuntimeException(e);
        }

        return metadata;
    }

    private static IIOMetadata createStreamMetadata(ImageTypeSpecifier imageTypeSpecifier) {
        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");

        IIOMetadataNode node = new IIOMetadataNode("Chroma");
        node.setAttribute("ColorSpaceType", "RGB");
        root.appendChild(node);

        IIOMetadataNode app0JFIF = new IIOMetadataNode("app0JFIF");
        app0JFIF.setAttribute("majVersion", "1");
        app0JFIF.setAttribute("minVersion", "1");
        app0JFIF.setAttribute("thumbWidth", "0");
        app0JFIF.setAttribute("thumbHeight", "0");
        app0JFIF.setAttribute("resUnits", "0");
        app0JFIF.setAttribute("Xdensity", "1");
        app0JFIF.setAttribute("Ydensity", "1");
        app0JFIF.setAttribute("thumbWidth", "0");
        app0JFIF.setAttribute("thumbHeight", "0");
        root.appendChild(app0JFIF);

        IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");
        root.appendChild(markerSequence);

        IIOMetadataNode app2ICC = new IIOMetadataNode("app2ICC");
        markerSequence.appendChild(app2ICC);

        return new IIOMetadata() {
            @Override
            public String[] getMetadataFormatNames() {
                return new String[]{"javax_imageio_1.0"};
            }

            @Override
            public boolean isReadOnly() {
                return false;
            }

            @Override
            public Node getAsTree(String formatName) {
                return root;
            }

            @Override
            public void mergeTree(String formatName, Node root) {
                // Not needed for this example
            }

            @Override
            public void reset() {
                // Not needed for this example
            }
        };
    }

    private static void setVideoAttributes(IIOMetadata metadata, double frameRate) {
        String metadataFormatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metadataFormatName);

        IIOMetadataNode child = new IIOMetadataNode("VideoDescriptor");
        child.setAttribute("pixelAspectRatio", "1:1");
        child.setAttribute("frameRate", Double.toString(frameRate));

        root.appendChild(child);

        try {
            metadata.setFromTree(metadataFormatName, root);
        } catch (IIOInvalidTreeException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(List<BufferedImage> frames) {
        // Example usage
        try {
            convertToVideo(frames, "output.mov", 30.0); // Adjust frame rate as needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
