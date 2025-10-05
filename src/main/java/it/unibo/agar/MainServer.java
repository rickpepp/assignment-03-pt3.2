package it.unibo.agar;

import it.unibo.agar.model.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class MainServer {

    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 1000;
    private static final int NUM_PLAYERS = 4; // p1, p2, p3, p4
    private static final int NUM_FOODS = 100;

    public static void main(String[] args) throws RemoteException {
        final List<Player> initialPlayers = GameInitializer.initialPlayers(NUM_PLAYERS, WORLD_WIDTH, WORLD_HEIGHT);
        final List<Food> initialFoods = GameInitializer.initialFoods(NUM_FOODS, WORLD_WIDTH, WORLD_HEIGHT);
        final World initialWorld = new World(WORLD_WIDTH, WORLD_HEIGHT, initialPlayers, initialFoods);
        final GameStateManager gameManager = new RmiGameStateManager(initialWorld);
        final GameStateManager stub = (GameStateManager) UnicastRemoteObject.exportObject((GameStateManager) gameManager, 0);
        final Registry registry = LocateRegistry.getRegistry(1099);
        registry.rebind("GameManager", stub);
    }
}
