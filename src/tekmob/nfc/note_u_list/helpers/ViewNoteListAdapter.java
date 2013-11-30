package tekmob.nfc.note_u_list.helpers;

import java.util.List;

import tekmob.nfc.note_u_list.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewNoteListAdapter extends ArrayAdapter<ViewNoteListObject> {

	private List<ViewNoteListObject> list;
	private LayoutInflater inflator;

	public ViewNoteListAdapter(Activity context, List<ViewNoteListObject> list) {
		super(context, R.layout.row_view_note_list_adapter, list);
		this.list = list;
		inflator = context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflator.inflate(R.layout.row_view_note_list_adapter,
					null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.note_type);
			holder.title = (TextView) convertView.findViewById(R.id.note_title);
			convertView.setTag(holder);
			convertView.setTag(R.id.note_type, holder.image);
			convertView.setTag(R.id.note_title, holder.title);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.image.setTag(position);

		if (list.get(position).getType().equals(ViewNoteListObject.TYPE_TEXT)) {
			holder.image.setImageResource(R.drawable.ic_note);
		} else if (list.get(position).getType()
				.equals(ViewNoteListObject.TYPE_IMAGE)) {
			holder.image.setImageResource(R.drawable.ic_camera);
		} else if (list.get(position).getType()
				.equals(ViewNoteListObject.TYPE_AUDIO)) {
			holder.image.setImageResource(R.drawable.ic_voicerec);
		} else {
			// TODO image for another filetype
		}

		holder.title.setText(list.get(position).getTitle());

		return convertView;
	}

	static class ViewHolder {
		protected ImageView image;
		protected TextView title;
	}

}
