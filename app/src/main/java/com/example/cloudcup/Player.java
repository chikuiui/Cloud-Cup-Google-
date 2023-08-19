package com.example.cloudcup;

public class Player {
    public String name;
    public String imageUrl;
    public int score;

    public Player() {
        // Default constructor required for Firebase
    }

    public Player(String name, String imageUrl, int score) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.score = score;
    }
}