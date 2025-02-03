import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class NumberGuessingGame extends JFrame {
    private int targetNumber;
    private int attempts;
    private int maxAttempts = 5;
    private int roundsPlayed;
    private int roundsWon;
    private boolean gameActive;

    private JLabel titleLabel;
    private JLabel statusLabel;
    private JTextField guessField;
    private JButton submitButton;
    private JLabel statsLabel;
    private JButton startButton;
    private JButton exitButton;
    private Random random;

    public NumberGuessingGame() {
        random = new Random();
        setupUI();
        initializeGame();
    }

    private void setupUI() {
        setTitle("Number Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Set background color for the main content pane
        getContentPane().setBackground(new Color(173, 216, 230)); // Light Blue

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(173, 216, 230)); // Light Blue
        titleLabel = new JLabel("Number Guessing Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK); // Set text color to black for better contrast
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Game Panel
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gamePanel.setBackground(new Color(173, 216, 230)); // Light Blue

        // Status Label
        statusLabel = new JLabel("Press Start to begin!");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.BLACK); // Set text color to black for better contrast
        gamePanel.add(statusLabel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Guess Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(173, 216, 230)); // Light Blue
        guessField = new JTextField(10);
        guessField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(guessField);
        gamePanel.add(inputPanel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Submit Button
        submitButton = new JButton("Submit Guess");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(submitButton);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Stats Label
        statsLabel = new JLabel("Rounds Played: 0 | Rounds Won: 0");
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsLabel.setForeground(Color.BLACK); // Set text color to black for better contrast
        gamePanel.add(statsLabel);

        add(gamePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(new Color(173, 216, 230)); // Light Blue

        startButton = new JButton("Start Game");
        startButton.setBackground(new Color(76, 175, 80));
        startButton.setForeground(Color.WHITE);
        buttonPanel.add(startButton);

        exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(244, 67, 54));
        exitButton.setForeground(Color.WHITE);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add Action Listeners
        startButton.addActionListener(e -> startGame());
        submitButton.addActionListener(e -> checkGuess());
        exitButton.addActionListener(e -> confirmExit());
        guessField.addActionListener(e -> checkGuess());

        // Set window size and center it
        setSize(400, 500);
        setLocationRelativeTo(null);
    }

    private void initializeGame() {
        gameActive = false;
        attempts = 0;
        roundsPlayed = 0;
        roundsWon = 0;
        updateStats();
    }

    private void startGame() {
        gameActive = true;
        attempts = 0;
        targetNumber = random.nextInt(100) + 1;
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setText("<html><div style='text-align: center;'>"
                + "I'm thinking of a number between 1 and 100<br>"
                + "You have " + maxAttempts + " attempts remaining!"
                + "</div></html>");
        guessField.setEnabled(true);
        submitButton.setEnabled(true);
        startButton.setEnabled(false);
        guessField.setText("");
        guessField.requestFocus();
    }

    private void checkGuess() {
        if (!gameActive)
            return;

        try {
            int guess = Integer.parseInt(guessField.getText());
            if (guess < 1 || guess > 100) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a number between 1 and 100!",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            attempts++;
            int remainingAttempts = maxAttempts - attempts;

            if (guess == targetNumber) {
                roundsWon++;
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You got it in " + attempts + " attempts!",
                        "Winner!",
                        JOptionPane.INFORMATION_MESSAGE);
                endRound();
            } else if (remainingAttempts == 0) {
                JOptionPane.showMessageDialog(this,
                        "Sorry! The number was " + targetNumber,
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
                endRound();
            } else {
                String hint = (guess > targetNumber) ? "Too high!" : "Too low!";
                statusLabel.setText(hint + "\nYou have " + remainingAttempts + " attempts remaining!");
            }

            guessField.setText("");
            updateStats();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number!",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void endRound() {
        roundsPlayed++;
        gameActive = false;
        guessField.setEnabled(false);
        submitButton.setEnabled(false);
        startButton.setEnabled(true);
        statusLabel.setText("Press Start to play again!");
        updateStats();
    }

    private void updateStats() {
        double winRate = (roundsPlayed > 0) ? (double) roundsWon / roundsPlayed * 100 : 0;
        statsLabel.setText(String.format("Rounds Played: %d | Rounds Won: %d | Win Rate: %.1f%%",
                roundsPlayed, roundsWon, winRate));
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Do you want to exit the game?",
                "Confirm Exit",
                JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NumberGuessingGame game = new NumberGuessingGame();
            game.setVisible(true);
        });
    }
}