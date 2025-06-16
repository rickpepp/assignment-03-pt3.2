# PCD Assignment - Optional Exercise with Java RMI

In this repository there is a centralized version of the game Agar.IO implemented in Java. 
The game currently works with a local architecture where the view, game logic, and player interactions all exist within the same JVM.

This assignment focuses on distributing the application using Java RMI (Remote Method Invocation) with the following objectives:

a) Create a distributed version where the LocalView can be created remotely, allowing players to connect from different machines 

b) Update the GameStateManager to support remote player joining, enabling dynamic multiplayer functionality across the network