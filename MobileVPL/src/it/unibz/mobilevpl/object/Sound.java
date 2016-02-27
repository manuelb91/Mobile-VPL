package it.unibz.mobilevpl.object;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Sound extends SugarRecord<Sound> implements Comparable<Sound>, Serializable {
	
	private Project project;
	private String path;
	private String filename;
	
	public Sound() {
	}
	
	public Sound(String path, String filename, Project project) {
		this();
		this.path = path;
		this.filename = filename;
		this.project = project;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public static Sound getSoundById(long id) {
		return Sound.findById(Sound.class, id);
	}
	
	public void store() {
		this.save();
	}
	
	public void remove() {
		this.delete();
	}

	@Override
	public int compareTo(Sound another) {
		return this.filename.compareTo(another.getFilename());
	}
}
