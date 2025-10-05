package it.unibo.agar.view;

import it.unibo.agar.model.GameStateManager;
import it.unibo.agar.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.rmi.RemoteException;
import java.util.Optional;

public class LocalView extends JFrame {
    private static final double SENSITIVITY = 2;
    private final GamePanel gamePanel;
    private final GameStateManager gameStateManager;
    private final String playerId;

    public LocalView(GameStateManager gameStateManager, String playerId) {
        this.gameStateManager = gameStateManager;
        this.playerId = playerId;

        setTitle("Agar.io - Local View (" + playerId + ") (Java)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose only this window
        setPreferredSize(new Dimension(600, 600));

        this.gamePanel = new GamePanel(gameStateManager, playerId);
        add(this.gamePanel, BorderLayout.CENTER);

        setupMouseControls();

        pack();
        setLocationRelativeTo(null); // Center on screen
    }

    private void setupMouseControls() {
        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Optional<Player> playerOpt = null;
                try {
                    playerOpt = gameStateManager.getWorld().getPlayerById(playerId);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                if (playerOpt.isPresent()) {
                Point mousePos = e.getPoint();
                // Player is always in the center of the local view
                double viewCenterX = gamePanel.getWidth() / 2.0;
                double viewCenterY = gamePanel.getHeight() / 2.0;

                double dx = mousePos.x - viewCenterX;
                double dy = mousePos.y - viewCenterY;

                // Normalize the direction vector
                double magnitude = Math.hypot(dx, dy);
                if (magnitude > 0) { // Avoid division by zero if mouse is exactly at center
                    try {
                        gameStateManager.setPlayerDirection(playerId, (dx / magnitude) * SENSITIVITY, (dy / magnitude) * SENSITIVITY);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    try {
                        gameStateManager.setPlayerDirection(playerId, 0, 0); // Stop if mouse is at center
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                // Repainting is handled by the main game loop timer
            }
            }
        });
    }

    public void repaintView() {
        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }
}
