/*
    Representa uma atualização de status de um pet,
    armazenando tipo, data, veterinário responsável,
    descrição e estado atual.
*/

package com.example.healthpet3.models;

import java.io.Serializable;
import com.google.firebase.Timestamp;

public class Status implements Serializable {
    private String type;
    private Timestamp timestamp;
    private String vetName;
    private String description;
    private String status;
    private String icon;

    public Status() {}

    public Status(String type, String vetName, Timestamp timestamp, String description, String status, String icon) {
        this.type = type;
        this.vetName = vetName;
        this.timestamp = timestamp;
        this.description = description;
        this.status = status;
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getVetName() {
        return vetName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getIcon(){
        return icon;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }
}