package it.unibz.mobilevpl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Converter {

	public static byte[] compress(String text) throws IOException {
		if(text == null || text.length() == 0)
			return null;
		ByteArrayOutputStream os = new ByteArrayOutputStream(text.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(text.getBytes());
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}

	public static String decompress(byte[] compressed) throws IOException {
		if(compressed == null || compressed.length == 0)
			return "";
		final int BUFFER_SIZE = 32;
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			string.append(new String(data, 0, bytesRead));
		}
		gis.close();
		is.close();
		return string.toString();
	}
}
