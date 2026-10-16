package com.example.maxlab3.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.maxlab3.R;
import com.example.maxlab3.model.ChecklistNote;
import com.example.maxlab3.model.Note;
import com.example.maxlab3.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/** แสดงโน้ตแต่ละอันพร้อมข้อมูลเจ้าของ (User) */
public class NoteAdapter extends ArrayAdapter<Note> {

    private final LayoutInflater inflater;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public NoteAdapter(@NonNull Context context) {
        super(context, 0, new ArrayList<>());
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView != null ? convertView
                : inflater.inflate(R.layout.item_note, parent, false);

        Note note = getItem(position);
        if (note == null) return view;

        TextView tvType = view.findViewById(R.id.tvNoteType);
        TextView tvTitle = view.findViewById(R.id.tvNoteTitle);
        TextView tvSummary = view.findViewById(R.id.tvNoteSummary);
        TextView tvOwner = view.findViewById(R.id.tvNoteOwner);

        tvType.setText(note instanceof ChecklistNote
                ? R.string.type_checklist_note : R.string.type_text_note);
        tvTitle.setText(note.getTitle());
        tvSummary.setText(note.getSummary());

        User owner = note.getOwner();
        String ownerName = owner != null ? owner.getUsername() : "-";
        tvOwner.setText(getContext().getString(R.string.note_owner_format,
                ownerName, dateFormat.format((Date) note.getCreatedDate())));
        return view;
    }
}
