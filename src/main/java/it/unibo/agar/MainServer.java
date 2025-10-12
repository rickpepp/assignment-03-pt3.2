package it.unibo.agar;

import it.unibo.agar.model.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainServer {

    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 1000;
    private static final int NUM_FOODS = 100;
    private static final long GAME_TICK_MS = 30;

    public static void main(String[] args) throws RemoteException {
        final List<Food> initialFoods = GameInitializer.initialFoods(NUM_FOODS, WORLD_WIDTH, WORLD_HEIGHT);
        final World initialWorld = new World(WORLD_WIDTH, WORLD_HEIGHT, new LinkedList<>(), initialFoods);
        final GameStateManager gameManager = new RmiGameStateManager(initialWorld);
        final GameStateManager stub = (GameStateManager) UnicastRemoteObject.exportObject((GameStateManager) gameManager, 0);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("GameManager", stub);

        final Timer timer = new Timer(true); // Use daemon thread for timer
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    gameManager.tick();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, GAME_TICK_MS);
    }
}
