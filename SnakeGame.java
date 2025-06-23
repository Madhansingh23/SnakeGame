// ✅ Updated SnakeGame.java with Multiple Enhancements:
// - Live Score Display
// - Pause/Resume (P key)
// - Game Over Sound (beep)
// - Save/Load High Score from File

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.io.*;
import java.util.*;

public class SnakeGame extends JPanel implements ActionListener {
    private final int width, height, cellSize;
    private final Random random = new Random();
    private boolean gameStarted = false, gameOver = false, paused = false;
    private int highScore = 0, gameSpeed = 100;
    private GamePoint food;
    private Direction direction = Direction.RIGHT;
    private Direction newDirection = Direction.RIGHT;
    private final Deque<GamePoint> snake = new LinkedList<>();
    private javax.swing.Timer timer;
    private final File highScoreFile = new File("highscore.dat");

    public SnakeGame(int width, int height) {
        this.width = width;
        this.height = height;
        this.cellSize = width / 40;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        loadHighScore();
    }

    public void startGame() {
        resetGameData();
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvent(e.getKeyCode());
            }
        });
        timer = new javax.swing.Timer(gameSpeed, this);
        timer.start();
    }

    private void handleKeyEvent(int keyCode) {
        if (!gameStarted && keyCode == KeyEvent.VK_ENTER) {
            gameStarted = true;
        } else if (gameOver && keyCode == KeyEvent.VK_ENTER) {
            gameStarted = false;
            gameOver = false;
            resetGameData();
        } else if (keyCode == KeyEvent.VK_P && gameStarted && !gameOver) {
            paused = !paused;
        } else if (!gameOver) {
            switch (keyCode) {
                case KeyEvent.VK_UP -> {
                    if (direction != Direction.DOWN) newDirection = Direction.UP;
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != Direction.UP) newDirection = Direction.DOWN;
                }
                case KeyEvent.VK_LEFT -> {
                    if (direction != Direction.RIGHT) newDirection = Direction.LEFT;
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != Direction.LEFT) newDirection = Direction.RIGHT;
                }
            }
        }
    }

    private void resetGameData() {
        snake.clear();
        int centerX = width / 2;
        int centerY = height / 2;
        for (int i = 0; i < 3; i++) {
            snake.add(new GamePoint(centerX - (i * cellSize), centerY));
        }
        direction = Direction.RIGHT;
        newDirection = Direction.RIGHT;
        generateFood();
        gameSpeed = 100;
        if (timer != null) timer.setDelay(gameSpeed);
    }

    private void generateFood() {
        do {
            food = new GamePoint(random.nextInt(width / cellSize) * cellSize,
                    random.nextInt(height / cellSize) * cellSize);
        } while (snake.contains(food));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!gameStarted) {
            printMessage(g, "Press ENTER to Begin Game");
            return;
        }

        // Draw food
        g2d.setColor(Color.RED);
        g2d.fillOval(food.x + cellSize / 4, food.y + cellSize / 4, cellSize / 2, cellSize / 2);

        // Draw snake
        int segment = 0;
        for (GamePoint point : snake) {
            if (segment == 0) {
                g2d.setColor(new Color(0, 255, 0));
                g2d.fillRoundRect(point.x, point.y, cellSize, cellSize, cellSize / 2, cellSize / 2);
            } else {
                int green = Math.max(0, 200 - segment * 5);
                int blue = Math.min(255, segment * 8);
                g2d.setColor(new Color(0, green, blue));
                g2d.fillRoundRect(point.x, point.y, cellSize, cellSize, 8, 8);
            }
            segment++;
        }

        // Live score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Score: " + (snake.size() - 3), 10, 20);

        if (paused) {
            printMessage(g, "Game Paused\nPress P to Resume");
        } else if (gameOver) {
            int score = snake.size() - 3;
            highScore = Math.max(score, highScore);
            saveHighScore();
            Toolkit.getDefaultToolkit().beep();
            printMessage(g, "Your Score: " + score +
                    "\nHigh Score: " + highScore +
                    "\nPress ENTER to Reset");
        }
    }

    private void printMessage(Graphics g, String message) {
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(28F));
        int currentHeight = height / 3;
        Graphics2D g2d = (Graphics2D) g;
        var frc = g2d.getFontRenderContext();

        for (String line : message.split("\n")) {
            var layout = new TextLayout(line, g.getFont(), frc);
            var bounds = layout.getBounds();
            float x = (float) (width - bounds.getWidth()) / 2;
            layout.draw(g2d, x, currentHeight);
            currentHeight += g.getFontMetrics().getHeight();
        }
    }

    private void move() {
        direction = newDirection;
        GamePoint head = snake.peekFirst();

        GamePoint newHead = switch (direction) {
            case UP -> new GamePoint(head.x, head.y - cellSize);
            case DOWN -> new GamePoint(head.x, head.y + cellSize);
            case LEFT -> new GamePoint(head.x - cellSize, head.y);
            case RIGHT -> new GamePoint(head.x + cellSize, head.y);
        };

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            generateFood();
            if (gameSpeed > 30) {
                gameSpeed -= 5;
                timer.setDelay(gameSpeed);
            }
        } else if (isCollision()) {
            gameOver = true;
            snake.removeFirst();
        } else {
            snake.removeLast();
        }
    }

    private boolean isCollision() {
        GamePoint head = snake.peekFirst();
        boolean wall = head.x < 0 || head.x >= width || head.y < 0 || head.y >= height;
        boolean self = new HashSet<>(snake).size() != snake.size();
        return wall || self;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gameOver && !paused) {
            move();
        }
        repaint();
    }

    private void saveHighScore() {
        try (PrintWriter writer = new PrintWriter(highScoreFile)) {
            writer.println(highScore);
        } catch (IOException ignored) {}
    }

    private void loadHighScore() {
        if (highScoreFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(highScoreFile))) {
                highScore = Integer.parseInt(reader.readLine());
            } catch (IOException | NumberFormatException ignored) {}
        }
    }

    private record GamePoint(int x, int y) {}
    private enum Direction { UP, DOWN, LEFT, RIGHT }
}
