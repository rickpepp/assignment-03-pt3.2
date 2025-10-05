package it.unibo.agar;

import it.unibo.agar.model.*;
import it.unibo.agar.view.GlobalView;
import it.unibo.agar.view.LocalView;

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

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final Registry registry = LocateRegistry.getRegistry();
        final GameStateManager gameManager = (GameStateManager) registry.lookup("GameManager");

        // List to keep track of active views for repainting
        final List<JFrameRepaintable> views = new ArrayList<>();

        SwingUtilities.invokeLater(() -> {
            GlobalView globalView = new GlobalView(gameManager);
            views.add(globalView::repaintView); // Add repaint method reference
            globalView.setVisible(true);

            LocalView localViewP1 = new LocalView(gameManager, "p1");
            views.add(localViewP1::repaintView);
            localViewP1.setVisible(true);

            LocalView localViewP2 = new LocalView(gameManager, "p2");
            views.add(localViewP2::repaintView);
            localViewP2.setVisible(true);
        });

        final Timer timer = new Timer(true); // Use daemon thread for timer
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // AI movement for p1, p3, p4
                try {
                    AIMovement.moveAI("p1", gameManager);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                try {
                    AIMovement.moveAI("p3", gameManager); // Assuming p3 is AI
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                try {
                    AIMovement.moveAI("p4", gameManager); // Assuming p4 is AI
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

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
