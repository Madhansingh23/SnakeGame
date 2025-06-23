# 🐍 Java Swing Snake Game

A feature-rich Snake Game developed in Java using the Swing framework. This version features dynamic visuals, difficulty levels, wrap-around mode, animated food pulses, high score saving, and UI enhancements — all without external libraries.

---

## 🚀 Features

| Feature                    | Description                                                                 |
|---------------------------|-----------------------------------------------------------------------------|
| 🎮 Difficulty Selection    | Press `1`, `2`, or `3` to select Easy, Medium, or Hard at the start        |
| ⏸ Pause & Resume          | Press `P` to pause/resume the game anytime                                 |
| 🔁 Wrap-around Toggle      | Press `W` to enable/disable screen-edge wrapping                            |
| 🎨 Theme Switching         | Press `T` to toggle between Dark and Light themes                          |
| 🍬 Animated Food Pulse     | Food pulses visually with a circular animation                             |
| 📊 UI Panel                | Displays score, speed (FPS), wrap mode, theme status, and controls         |
| 💾 High Score Persistence  | Saves your highest score in `highscore.dat` for the next play session      |
| 🧠 Performance Optimized   | Uses efficient data structures like `Deque`, double buffering, and caching  |
| 🎯 Modern UI               | Smooth anti-aliased graphics with colored gradients                         |

---

## 🎮 Controls

| Key         | Action                            |
|-------------|-----------------------------------|
| `1/2/3`     | Select difficulty (Easy/Medium/Hard) |
| `ENTER`     | Start or Restart the game         |
| `↑ ↓ ← →`   | Move the snake                    |
| `P`         | Pause / Resume                    |
| `W`         | Toggle wrap-around mode           |
| `T`         | Toggle dark/light theme           |

---

## 🎯 Objective

- Eat red pulsing food pellets to grow your snake.
- Avoid collisions with walls (unless wrap mode is ON) or your own body.
- Try to beat your previous high score!

---

## 🔗 Clone This Game

```bash
git clone https://github.com/Madhansingh23/SnakeGame.git
cd SnakeGame
javac Main.java SnakeGame.java
java Main


## 🛠 Setup & Run
- Setup java with environmental setup 
- use javac Main.java (to compile)
- use java Main (to run)


### 🧱 Requirements
- Java JDK 17 or later
- Any IDE (VS Code / IntelliJ / Eclipse) or terminal

### 📦 File Structure
```bash
SnakeGame/
├── Main.java           # Launches the JFrame with SnakeGame panel
├── SnakeGame.java      # Main game logic and rendering
├── highscore.dat       # Auto-created to save high score (optional)


### 🖼️ Game Setup Preview

![Level Selection Screenshot](outputs\Level-setup.png)
![Theme Toggle Screenshot](outputs\Light-background.png)
![High Score Screenshot](outputs\High-score.png)
![Pause Option Screenshot](outputs\Pause-option.png)
![Sidebar Components Screenshot](outputs\Snake-sidebar.png)
