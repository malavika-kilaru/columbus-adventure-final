package application;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

/**
 * GameWebServer - Christopher Columbus Adventure Backend
 */
public class GameWebServer {

    private static final int PORT = 8000;
    private static Map<String, GameSession> gameSessions = new HashMap<>();
    private static int sessionCounter = 0;

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 50);

            server.createContext("/api/start", GameWebServer::handleStart);
            server.createContext("/api/move", GameWebServer::handleMove);
            server.createContext("/api/state", GameWebServer::handleState);
            server.createContext("/", GameWebServer::handleRoot);

            server.setExecutor(null);
            server.start();

            printStartupBanner();

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printStartupBanner() {
        System.out.println("=== Christopher Columbus Adventure Backend ===");
        System.out.println("Server running at http://localhost:8000");
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
        String response = "Christopher Columbus Adventure Backend v2.0";
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        addCORSHeaders(exchange);
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private static void handleStart(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            String difficulty = "EASY";

            if (query != null && query.contains("difficulty=")) {
                difficulty = query.split("=")[1].toUpperCase();
            }

            String sessionId = "session_" + (++sessionCounter);
            GameSession session = new GameSession(sessionId, difficulty);
            gameSessions.put(sessionId, session);

            String response = "{\"sessionId\":\"" + sessionId + "\",\"difficulty\":\"" + difficulty +
                    "\",\"level\":1,\"status\":\"CREATED\"}";
            sendJSON(exchange, response);

        } catch (Exception e) {
            sendJSON(exchange, "{\"error\":\"Failed to start game\"}");
        }
    }

    private static void handleMove(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            String sessionId = "";
            String direction = "";

            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("session=")) sessionId = param.substring(8);
                    if (param.startsWith("direction=")) direction = param.substring(10);
                }
            }

            GameSession session = gameSessions.get(sessionId);
            if (session != null) session.moveShip(direction);

            sendJSON(exchange, "{\"success\":true}");

        } catch (Exception e) {
            sendJSON(exchange, "{\"error\":\"Move failed\"}");
        }
    }

    private static void handleState(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            String sessionId = "";

            if (query != null && query.contains("session=")) {
                sessionId = query.split("=")[1];
            }

            GameSession session = gameSessions.get(sessionId);
            String response = (session != null)
                    ? session.getGameStateJSON()
                    : "{\"error\":\"Session not found\"}";

            sendJSON(exchange, response);

        } catch (Exception e) {
            sendJSON(exchange, "{\"error\":\"State failed\"}");
        }
    }

    private static void sendJSON(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        addCORSHeaders(exchange);
        exchange.sendResponseHeaders(200, json.length());
        exchange.getResponseBody().write(json.getBytes());
        exchange.close();
    }

    private static void addCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
}


/**
 * =====================
 *  GAME SESSION CLASS
 * =====================
 */
class GameSession {

    private final String sessionId;
    private final String difficulty;
    private final Ship ship;
    private final OceanMap oceanMap;
    private final Treasure treasure;
    private final GameControllerV2 controller;

    private final List<PirateShip> pirates = new ArrayList<>();
    private final List<SeaMonster> monsters = new ArrayList<>();
    private int moveCount = 0;

    public GameSession(String sessionId, String difficulty) {
        this.sessionId = sessionId;
        this.difficulty = difficulty;

        this.oceanMap = OceanMap.getInstance(20);
        oceanMap.resetMap();
        oceanMap.placeIslands(getIslandCount());

        this.ship = new Ship(1, 1, 20);
        oceanMap.setShip(ship);

        this.treasure = new Treasure(20, oceanMap);

        this.controller = new GameControllerV2(ship, treasure, oceanMap, difficulty);

        createPirates();
        createMonsters();
    }

    private int getIslandCount() {
        switch (difficulty) {
            case "MEDIUM": return 12;
            case "HARD": return 14;
            case "SURVIVAL": return 14;
            default: return 10;
        }
    }

