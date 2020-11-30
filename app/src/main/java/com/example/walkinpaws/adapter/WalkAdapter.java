package com.example.walkinpaws.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walkinpaws.R;
import com.example.walkinpaws.model.Pet;
import com.example.walkinpaws.model.Walk;

import java.util.List;

public class WalkAdapter extends RecyclerView.Adapter<WalkAdapter.WalkHolder>
{
    private final List<Walk> walks;
    private final Context context;

    public WalkAdapter(List<Walk> walks, Context context)
    {
        this.walks = walks;
        this.context = context;
    }


    @NonNull
    @Override
    public WalkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.walk_row_layout,
                parent, false);

        return new WalkHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalkHolder holder, int position)
    {
        holder.bind(walks.get(position));
    }

    @Override
    public int getItemCount()
    {
        return walks.size();
    }

    class WalkHolder extends RecyclerView.ViewHolder
    {
        TextView destinyTextView;
        TextView sourceTextView;
        TextView timeTextView;
        TextView petsTextView;

        public WalkHolder(@NonNull View itemView)
        {
            super(itemView);

            destinyTextView = itemView.findViewById(R.id.txtvwdestino);
            sourceTextView = itemView.findViewById(R.id.txtvworigem);
            timeTextView = itemView.findViewById(R.id.txtvwinicioeconclusao);
            petsTextView = itemView.findViewById(R.id.txtvwpetspresentes);
        }

        public void bind(Walk walk)
        {
            destinyTextView.setText(walk.getDestination());
            sourceTextView.setText(walk.getSource());

            String text = walk.getStartTime() + " - " + walk.getEndTime();
            timeTextView.setText(text);

            text = petsTextView.getText() + "\n";
            text += TextUtils.join("\n", Pet.namesFromIDs(walk.getPresentPets(), context));


            petsTextView.setText(text);

        }
    }
}
