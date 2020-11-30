package com.example.walkinpaws.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walkinpaws.R;
import com.example.walkinpaws.model.Pet;

import java.io.IOException;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder>
{

    List<Pet> petList;
    Context context;

    public PetAdapter(List<Pet> petList, Context context)
    {
        this.petList = petList;
        this.context = context;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_row_layout, parent,
                false);

        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position)
    {
        holder.load(petList.get(position));
    }

    public void addPet(Pet pet)
    {
        this.petList.add(pet);
        notifyItemInserted(petList.size() - 1);
    }

    @Override
    public int getItemCount()
    {
        return petList.size();
    }

    public class PetViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView nameEditText;
        private final TextView speciesEditText;
        private final TextView genderEditText;
        private final ImageView imageView;

        public PetViewHolder(@NonNull View itemView)
        {
            super(itemView);

            nameEditText = itemView.findViewById(R.id.txtvwdestino);
            speciesEditText = itemView.findViewById(R.id.txtvworigem);
            genderEditText = itemView.findViewById(R.id.txtvwinicioeconclusao);
            imageView = itemView.findViewById(R.id.imgvwpawpet);
        }

        public void load(Pet pet)
        {
            nameEditText.setText(pet.getName());
            speciesEditText.setText(pet.getSpeciesName());
            genderEditText.setText(pet.getGender());

            Bitmap bitmap;

            if (pet.getPhotoFileName() != null)
            {
                try
                {
                    bitmap = pet.getImageBitmap(context);

                    if (bitmap != null)
                    {
                        imageView.setImageBitmap(bitmap);
                    }

                } catch (IOException ex)
                {
                    String TAG = "beep-pet-adapter";
                    Log.e(TAG, "load: ", ex);
                }
            }
        }
    }
}