    private void createPirates() {
        int count = controller.getPirateCount();

        for (int i = 0; i < count; i++) {
            PirateShipFactory factory =
                    (i % 2 == 0) ? new ChasePirateShipFactory() : new PatrolPirateShipFactory();

            PirateShip pirate = factory.createPirateShip(
                    3 + (i * 3),
                    3 + (i * 3),
                    20,
                    oceanMap
            );

            ship.attach(pirate);
            controller.addPirate(pirate);
            pirates.add(pirate);
        }
    }

    private void createMonsters() {
        int count = controller.getMonsterCount();
        int tx = (int) treasure.getLocation().getX();
        int ty = (int) treasure.getLocation().getY();

        for (int i = 0; i < count; i++) {
            int mx = Math.min(19, Math.max(0, tx - 3 + i));
            int my = Math.min(19, Math.max(0, ty - 3 + i));

            SeaMonster m = new SeaMonster(mx, my, 20, oceanMap, "Monster_" + (i+1));
            controller.addSeaMonster(m);
            monsters.add(m);
        }
    }

    public void moveShip(String direction) {
        switch (direction.toLowerCase()) {
            case "up": case "north": ship.goNorth(oceanMap); break;
            case "down": case "south": ship.goSouth(oceanMap); break;
            case "left": case "west": ship.goWest(oceanMap); break;
            case "right": case "east": ship.goEast(oceanMap); break;
        }

        moveCount++;
        controller.update();
    }

    /**
     *  FIXED GRID ORIENTATION HERE
     */
    private String[][] generateGrid() {
        int size = 20;
        String[][] grid = new String[size][size];

        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                grid[r][c] = "";

        // ISLANDS
        boolean[][] map = oceanMap.getMap();
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                if (map[x][y]) grid[y][x] = "W";  

        // TREASURE
        int tx = (int) treasure.getLocation().getX();
        int ty = (int) treasure.getLocation().getY();
        grid[ty][tx] = "T";   

        // SHIP
        int sx = (int) ship.getShipLocation().getX();
        int sy = (int) ship.getShipLocation().getY();
        grid[sy][sx] = "S";   

        // PIRATES
        for (PirateShip p : pirates) {
            int px = (int) p.getLocation().getX();
            int py = (int) p.getLocation().getY();
            if (!grid[py][px].equals("S")) grid[py][px] = "P";   
        }

        // MONSTERS
        for (SeaMonster m : monsters) {
            int mx = (int) m.getLocation().getX();
            int my = (int) m.getLocation().getY();
            if (!grid[my][mx].equals("S") &&
                !grid[my][mx].equals("P"))
                grid[my][mx] = "M";  // FIXED
        }

        return grid;
    }

    public String getGameStateJSON() {
        String[][] grid = generateGrid();
        Point shipLoc = ship.getShipLocation();
        Point tLoc = treasure.getLocation();

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"grid\":").append(arrayToJSON(grid)).append(",");
        sb.append("\"sessionId\":\"").append(sessionId).append("\",");
        sb.append("\"shipX\":").append((int)shipLoc.getX()).append(",");
        sb.append("\"shipY\":").append((int)shipLoc.getY()).append(",");
        sb.append("\"treasureX\":").append((int)tLoc.getX()).append(",");
        sb.append("\"treasureY\":").append((int)tLoc.getY()).append(",");
        sb.append("\"score\":").append(controller.getScore()).append(",");
        sb.append("\"lives\":").append(controller.getLives()).append(",");
        sb.append("\"status\":\"").append(controller.getCurrentState()).append("\",");
        sb.append("\"pirates\":").append(pirates.size()).append(",");
        sb.append("\"monsters\":").append(monsters.size()).append(",");
        sb.append("\"moves\":").append(moveCount).append(",");
        sb.append("\"difficulty\":\"").append(difficulty).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String arrayToJSON(String[][] g) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < g.length; i++) {
            sb.append("[");
            for (int j = 0; j < g[i].length; j++) {
                sb.append("\"").append(g[i][j]).append("\"");
                if (j < g[i].length - 1) sb.append(",");
            }
            sb.append("]");
            if (i < g.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}