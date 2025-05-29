# Geometry Dash FC

A simplified Geometry Dash clone made in Java for a school assignment. Built using Java Swing and AWT, the game replicates core features of the original Geometry Dash, with the potential for custom levels and new features later.

![Java](https://img.shields.io/badge/Java-17+-red)
![Built with IntelliJ](https://img.shields.io/badge/IDE-IntelliJ-blue)
![UI](https://img.shields.io/badge/Swing-AWT%20%2F%20Swing-green)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow)
![Entry](https://img.shields.io/badge/Main%20Class-GDmain-blue)
![Platform](https://img.shields.io/badge/Platform-PC%20Only-lightgrey)
![Authors](https://img.shields.io/badge/Authors-Isehiya%20%26%20ay4325434-cyan)
![License](https://img.shields.io/badge/License-Educational-blueviolet)

---

## 🚀 Progress  

<details>
   <summary>Progress</summary>

| Feature             | Status         |
|---------------------|----------------|
| Working JFrame      | ✅ Done         |
| Working Graphics    | ✅ Done         |
| Loaded Icons        | ✅ Done         |
| Proper Menu         | 🔄 Planned     |
| Working Menu        | 🔄 Planned     |
| Moving Background   | ✅ Done         |
| Basic Movement      | 🔲 Not Started |
| Basic Gravity       | 🔄 Planned     |
| Collision           | 🔄 Planned     |
| Music               | 🔄 Planned     |
| Death Effects       | 🔄 Planned     |
| Reset on Death      | 🔄 Planned     |
| Pause Menu          | 🔄 Planned     |
| ------------------- | -----------    |
| Game                | ⚠️ WIP         |

</details>

---

## 👥 Contributors
<details>
   <summary> Contributors </summary>

- Isehiya

- ay4325434
</details>

---

## 🎮 Game Description
<details>
   <summary>Game Description</summary>


Geometry Dash FC is a one-player platformer game inspired by the original Geometry Dash. The goal is to jump over obstacles and survive until the end of the level without crashing into a hazardous hitbox.

   <details>
      <summary>Player Controls</summary>

   ### Player Controls:

   The player uses the keyboard (spacebar or up-arrow) to make the character jump.

   </details>
   
   <details>
      <summary>Gameplay</summary>

   ### Gameplay:

   The game runs automatically (side-scrolling), and the player must time their jumps to avoid hitting spikes or incoming blocks.

   The player dies and restarts the level if they hit an obstacle.

   </details>

   <details>
      <summary>Graphics</summary>

   ### Graphics:

   Graphics are built using Java Swing, AWT, and Geom.

   The player character and obstacles are represented using simple geometric shapes.

   </details>

   <details>
      <summary>Levels</summary>

   ### Levels:

   The game will include at least one fully playable level.

   If there is time, more maps will be added with increasing difficulty.

   </details>

   <details>
      <summary>Timer & Speed</summary>

   ### Timer & Speed:

   The level has a consistent auto-scroll speed, simulating the flow of classic Geometry Dash.

   </details>

   <details>
      <summary>Sound and Effects</summary>

   ### Sound and Effects:

   If time allows, simple background music or sound effects may be added using Java’s audio libraries.

   </details>

</details>

---

## 🛠️ Project Steps

<details>
   <summary>Project Steps</summary>

### 🔢 Development Timeline (Rough Order)

- Create main game window (GDmain) and set up canvas – Isehiya

- Design and create a working starting menu - Both

- Draw the player character and level obstacles – Isehiya

- Implement basic physics and jumping mechanics – ay4325434

- Set up the game loop and auto-scroll movement – Isehiya

- Add collision detection for obstacles – ay4325434

- Create one playable map layout with obstacles – ay4325434

- Add restart and death screen handling – Both

- Polish graphics and user interface – Both

- (Optional) Add second level – Both

</details>

---

## ⚠️ Anticipated Challenges

<details>
   <summary>Anticipated Challenges</summary>

### Smooth game loop: 
Java Swing isn’t designed for real-time games, so syncing frame updates smoothly may be difficult.

### Collision accuracy: 
Making sure the player’s hitbox is fair and responsive.

### Jump timing and feel: 
Tweaking gravity and jump height for good gameplay feel.

### Graphics design: 
Making simple but visually appealing graphics using only geometric shapes.

### Audio (if attempted): 
Loading and syncing sound effects correctly in Java.

</details>

---

## 💡 New Programming Concepts to Learn

<details>
   <summary>New Programming Concepts to Learn</summary>

Implementing a game loop with Swing timers or threads.

Collision detection using shapes from java.awt.geom.

Creating a responsive UI layout with canvas-based drawing.

Possibly playing audio using Clip or AudioInputStream in Java.

</details>

---

## 🎮 Features
<details>
   <summary> Features </summary>

- Side-scrolling platformer gameplay
- Jump mechanics and obstacle collision
- Java Swing-based user interface
- Expandable level design (planned)
- Clean object-oriented code
</details>

---

## 🛠️ Technologies Used
<details>
   <summary>Technologies Used</summary>

- Java (JDK 17+ recommended)
- Swing (`javax.swing`)
- AWT (`java.awt`, `java.awt.geom`)
- IntelliJ IDEA
- Main class: `GDmain`
</details>

---
## ✅ Make sure Java is installed and configured (Project SDK set to Java 17+).

---

## 🚀 How to Run

<details>
   <summary>How to Run</summary>

Clone the repository:

   ```bash
   git clone https://github.com/Isehiya/Geometry-Dash-FC.git
   ```

Run the GDmain class to launch the game.

</details>

---

## ⚠️ Development Notes

<details>
   <summary>Development Notes</summary>

Still troubleshooting and building core functionality.

Level/map expansion is planned if time allows.

Project is in active development.

</details>

---

## 📌 Disclaimer

<details>
   <summary> Disclaimer </summary>

This project is for educational use only and is not affiliated with or endorsed by the official Geometry Dash game or developers.

</details>