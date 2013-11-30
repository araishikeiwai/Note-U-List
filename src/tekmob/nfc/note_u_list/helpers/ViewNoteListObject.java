package tekmob.nfc.note_u_list.helpers;

public class ViewNoteListObject {
	public static final String FILENAME = "FILENAME";
	public static final String FILETYPE = "FILETYPE";
	public static final String FILETITLE = "FILETITLE";

	public static final String TYPE_TEXT = "text/*";
	public static final String TYPE_IMAGE = "image/*";
	public static final String TYPE_AUDIO = "audio/*";

	private String title;
	private String type;
	private String filename;

	public ViewNoteListObject(String title, String type, String filename) {
		this.title = title;
		this.type = type;
		this.setFilename(filename);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
