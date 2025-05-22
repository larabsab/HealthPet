/*
    Classe utilitária para lidar com formatação de tempo e exibição de datas relativas.
    Usada principalmente para converter objetos Timestamp em strings legíveis para humanos,
    como "3 min atrás", "2 h atrás" ou uma data formatada.
*/

package com.example.healthpet3.utils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeUtils {

    //Retorna uma string relativa (ex: "3 min atrás", "2 h atrás", "05/04/2024") com base no Timestamp fornecido.
    public static String getRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "";

        long now = System.currentTimeMillis();
        long diff = now - timestamp.toDate().getTime();

        long minutes = diff / (1000 * 60);
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        } else if (hours > 0) {
            return hours + " h atrás";
        } else if (minutes > 0) {
            return minutes + " min atrás";
        } else {
            return "agora mesmo";
        }
    }
}
