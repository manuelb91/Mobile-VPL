package it.unibz.mobilevpl.util;

import it.unibz.mobilevpl.object.Sound;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import android.content.Context;
import android.content.res.AssetManager;

public class FileManager {

	private final static String ROOT_DIRECTORY = "MobileScratch";
	//private final static String IMAGES_DIRECTORY = ROOT_DIRECTORY + File.separator + "Images";
	//private final static String SOUNDS_DIRECTORY = ROOT_DIRECTORY + File.separator + "Sounds";
	private final static String IMAGES_DIRECTORY = "Images";
	private final static String SOUNDS_DIRECTORY = "Sounds";

	private final static String EDITOR_HTML = "index.html";

	public static File getInternalStorageFolder(Context context) {
		return context.getDir(ROOT_DIRECTORY, Context.MODE_PRIVATE);
	}

	public static File getImagesFolder(Context context) {
		return context.getDir(IMAGES_DIRECTORY, Context.MODE_PRIVATE);
	}

	public static File getSoundsFolder(Context context) {
		return context.getDir(SOUNDS_DIRECTORY, Context.MODE_PRIVATE);
	}

	private static String generateUniqueID() {
		return UUID.randomUUID().toString();
	}

	private static String getFileExtension(File file) {
		return file.getAbsolutePath().substring((file.getAbsolutePath().lastIndexOf(".") + 1));
	}

	private static File transferFile(File source, String directory) {
		File destination = new File(directory + File.separator + generateUniqueID() + '.' + getFileExtension(source));

		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(destination);
			byte[] buffer = new byte[1024];
			int length;
			while((length = in.read(buffer)) > 0) {
				out.write(buffer,0,length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {  if(in != null ) in.close(); } catch (Exception e) {}
			try {  if(out != null) out.close(); } catch (Exception e) {}
		}

		return destination;
	}

	public static File transferImageFile(Context context, File source) {
		return transferFile(source, getImagesFolder(context).getAbsolutePath());
	}

	public static File transferImageFile(Context context, String source) {
		return transferFile(new File(source), getImagesFolder(context).getAbsolutePath());
	}

	public static File transferSoundFile(Context context, File source) {
		Sound sound = new Sound();
		sound.setFilename(source.getName());
		File file = transferFile(source, getSoundsFolder(context).getAbsolutePath());
		sound.setPath(getSoundsFolder(context).getAbsolutePath() + File.separator + file.getName());
		sound.store();
		return file;
	}
	
	public static File transferSoundFile(Context context, String source) {
		return transferFile(new File(source), getSoundsFolder(context).getAbsolutePath());
	}

	public static String readHTMLFile(AssetManager assetManager) {
		String result = "";
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(assetManager.open(EDITOR_HTML)));
			String str;
			while ((str = in.readLine()) != null) {
				result += str;
			}
		} catch (IOException e) { 
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static void removeFile(String path) {
		File file = new File(path);
		file.delete();
	}
}
