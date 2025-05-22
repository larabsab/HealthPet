/*
    CustomSpinnerAdapter é um adaptador personalizado para Spinners,
    usado para exibir um item de placeholder (como "Selecione o gênero") com uma cor diferente.
    Itens da posição 0 são renderizados em cinza claro, enquanto os demais usam cor preta.
*/

package com.example.healthpet3.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.healthpet3.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> items) {
        super(context, resource, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        setTextColor(view, position);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        setTextColor(view, position);
        return view;
    }

    private void setTextColor(TextView view, int position) {
        int color = (position == 0)
                ? ContextCompat.getColor(context, R.color.greywhite)
                : ContextCompat.getColor(context, R.color.black);
        view.setTextColor(color);
    }
}