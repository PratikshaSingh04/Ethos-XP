package com.ecohabit.fx;

//Inside Habit.java
public class Habit {
 private int id;
 private String name;
 private String date;
 private String status;
 private String category; // 1. Add this field

 // 2. Update constructor to accept 5 parameters
 public Habit(int id, String name, String date, String status, String category) {
     this.id = id;
     this.name = name;
     this.date = date;
     this.status = status;
     this.category = category;
 }

 // 3. Add this getter so the TableView can find the icon
 public String getCategoryIcon() {
     if (category == null) return "❓";
     if (category.contains("Water")) return "💧";
     if (category.contains("Energy")) return "💡";
     if (category.contains("Waste")) return "♻️";
     if (category.contains("Food")) return "🍎";
     if (category.contains("Transport")) return "🚲";
     return "🌟";
 }

 // Standard getters for name, id, etc.
 public String getName() { return name; }
 public String getStatus() { return status; }
 public int getId() { return id; }

//Add this inside the Habit class
public void setStatus(String status) {
  this.status = status;
}
}
