package com.example.healthpet3.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.healthpet3.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditOptionsMenuFragment extends BottomSheetDialogFragment {

    public interface EditOptionsListener {
        void onEditTutor();
        void onEditPet();
        void onChatbot();
        void onLogout();
    }

    private EditOptionsListener listener;

    public static EditOptionsMenuFragment newInstance() {
        return new EditOptionsMenuFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditOptionsListener) {
            listener = (EditOptionsListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_options_menu, container, false);

        Button btnEditTutor = view.findViewById(R.id.btnEditTutor);
        Button btnEditPet = view.findViewById(R.id.btnEditPet);
        Button btnChatbot = view.findViewById(R.id.btnChatbot);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        btnEditTutor.setOnClickListener(v -> {
            if (listener != null) listener.onEditTutor();
            dismiss();
        });
        btnEditPet.setOnClickListener(v -> {
            if (listener != null) listener.onEditPet();
            dismiss();
        });
        btnChatbot.setOnClickListener(v -> {
            if (listener != null) listener.onChatbot();
            dismiss();
        });
        btnLogout.setOnClickListener(v -> {
            if (listener != null) listener.onLogout();
            dismiss();
        });

        return view;
    }
}