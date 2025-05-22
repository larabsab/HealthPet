/*
    Modelo de dados que representa um pet com ID, nome, imagem e e-mail do tutor.
    Usado para mapear dados no Firebase Firestore.
*/

package com.example.healthpet3.models;

import java.util.Objects;

public class Pet {
    private String id;
    private String name;
    private String imageUrl;
    private String tutorEmail;
    private String age;
    private String breed;
    private String gender;
    private String weight;
    private String height;
    private String color;
    private String observations;
    private String species;
    private String imageBase64;

    public Pet() {}

    public Pet(String name, String breed, String age, String species, String gender, String observations, String tutorEmail, String weight, String height, String color) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.species = species;
        this.gender = gender;
        this.observations = observations;
        this.tutorEmail = tutorEmail;
        this.weight = weight;
        this.height = height;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTutorEmail() {
        return tutorEmail;
    }

    public String getAge() {
        return age;
    }

    public String getBreed() {
        return breed;
    }

    public String getGender() {
        return gender;
    }

    public String getObservations() {
        return observations;
    }

    public String getSpecies() {
        return species;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getColor() {
        return color;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTutorEmail(String tutorEmail) {
        this.tutorEmail = tutorEmail;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id) && Objects.equals(name, pet.name) && Objects.equals(imageUrl, pet.imageUrl) && Objects.equals(tutorEmail, pet.tutorEmail) && Objects.equals(age, pet.age) && Objects.equals(breed, pet.breed) && Objects.equals(gender, pet.gender) && Objects.equals(weight, pet.weight) && Objects.equals(height, pet.height) && Objects.equals(color, pet.color) && Objects.equals(observations, pet.observations) && Objects.equals(species, pet.species);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageUrl, tutorEmail, age, breed, gender, weight, height, color, observations, species);
    }
}