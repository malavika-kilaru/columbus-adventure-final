Christopher Columbus Adventure - Final Project

Team Members:-

Malavika Kilaru

Design Pattern(Observer,Strategy)
Test Cases
Front-end

Sailee Shingare

Design Patterns(Factory, Singleton,Composite)
UML diagrams
Front-end


Project Overview

Christopher Columbus Adventure is a tactical ocean navigation game developed as a final project for CSCI 513 Software Engineering course. The game challenges players to navigate Columbus's ship across a 20x20 ocean grid to find treasure while avoiding intelligent pirate ships and sea monsters. 

The project demonstrates enterprise-level software engineering practices including client-server architecture, multiple design patterns, comprehensive unit testing, and clean code organization.



Game Concept

Players control a ship (Columbus) that must navigate through an ocean filled with obstacles and enemies to reach the treasure. The game requires strategic thinking, careful navigation, and collision avoidance. Different difficulty levels provide varying challenges with different numbers of pirates, monsters, and obstacles.



 Project Structure

The project is organized into two main components:

Backend (Java): Contains all game logic
- Game controller and state management
- Map and entity system
- Pirate AI with multiple strategies
- Sea monster system
- Ship upgrade system
- Complete JUnit test coverage

Frontend (React): User interface and visualization
- Game grid display
- Player controls
- Real-time game updates
- Score and lives tracking


Technologies & Tools Used

Backend Development:
- Java 

Testing:
- JUnit 4.13.2 for unit testing
- Hamcrest Core 1.3 for advanced assertions

Frontend:
- React 
- JavaScript/TypeScript 


 Design Patterns 

1. Observer Pattern

2. Strategy Pattern 

3. Factory Pattern

4. Decorator Pattern

5. Composite Pattern

6. Singleton Pattern


Testing Approach

The project includes comprehensive unit testing to validate core functionality:

Test Class 1: ShipTest (8 Tests)
Tests validate that the player ship moves correctly in all directions and respects boundary conditions. Tests ensure the ship cannot move off the grid edges and that movement calculations are accurate.

Test Class 2: GameControllerTest (12 Tests)
Tests validate game mechanics including difficulty level configuration, enemy spawning, collision detection, scoring calculations, and game state transitions. Tests verify the complete game lifecycle from start to game-over conditions.

Testing Results: All 20 tests pass successfully, indicating core functionality is working correctly and reliably.

Test Coverage Areas:
- Player movement in all directions
- Grid boundary enforcement
- Difficulty level validation
- Enemy count per difficulty
- Collision mechanics and responses
- Score calculation accuracy
- Game state transitions
- Lives management


Requirements Met

The project successfully fulfills all course requirements:

Larger Grid: The game world has been expanded to a 20x20 grid, providing more room for strategic navigation compared to the original 10x10 grid.

Treasure Search: Players must actively search for treasure, which is randomly placed each game, requiring them to explore the entire map.

Win/Lose Notification: The game clearly communicates victory when treasure is found and game-over when lives are exhausted.

Sea Monsters: Dynamic sea monsters swim around patrol areas, activating when the player approaches the treasure, increasing difficulty as the game progresses.

Improved Pirate Algorithms: Pirates use intelligent strategies including distance-based pursuit, random patrol, and pure random movement, creating varied and interesting challenges.

Multiple Strategies: The Strategy pattern provides three different movement algorithms that can be mixed and matched for different game difficulties.

JUnit Tests: Comprehensive test coverage with 20 tests across 2 test classes, achieving 100% pass rate.

Design Patterns: Five distinct design patterns (Observer, Strategy, Factory, Decorator, Composite) are properly implemented throughout the codebase.

Code Quality: Clean, well-organized code with clear separation of concerns and appropriate documentation.


How to Play

1. Launch the application and select a difficulty level
2. View the ocean grid with your ship starting at position (1,1)
3. Use movement commands to navigate around islands and avoid enemies
4. Monitor your score - it increases with each move
5. Watch your lives count - collision with enemies costs lives
6. Strategically navigate toward the treasure location
7. Reach the treasure before losing all lives to win the game
8. Upon victory, earn bonus points and advance to the next level

Game Flow :

                    START GAME
                          ↓
           Select Difficulty (Easy, Medium, Hard, Survival)
                          ↓
                  Create 20x20 Ocean Map
                          ↓
                   Place Islands & Obstacles
                          ↓
            Spawn Ship, Treasure, Pirates, Monsters
                          ↓
              GAME READY - START PLAYING
                          ↓
Game Loop Flow:-


            Player Presses Arrow Keys (↑ ↓ ← →)
                          ↓
              Ship Moves in Pressed Direction
                          ↓
           Pirates Notified (Observer Pattern)
                          ↓
         Pirates Move Using Strategy (Chase/Patrol)
                          ↓
         Check: Is Ship within 5 grids of Treasure?
                    ↙               ↘
                  NO                YES
                  ↓                 ↓
          Monsters Patrol      Monsters Chase
                  ↘               ↙
                    ↓
         Check for Collision with Pirates/Monsters
                          ↓
                      Collision?
                    ↙               ↘
                  NO                YES
                  ↓                 ↓
              +10 Points       -1 Life / Reset Ship
                    ↘               ↙
                    ↓
            Did Ship Reach Treasure?
                          ↓
                    Lives > 0?
                    ↙               ↘
                  NO                YES
                  ↓                 ↓
              GAME OVER         Continue Loop
                                   ↓
                          (Repeat from Player Input)


Summary

This project successfully demonstrates advanced software engineering principles through a fully functional game that applies five design patterns appropriately and naturally. The use of Observer, Strategy, Factory, Decorator, and Composite patterns shows a sophisticated understanding of object-oriented design. The comprehensive test coverage ensures reliability while the clean architecture makes the codebase maintainable and extensible. The combination of intelligent pirate AI, dynamic sea monsters, and varied difficulty levels creates an engaging game experience while serving as an excellent educational example of enterprise software development practices.