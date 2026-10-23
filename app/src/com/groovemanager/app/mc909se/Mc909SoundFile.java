/*
 * Created on 23.10.2026
 *
 */
package com.groovemanager.app.mc909se;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.widgets.Combo;

/**
 * Reads the names of the patches and rhythm sets in the card bank of the
 * MC-909 from <code>ROLAND/SND/snd909.svd</code> on the memory card.
 * The names of the user bank are stored inside the MC-909 and can´t be read
 * this way.
 * @author Manu Robledo
 *
 */
public class Mc909SoundFile {
	/**
	 * The section IDs of the patches and rhythm sets in the svd file
	 */
	public final static String PATCHES = "PAT", RHYTHM_SETS = "RHY";
	/**
	 * The names of empty patches and rhythm sets
	 */
	public final static String EMPTY_PATCH = "INIT PATCH", EMPTY_RHYTHM_SET = "INIT RHYTHM";
	/**
	 * Path of the sound file relative to the card´s root
	 */
	final static String SND_FILE = "ROLAND" + File.separator + "SND" + File.separator + "snd909.svd";
	/**
	 * Length of a patch or rhythm set name
	 */
	final static int NAME_LENGTH = 12;

	/**
	 * Read the names of one section of the card´s sound file.
	 * @param cardRoot The root path of the memory card
	 * @param section PATCHES or RHYTHM_SETS
	 * @return The names (without trailing spaces), or null if the file
	 * doesn´t exist or couldn´t be read
	 */
	public static String[] readNames(String cardRoot, String section){
		if(cardRoot == null) return null;
		if(!cardRoot.endsWith(File.separator)) cardRoot += File.separator;
		File f = new File(cardRoot + SND_FILE);
		if(!f.isFile()) return null;
		try {
			byte[] data = readFile(f);
			if(data.length < 16 || !new String(data, 2, 4, "US-ASCII").equals("SVD1")) return null;
			// Directory entries of 16 bytes: ID (3 chars), type, offset, length
			for(int entry = 16; entry + 16 <= data.length && data[entry] != 0; entry += 16){
				if(!new String(data, entry, 3, "US-ASCII").equals(section)) continue;
				int offset = getInt(data, entry + 8);
				// Section header: count, size of one record, header length
				int count = getInt(data, offset);
				int size = getInt(data, offset + 4);
				int header = getInt(data, offset + 8);
				if(count <= 0 || offset + header + count * size > data.length) return null;
				String[] names = new String[count];
				for(int i = 0; i < count; i++){
					names[i] = readName(data, offset + header + i * size);
				}
				return names;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Fill a Combo with the numbers 1..count. If names are given, each
	 * number is followed by its name, empty slots are marked, and the
	 * first empty slot gets selected.
	 * @param combo The Combo to fill
	 * @param count The number of slots
	 * @param names The names of the slots or null if unknown
	 * @param emptyName The name of an empty slot
	 */
	public static void fillCombo(Combo combo, int count, String[] names, String emptyName){
		int selection = Math.max(combo.getSelectionIndex(), 0);
		combo.removeAll();
		int firstEmpty = -1;
		for(int i = 0; i < count; i++){
			if(names == null || i >= names.length) combo.add("" + (i + 1));
			else if(names[i].equals(emptyName)){
				combo.add((i + 1) + "  (empty)");
				if(firstEmpty == -1) firstEmpty = i;
			}
			else combo.add((i + 1) + "  " + names[i]);
		}
		if(names != null && firstEmpty != -1) selection = firstEmpty;
		combo.select(Math.min(selection, count - 1));
	}
	/**
	 * Names are stored as 7-bit characters packed into a bit stream
	 * @param data The file data
	 * @param pos The start of the record
	 * @return The name without trailing spaces
	 */
	private static String readName(byte[] data, int pos){
		char[] c = new char[NAME_LENGTH];
		for(int i = 0; i < NAME_LENGTH; i++){
			int value = 0;
			for(int bit = i * 7; bit < i * 7 + 7; bit++){
				value = (value << 1) | ((data[pos + bit / 8] >> (7 - bit % 8)) & 1);
			}
			c[i] = value < 32 ? ' ' : (char)value;
		}
		String s = new String(c);
		int end = s.length();
		while(end > 0 && s.charAt(end - 1) == ' ') end--;
		return s.substring(0, end);
	}
	private static int getInt(byte[] data, int pos){
		return ((data[pos] & 0xFF) << 24) | ((data[pos + 1] & 0xFF) << 16)
			| ((data[pos + 2] & 0xFF) << 8) | (data[pos + 3] & 0xFF);
	}
	private static byte[] readFile(File f) throws IOException{
		byte[] data = new byte[(int)f.length()];
		InputStream in = new FileInputStream(f);
		try {
			int read = 0;
			while(read < data.length){
				int r = in.read(data, read, data.length - read);
				if(r < 0) break;
				read += r;
			}
		}
		finally {
			in.close();
		}
		return data;
	}
}
