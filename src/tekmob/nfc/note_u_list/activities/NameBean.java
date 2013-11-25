package tekmob.nfc.note_u_list.activities;



public class NameBean  implements Comparable<NameBean>{

	private String name;
	private boolean selected;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public int compareTo(NameBean other) { 
        if(this.name != null) 
             return this.name.compareTo(other.getName()); 
        else 
             throw new IllegalArgumentException(); 
   }

}