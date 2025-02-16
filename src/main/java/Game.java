import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import com.mongodb.client.*;
import org.bson.Document;
import java.util.List;

public class Game implements KeyListener {
    private int score = 0;
    private MongoConnection db;
    private int remainingTime = 13;
    private Timer timer;
    private JPanel mainPanel;
    private JPanel southPanel;
    private JFrame frame;
    private JLabel titleLabel;
    private Boolean started = false;
    private JTextField nameField;
    public String playerName;

    public Game() {
        // Connect to MongoDB
        db = new MongoConnection();

        // JFrame configuration
        frame = new JFrame("Spacebar Race");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add key listener
        frame.addKeyListener(this);
        frame.setFocusable(true);

        ///////////////////////////////////////////
        //              MENU
        ///////////////////////////////////////////

        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.PINK);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(mainPanel);

        // Title label
        titleLabel = new JLabel("Spacebar Race");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // South panel
        southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(Color.PINK);
        frame.add(southPanel, BorderLayout.SOUTH);

        // Name panel
        JPanel namePanel = new JPanel();
        namePanel.setBackground(Color.PINK);
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setForeground(Color.WHITE);
        nameField = new JTextField(15);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        southPanel.add(namePanel);

        // Start button
        JButton startButton = new JButton("START");
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);
        startButton.setFont(new Font("Arial", Font.BOLD, 15));
        southPanel.add(startButton);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // Start game if player presses start button
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt user for name
                if (nameField.getText().equals("")) {
                    JOptionPane.showMessageDialog(frame, "Please enter a name!");
                    return;
                }
                // Store player name
                playerName = nameField.getText();

                // Empty main panel
                mainPanel.removeAll();
                frame.repaint();
                frame.revalidate();

                started = true;

                // Start game
                startGame();
            }
        });

        frame.setVisible(true);
    }

    private void startGame() {
        ///////////////////////////////////////////
        //              GAMEPLAY
        ///////////////////////////////////////////

        // Timer label
        JLabel timerLabel = new JLabel();
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(timerLabel, BorderLayout.CENTER);

        // Create timer
        timer = new Timer(1300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add title label back to main panel
                mainPanel.add(titleLabel, BorderLayout.NORTH);

                // Countdown
                if (remainingTime == 13) {
                    titleLabel.setText("3...");
                } else if (remainingTime == 12) {
                    titleLabel.setText("2...");
                } else if (remainingTime == 11) {
                    titleLabel.setText("1...");
                } else if (remainingTime == 10) {
                    titleLabel.setText("Press SPACE!");
                    timerLabel.setText("Time left: 10");
                }
                else {
                    // Display timer
                    timerLabel.setText("Time left: " + remainingTime);
                }

                // Decrease remaining time
                remainingTime--;

                // Game over
                if (remainingTime <= 0) {
                    timer.stop();
                    mainPanel.remove(timerLabel);
                    titleLabel.setText("Score: " + score);
                    gameOver();
                }
            }
        });

        // Start timer
        timer.start();
    }

    private void gameOver() {
        // Save current score to database
        db.saveScoreToDatabase(playerName, score);

        // Retrieve top 5 scores from database
        List<Document> topScores = db.getTopScores(5);

        ///////////////////////////////////////////
        //              LEADERBOARD
        ///////////////////////////////////////////

        // Leaderboard panel
        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
        leaderboardPanel.setBackground(Color.PINK);
        leaderboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Leaderboard label
        JLabel leaderboardLabel = new JLabel("Leaderboard:");
        leaderboardLabel.setForeground(Color.WHITE);
        leaderboardLabel.setFont(new Font("Arial", Font.BOLD, 18));
        leaderboardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardPanel.add(leaderboardLabel);

        // Display top scores
        for (int i = 0; i < topScores.size(); i++) {
            Document scoreDoc = topScores.get(i);
            String playerName = scoreDoc.getString("playerName");
            int playerScore = scoreDoc.getInteger("score");
            JLabel scoreLabel = new JLabel((i + 1) + ". " + playerName + ": " + playerScore);
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            leaderboardPanel.add(scoreLabel);
        }

        // Add Leaderboard panel to main
        mainPanel.add(leaderboardPanel, BorderLayout.CENTER);

        ///////////////////////////////////////////
        //              PLAY AGAIN?
        ///////////////////////////////////////////

        // Panel for label and buttons
        JPanel playAgainPanel = new JPanel();
        playAgainPanel.setLayout(new BoxLayout(playAgainPanel, BoxLayout.Y_AXIS));
        playAgainPanel.setBackground(Color.PINK);
        playAgainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(playAgainPanel, BorderLayout.SOUTH);

        // Label for "Play again?"
        JLabel playAgainLabel = new JLabel("Play again?");
        playAgainLabel.setBackground(Color.PINK);
        playAgainLabel.setForeground(Color.WHITE);
        playAgainLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playAgainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainPanel.add(playAgainLabel);

        // Button panel for "Play again?"
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.PINK);
        playAgainPanel.add(buttonPanel);

        // Yes button
        JButton yesButton = new JButton("YES");
        yesButton.setBackground(Color.WHITE);
        yesButton.setForeground(Color.BLACK);
        yesButton.setFont(new Font("Arial", Font.BOLD, 15));
        yesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(yesButton);

        // No button
        JButton noButton = new JButton("NO");
        noButton.setBackground(Color.WHITE);
        noButton.setForeground(Color.BLACK);
        noButton.setFont(new Font("Arial", Font.BOLD, 15));
        noButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(noButton);

        // Refresh frame
        frame.repaint();
        frame.revalidate();

        // YES LOGIC
        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reset score and remaining time
                score = 0;
                remainingTime = 13;

                // Remove components from main
                mainPanel.removeAll();

                // Refresh frame
                frame.repaint();
                frame.revalidate();

                // Restart game
                startGame();
            }
        });

        // NO LOGIC
        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // Increase score per spacebar press
    @Override
    public void keyPressed(KeyEvent e) {
        if (remainingTime > 0 && remainingTime < 11) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                score++;
            }
        }
    }

    // I had to "implement" these
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        new Game();
    }
}
