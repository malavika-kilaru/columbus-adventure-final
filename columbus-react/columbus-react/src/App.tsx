import React, { useState, useEffect, useCallback } from 'react';
import './App.css';

interface GameState {
  grid: string[][];
  status: string;
  score: number;
  shipX: number;
  shipY: number;
  treasureX: number;
  treasureY: number;
  lives: number;
  pirates: number;
  monsters: number;
  difficulty: string;
  shipDescription: string;
  moves: number;
  error?: string;
}

type Difficulty = 'EASY' | 'MEDIUM' | 'HARD' | 'SURVIVAL';

const App: React.FC = () => {
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [gameState, setGameState] = useState<GameState | null>(null);
  const [currentLevel, setCurrentLevel] = useState<number>(1);
  const [difficulty, setDifficulty] = useState<Difficulty>('EASY');
  const [isGameRunning, setIsGameRunning] = useState(false);
  const [gameOver, setGameOver] = useState(false);
  const [gameOverMessage, setGameOverMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [levelCleared, setLevelCleared] = useState(false);
  const [totalScore, setTotalScore] = useState(0);

  const levelProgression: Difficulty[] = ['EASY', 'MEDIUM', 'HARD', 'SURVIVAL'];
  const BACKEND_URL = 'http://localhost:8000';

  const updateGame = useCallback(async () => {
    if (!sessionId) return;

    try {
      const response = await fetch(`${BACKEND_URL}/api/state?session=${sessionId}`);
      
      if (!response.ok) {
        console.error('API error:', response.status);
        return;
      }

      const data: GameState = await response.json();

      if (data.error) {
        console.error('Game error:', data.error);
        return;
      }

      setGameState(data);

      // Check win condition
      if (data.status === 'WIN') {
        setIsGameRunning(false);
        setLevelCleared(true);
        const newTotal = totalScore + data.score;
        setTotalScore(newTotal);
        
        if (currentLevel < levelProgression.length) {
          setGameOverMessage(
            ` LEVEL ${currentLevel} COMPLETE!\n\nTreasure Found!\nLevel Score: ${data.score}\n\nGet ready for Level ${currentLevel + 1}: ${levelProgression[currentLevel]}`
          );
        } else {
          setGameOverMessage(
            ` YOU WIN THE ENTIRE GAME!\n\nAll 4 Levels Complete!\nTotal Score: ${newTotal}\n\nAmazing Adventure! 🚢⚓`
          );
        }
        setGameOver(true);
      } 
      // Check lose condition (all lives lost)
      else if (data.status === 'LOSE') {
        setIsGameRunning(false);
        setGameOver(true);
        setGameOverMessage(
          ` GAME OVER!\n\nAll Lives Lost at Level ${currentLevel}!\n\nRestart Level ${currentLevel}?`
        );
        setLevelCleared(false);
      }
    } catch (err) {
      console.error('Error updating game:', err);
    }
  }, [sessionId, currentLevel, levelProgression.length, totalScore]);

  const startGame = async (selectedDifficulty: Difficulty) => {
    setLoading(true);
    setError(null);
    setDifficulty(selectedDifficulty);
    setGameOver(false);
    setLevelCleared(false);
    setGameOverMessage('');

    try {
      console.log('Starting game:', selectedDifficulty);
      const response = await fetch(`${BACKEND_URL}/api/start?difficulty=${selectedDifficulty}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      if (data.error) {
        setError(data.error);
        setLoading(false);
        return;
      }

      setSessionId(data.sessionId);
      setIsGameRunning(true);
      setGameState(null);
      setLoading(false);
    } catch (err) {
      console.error('Error starting game:', err);
      setError('Failed to start game. Make sure backend is running on port 8000');
      setLoading(false);
    }
  };

  const startNextLevel = async () => {
    if (currentLevel < levelProgression.length) {
      const nextDifficulty = levelProgression[currentLevel];
      setCurrentLevel(currentLevel + 1);
      setGameOver(false);
      setLevelCleared(false);
      await startGame(nextDifficulty);
    }
  };

  const moveShip = async (direction: 'north' | 'south' | 'east' | 'west') => {
    if (!sessionId || !isGameRunning) return;

    try {
      await fetch(`${BACKEND_URL}/api/move?session=${sessionId}&direction=${direction}`);
      await updateGame();
    } catch (err) {
      console.error('Error moving ship:', err);
    }
  };

  useEffect(() => {
    if (!isGameRunning) return;

    const interval = setInterval(updateGame, 500);
    return () => clearInterval(interval);
  }, [isGameRunning, updateGame]);

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (!isGameRunning) return;

      switch (event.key) {
        case 'ArrowUp':
          event.preventDefault();
          moveShip('north');
          break;
        case 'ArrowDown':
          event.preventDefault();
          moveShip('south');
          break;
        case 'ArrowLeft':
          event.preventDefault();
          moveShip('west');
          break;
        case 'ArrowRight':
          event.preventDefault();
          moveShip('east');
          break;
        default:
          break;
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isGameRunning, moveShip]);

  const renderGrid = () => {
    if (!gameState) return null;

    return (
      <div className="grid">
        {gameState.grid.map((row, rowIndex) =>
          row.map((cell, colIndex) => {
            let emoji = '';
            let cellClass = 'cell';

            switch (cell) {
              case 'S':
                emoji = '🚢';
                cellClass += ' ship';
                break;
              case 'T':
                emoji = '💰';
                cellClass += ' treasure';
                break;
              case 'W':
                emoji = '🏝️';
                cellClass += ' island';
                break;
              case 'P':
                emoji = '🏴';
                cellClass += ' pirate';
                break;
              case 'M':
                emoji = '🐙';
                cellClass += ' monster';
                break;
              default:
                emoji = '';
            }

            return (
              <div key={`${rowIndex}-${colIndex}`} className={cellClass}>
                {emoji}
              </div>
            );
          })
        )}
      </div>
    );
  };

  // MENU SCREEN
  if (!isGameRunning && !levelCleared) {
    return (
      <div className="page-container">
        <div className="container">
          <h1>⚓ Christopher Columbus Adventure ⚓</h1>
          <p className="subtitle">Find the Treasure & Escape the Pirates!</p>

          <div className="concept-box">
            <h3> Game Concept:</h3>
            <p>
               Navigate your ship through the ocean grid<br/>
               Find the hidden treasure before time runs out<br/>
               Avoid pirate ships that chase from 2-3 steps away<br/>
               Escape sea monsters that guard the treasure<br/>
              Progress through 4 difficulty levels<br/>
            </p>
          </div>

          {error && (
            <div className="error-message">
               {error}
            </div>
          )}

          <div className="menu">
            <button
              className="btn btn-easy"
              onClick={() => {
                setCurrentLevel(1);
                setTotalScore(0);
                startGame('EASY');
              }}
              disabled={loading}
            >
              {loading ? ' Loading...' : ' LEVEL 1'}<br/>
              <span className="btn-desc">EASY - 5 Lives, 10 Islands</span>
            </button>
            <button
              className="btn btn-medium"
              onClick={() => {
                setCurrentLevel(2);
                setTotalScore(0);
                startGame('MEDIUM');
              }}
              disabled={loading}
            >
              {loading ? ' Loading...' : 'LEVEL 2'}<br/>
              <span className="btn-desc">MEDIUM - 4 Lives, 12 Islands</span>
            </button>
            <button
              className="btn btn-hard"
              onClick={() => {
                setCurrentLevel(3);
                setTotalScore(0);
                startGame('HARD');
              }}
              disabled={loading}
            >
              {loading ? ' Loading...' : ' LEVEL 3'}<br/>
              <span className="btn-desc">HARD - 3 Lives, 14 Islands</span>
            </button>
            <button
              className="btn btn-survival"
              onClick={() => {
                setCurrentLevel(4);
                setTotalScore(0);
                startGame('SURVIVAL');
              }}
              disabled={loading}
            >
              {loading ? ' Loading...' : ' LEVEL 4'}<br/>
              <span className="btn-desc">SURVIVAL - 2 Lives, 14 Islands</span>
            </button>
          </div>

          {gameOver && (
            <div className="game-over-modal">
              <div className="modal-content">
                <p>{gameOverMessage}</p>
                <button
                  className="btn btn-restart"
                  onClick={() => setGameOver(false)}
                >
                  Try Again
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    );
  }

  // GAME SCREEN
  return (
    <div className="page-container">
      <div className="container game-container">
        <h1>⚓ Level {currentLevel} - {difficulty} ⚓</h1>

        <div className="level-info">
          <span className="level-badge"> LEVEL {currentLevel}/4</span>
          <span className="difficulty-badge">{difficulty}</span> | 
          Lives: <span className="lives-badge"> {gameState?.lives || 0}</span> |
          Score: <span className="score-badge"> {totalScore + (gameState?.score || 0)}</span>
        </div>

        <div className="info-bar">
          <div className="info-item">
            <span className="stat-label"> Level Score:</span>
            <span className="stat-value">{gameState?.score || 0}</span>
          </div>
          <div className="info-item">
            <span className="stat-label"> Ship Position:</span>
            <span className="stat-value">
              ({gameState?.shipX || 0},{gameState?.shipY || 0})
            </span>
          </div>
          <div className="info-item">
            <span className="stat-label">Treasure Location:</span>
            <span className="stat-value">
              ({gameState?.treasureX || 0},{gameState?.treasureY || 0})
            </span>
          </div>
          <div className="info-item">
            <span className="stat-label"> Pirates:</span>
            <span className="stat-value">{gameState?.pirates || 0}</span>
          </div>
          <div className="info-item">
            <span className="stat-label"> Monsters:</span>
            <span className="stat-value">{gameState?.monsters || 0}</span>
          </div>
          <div className="info-item">
            <span className="stat-label"> Moves:</span>
            <span className="stat-value">{gameState?.moves || 0}</span>
          </div>
        </div>

        {renderGrid()}

        <div className="legend">
          <div className="legend-item">🚢 = Your Ship (Columbus)</div>
          <div className="legend-item">💰 = Treasure (Goal!)</div>
          <div className="legend-item">🏝️ = Island (Cannot sail)</div>
          <div className="legend-item">🏴 = Pirate Ship (Chase from 2-4 steps)</div>
          <div className="legend-item">🐙 = Sea Monster (Awakens near treasure)</div>
        </div>

        <div className="controls">
          <div></div>
          <button
            className="arrow-btn"
            onClick={() => moveShip('north')}
            title="Move North (UP)"
          >
            
          </button>
          <div></div>
          <button
            className="arrow-btn"
            onClick={() => moveShip('west')}
            title="Move West (LEFT)"
          >
            
          </button>
          <button
            className="arrow-btn"
            onClick={() => moveShip('south')}
            title="Move South (DOWN)"
          >
            
          </button>
          <button
            className="arrow-btn"
            onClick={() => moveShip('east')}
            title="Move East (RIGHT)"
          >
            
          </button>
        </div>

        <p className="controls-text">⌨️ Use Arrow Keys or Click Buttons</p>

        <button
          className="btn btn-restart"
          onClick={() => {
            setIsGameRunning(false);
            setSessionId(null);
            setGameState(null);
            setCurrentLevel(1);
            setTotalScore(0);
          }}
        >
           Back to Menu
        </button>

        {gameOver && (
          <div className="game-over-modal">
            <div className="modal-content">
              <p>{gameOverMessage}</p>
              {levelCleared && currentLevel < levelProgression.length ? (
                <>
                  <button
                    className="btn btn-easy"
                    onClick={startNextLevel}
                  >
                    Next Level
                  </button>
                  <button
                    className="btn btn-restart"
                    onClick={() => {
                      setGameOver(false);
                      setIsGameRunning(false);
                      setSessionId(null);
                      setGameState(null);
                      setCurrentLevel(1);
                      setTotalScore(0);
                    }}
                  >
                    Back to Menu
                  </button>
                </>
              ) : (
                <>
                  <button
                    className="btn btn-restart"
                    onClick={() => {
                      // Restart current level
                      setGameOver(false);
                      const currentDifficulty = levelProgression[currentLevel - 1];
                      startGame(currentDifficulty as Difficulty);
                    }}
                  >
                     Restart Level {currentLevel}
                  </button>
                  <button
                    className="btn btn-restart"
                    onClick={() => {
                      // Back to main menu
                      setGameOver(false);
                      setIsGameRunning(false);
                      setSessionId(null);
                      setGameState(null);
                      setCurrentLevel(1);
                      setTotalScore(0);
                    }}
                  >
                    Back to Menu
                  </button>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default App;