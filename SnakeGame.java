import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.io.*;
import java.util.*;

public class SnakeGame extends JPanel implements ActionListener {
    private final int width, height, cellSize;
    private final Random random = new Random();
    private boolean gameStarted = false, gameOver = false, paused = false, showDifficulty = true;
    private boolean wrapAround = false;
    private boolean darkTheme = true;
    private int highScore = 0, gameSpeed = 100, foodPulse = 0, pulseDir = 1;
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
        if (showDifficulty) {
            switch (keyCode) {
                case KeyEvent.VK_1 -> gameSpeed = 120;
                case KeyEvent.VK_2 -> gameSpeed = 80;
                case KeyEvent.VK_3 -> gameSpeed = 50;
                default -> { return; }
            }
            timer.setDelay(gameSpeed);
            showDifficulty = false;
            gameStarted = true;
            return;
        }

        if (!gameStarted && keyCode == KeyEvent.VK_ENTER) {
            gameStarted = true;
        } else if (gameOver && keyCode == KeyEvent.VK_ENTER) {
            gameStarted = false;
            gameOver = false;
            showDifficulty = true;
            resetGameData();
        } else if (keyCode == KeyEvent.VK_P && gameStarted && !gameOver) {
            paused = !paused;
        } else if (keyCode == KeyEvent.VK_W) {
            wrapAround = !wrapAround;
        } else if (keyCode == KeyEvent.VK_T) {
            darkTheme = !darkTheme;
            setBackground(darkTheme ? Color.BLACK : Color.WHITE);
        } else if (!gameOver) {
            switch (keyCode) {
                case KeyEvent.VK_UP -> { if (direction != Direction.DOWN) newDirection = Direction.UP; }
                case KeyEvent.VK_DOWN -> { if (direction != Direction.UP) newDirection = Direction.DOWN; }
                case KeyEvent.VK_LEFT -> { if (direction != Direction.RIGHT) newDirection = Direction.LEFT; }
                case KeyEvent.VK_RIGHT -> { if (direction != Direction.LEFT) newDirection = Direction.RIGHT; }
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

        if (showDifficulty) {
            printMessage(g, "Select Difficulty:\n1 - Easy\n2 - Medium\n3 - Hard");
            return;
        }

        if (!gameStarted) {
            printMessage(g, "Press ENTER to Begin Game");
            return;
        }

        int pulseSize = cellSize / 2 + foodPulse;
        g2d.setColor(Color.RED);
        g2d.fillOval(food.x + (cellSize - pulseSize) / 2, food.y + (cellSize - pulseSize) / 2, pulseSize, pulseSize);

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

        // Sidebar UI Panel
        g2d.setColor(darkTheme ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        g2d.setFont(new Font("Consolas", Font.BOLD, 16));
        g2d.drawString("Score: " + (snake.size() - 3), 10, 20);
        g2d.drawString("Speed: " + (1000 / gameSpeed) + " FPS", 10, 40);
        g2d.drawString("Wrap: " + (wrapAround ? "ON" : "OFF"), 10, 60);
        g2d.drawString("Theme: " + (darkTheme ? "Dark" : "Light"), 10, 80);
        g2d.drawString("[P]ause | [W]rap | [T]heme", 10, 100);

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
        int x = head.x;
        int y = head.y;

        switch (direction) {
            case UP -> y -= cellSize;
            case DOWN -> y += cellSize;
            case LEFT -> x -= cellSize;
            case RIGHT -> x += cellSize;
        }

        if (wrapAround) {
            if (x < 0) x = width - cellSize;
            if (x >= width) x = 0;
            if (y < 0) y = height - cellSize;
            if (y >= height) y = 0;
        }

        GamePoint newHead = new GamePoint(x, y);
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
        boolean wall = !wrapAround && (head.x < 0 || head.x >= width || head.y < 0 || head.y >= height);
        boolean self = new HashSet<>(snake).size() != snake.size();
        return wall || self;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gameOver && !paused && !showDifficulty) {
            move();
        }
        foodPulse += pulseDir;
        if (foodPulse >= 6 || foodPulse <= 0) pulseDir *= -1;
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