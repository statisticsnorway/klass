package no.ssb.klass.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class AdminUtils {
    private AdminUtils() {        
    }
    
    public static InputStream createInputStream(String exportToExcelData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            writeUtf8ByteOrderMark(outputStream);
            outputStream.write(exportToExcelData.getBytes(StandardCharsets.UTF_8));
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("unable to convert data to CsvStream", e);
        }
    }

    private static void writeUtf8ByteOrderMark(ByteArrayOutputStream outputStream) throws IOException {
        outputStream.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
    }
}
