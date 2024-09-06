import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird implements ActionListener, KeyListener {
    public static FlappyBird flappyBird;
    public final int WIDTH = 800, HEIGHT = 600;
    public Renderer renderer;
    public Rectangle bird;
    public ArrayList<Rectangle> pipes;
    public int ticks, yMotion, score;
    public boolean gameOver, started;
    public Random random;

    // Images to hold the bird, tree, and sky
    private Image birdImage;
    private Image treeImage;
    private Image skyImage;

    public FlappyBird() {
        JFrame jframe = new JFrame();
        Timer timer = new Timer(20, this);

        renderer = new Renderer();
        jframe.add(renderer);
        jframe.setTitle("Flappy Bird");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 30, HEIGHT / 2 - 50, 50, 50);
        pipes = new ArrayList<>();
        random = new Random();

        // Load the bird, tree, and sky images
        try {
            birdImage = ImageIO.read(new File("bird.png"));  // Ensure the path to your image is correct
            treeImage = ImageIO.read(new File("tree.png"));  // Ensure the path to your tree image is correct
            skyImage = ImageIO.read(new File("sky.png"));    // Ensure the path to your sky image is correct
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add initial pipes
        addPipe(true);
        addPipe(true);
        addPipe(true);
        addPipe(true);

        timer.start();
    }

    public void addPipe(boolean start) {
        int space = 300;
        int width = 100;
        int height = 50 + random.nextInt(300);

        if (start) {
            pipes.add(new Rectangle(WIDTH + width + pipes.size() * 300, HEIGHT - height - 120, width, height));
            pipes.add(new Rectangle(WIDTH + width + (pipes.size() - 1) * 300, 0, width, HEIGHT - height - space));
        } else {
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }

    public void paintPipe(Graphics g, Rectangle pipe) {
        if (treeImage != null) {
            g.drawImage(treeImage, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
    }

    public void jump() {
        if (gameOver) {
            bird = new Rectangle(WIDTH / 2 - 30, HEIGHT / 2 - 50, 50, 50);
            pipes.clear();
            yMotion = 0;
            score = 0;

            addPipe(true);
            addPipe(true);
            addPipe(true);
            addPipe(true);

            gameOver = false;
        }

        if (!started) {
            started = true;
        } else if (!gameOver) {
            if (yMotion > 0) {
                yMotion = 0;
            }
            yMotion -= 10;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 10;

        ticks++;

        if (started) {
            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= speed;
            }

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;
            }

            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);

                if (pipe.x + pipe.width < 0) {
                    pipes.remove(pipe);

                    if (pipe.y == 0) {
                        addPipe(false);
                    }
                }
            }

            bird.y += yMotion;

            for (Rectangle pipe : pipes) {
                if (pipe.y == 0 && bird.x + bird.width / 2 > pipe.x + pipe.width / 2 - 10 && bird.x + bird.width / 2 < pipe.x + pipe.width / 2 + 10) {
                    score++;
                }

                if (pipe.intersects(bird)) {
                    gameOver = true;
                    bird.x = pipe.x - bird.width;
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;
            }

            if (bird.y + yMotion >= HEIGHT - 120) {
                bird.y = HEIGHT - 120 - bird.height;
            }
        }

        renderer.repaint();
    }

    public void repaint(Graphics g) {
        // Draw the sky image as the background
        if (skyImage != null) {
            g.drawImage(skyImage, 0, 0, WIDTH, HEIGHT, null);  // Draw the sky image to cover the entire background
        }

s

        // Draw the bird image instead of the red rectangle
        if (birdImage != null) {
            g.drawImage(birdImage, bird.x, bird.y, bird.width, bird.height, null);
        } else {
            g.setColor(Color.red);
            g.fillRect(bird.x, bird.y, bird.width, bird.height);
        }

        for (Rectangle pipe : pipes) {
            paintPipe(g, pipe);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));

        if (!started) {
            g.drawString("Press Space to Start", 75, HEIGHT / 2 - 50);
        }

        if (gameOver) {
            g.drawString("Game Over", 100, HEIGHT / 2 - 50);
        }

        if (!gameOver && started) {
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        flappyBird = new FlappyBird();
    }
}

class Renderer extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        FlappyBird.flappyBird.repaint(g);
    }
}
