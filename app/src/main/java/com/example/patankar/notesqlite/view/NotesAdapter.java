package com.example.patankar.notesqlite.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patankar.notesqlite.R;
import com.example.patankar.notesqlite.database.model.Notes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {


    private Context context;
    private List<Notes> noteList;

    public NotesAdapter(Context context, List<Notes> noteList) {
        this.context = context;
        this.noteList = noteList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row,parent,false);

        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notes note =  noteList.get(position);
        holder.note.setText(note.getNote());

        holder.dot.setText(Html.fromHtml("&#8226;"));

        holder.timestamp.setText(formatDate(note.getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }


    public class MyViewHolder extends  RecyclerView.ViewHolder {
        public TextView note;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            dot= view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }


    }

    private String formatDate(String datestr) {
        try {
            SimpleDateFormat fmt =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(datestr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);

        }
        catch (ParseException e){

        }
        return "";

    }

}

