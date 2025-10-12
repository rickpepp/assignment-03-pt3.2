package it.unibo.agar.view;

import it.unibo.agar.model.GameStateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

public class GlobalView extends JFrame {

    private final GamePanel gamePanel;

    public GlobalView(GameStateManager gameStateManager, String playerName) {
        setTitle("Agar.io - Global View (Java)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Or DISPOSE_ON_CLOSE if multiple windows
        setPreferredSize(new Dimension(800, 800));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    gameStateManager.removePlayer(playerName);
                    System.out.println("Removed player");
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        this.gamePanel = new GamePanel(gameStateManager);
        add(this.gamePanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void repaintView() {
        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }
}
