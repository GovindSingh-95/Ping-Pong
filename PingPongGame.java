import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PingPongGame extends JPanel implements KeyListener, Runnable {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final int PADDLE_SPEED = 5;
    private static final int INITIAL_BALL_SPEED = 3;
    private static final double MAX_SPEED_MULTIPLIER = 3.0; // Maximum speed limit

    private int paddle1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int paddle2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int ballX = WIDTH / 2 - BALL_SIZE / 2;
    private int ballY = HEIGHT / 2 - BALL_SIZE / 2;
    private int ballXDir = INITIAL_BALL_SPEED;
    private int ballYDir = INITIAL_BALL_SPEED;

    private int playerScore = 0;
    private int computerScore = 0;
    private boolean gameOver = false;

    private boolean up = false;
    private boolean down = false;

    private long startTime = System.currentTimeMillis();
    private double speedMultiplier = 1.0;

    public PingPongGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(20, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - 30, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Player: " + playerScore, 20, 30);
        g.drawString("Computer: " + computerScore, WIDTH - 150, 30);
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String winner = (playerScore >= 10) ? "You Win!" : "Computer Wins!";
            g.drawString(winner, WIDTH / 2 - 150, HEIGHT / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_S) down = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_S) down = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void run() {
        while (!gameOver) {
            update();
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (up && paddle1Y > 0) paddle1Y -= PADDLE_SPEED;
        if (down && paddle1Y < HEIGHT - PADDLE_HEIGHT) paddle1Y += PADDLE_SPEED;

        if (paddle2Y + PADDLE_HEIGHT / 2 < ballY) {
            paddle2Y += PADDLE_SPEED;
        } else if (paddle2Y + PADDLE_HEIGHT / 2 > ballY) {
            paddle2Y -= PADDLE_SPEED;
        }

        if (paddle2Y < 0) paddle2Y = 0;
        if (paddle2Y > HEIGHT - PADDLE_HEIGHT) paddle2Y = HEIGHT - PADDLE_HEIGHT;

        ballX += (int) (ballXDir * speedMultiplier);
        ballY += (int) (ballYDir * speedMultiplier);

        if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) {
            ballYDir = -ballYDir;
        }

        if (ballX <= 20 + PADDLE_WIDTH && ballY + BALL_SIZE >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) {
            ballXDir = Math.abs(ballXDir);
        }
        if (ballX >= WIDTH - 30 - BALL_SIZE && ballY + BALL_SIZE >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT) {
            ballXDir = -Math.abs(ballXDir);
        }

        if (ballX <= 0) {
            computerScore++;
            resetBall();
        }
        if (ballX >= WIDTH - BALL_SIZE) {
            playerScore++;
            resetBall();
        }

        if (playerScore >= 10 || computerScore >= 10) {
            gameOver = true;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        speedMultiplier = 1.0 + (elapsedTime / 10000.0); // Increase speed every 10 seconds

        // Cap the speed multiplier to prevent it from getting out of control
        if (speedMultiplier > MAX_SPEED_MULTIPLIER) {
            speedMultiplier = MAX_SPEED_MULTIPLIER;
        }
    }

    private void resetBall() {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballXDir = (Math.random() > 0.5) ? INITIAL_BALL_SPEED : -INITIAL_BALL_SPEED;
        ballYDir = (Math.random() > 0.5) ? INITIAL_BALL_SPEED : -INITIAL_BALL_SPEED;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ping Pong Game - Computer vs Player");
        PingPongGame game = new PingPongGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        new Thread(game).start();
    }
}