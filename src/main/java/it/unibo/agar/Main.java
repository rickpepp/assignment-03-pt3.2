package it.unibo.agar;

import it.unibo.agar.model.*;
import it.unibo.agar.view.GlobalView;
import it.unibo.agar.view.LocalView;
import it.unibo.agar.view.StartScreen;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static final long GAME_TICK_MS = 30; // Corresponds to ~33 FPS

    public static void main(String[] args) {
        StartScreen.showAndWait();
    }

    public static void startGame(String[] args) throws RemoteException, NotBoundException {
        String playerName = args[0];
        String hostAddress = args[1];

        final Registry registry = LocateRegistry.getRegistry(hostAddress);
        final GameStateManager gameManager = (GameStateManager) registry.lookup("GameManager");

        gameManager.addPlayer(playerName);

        // List to keep track of active views for repainting
        final List<JFrameRepaintable> views = new ArrayList<>();

        SwingUtilities.invokeLater(() -> {
            GlobalView globalView = new GlobalView(gameManager, playerName);
            views.add(globalView::repaintView); // Add repaint method reference
            globalView.setVisible(true);

            LocalView localViewP1 = new LocalView(gameManager, playerName);
            views.add(localViewP1::repaintView);
            localViewP1.setVisible(true);
        });

        final Timer timer = new Timer(true); // Use daemon thread for timer
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    for (JFrameRepaintable view : views) {
                        view.repaintView();
                    }
                });
            }
        }, 0, GAME_TICK_MS);
    }        

    // Functional interface for repaintable views
    @FunctionalInterface
    interface JFrameRepaintable {
        void repaintView();
    }
}
