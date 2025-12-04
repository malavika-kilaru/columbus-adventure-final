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

  // Level progression
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

      if (data.status === 'WIN') {
        setIsGameRunning(false);
        setLevelCleared(true);
        setTotalScore(totalScore + data.score);
        
        if (currentLevel < levelProgression.length) {
          setGameOverMessage(
            ` LEVEL ${currentLevel} COMPLETE!\n\nLevel Score: ${data.score}\n\nGet ready for Level ${currentLevel + 1}!`
          );
        } else {
          setGameOverMessage(
            ` YOU WIN THE ENTIRE GAME!\n\nTotal Score: ${totalScore + data.score}\n\nAmazing job! `
          );
        }
        setGameOver(true);
      } else if (data.status === 'LOSE') {
        setIsGameRunning(false);
        setGameOver(true);
        setGameOverMessage(
          ` GAME OVER!\n\nYou were caught at Level ${currentLevel}!\n\nLevel Score: ${data.score}\nTotal Score: ${totalScore}\n\nTry Again!`
        );
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
      console.log('Starting game with difficulty:', selectedDifficulty);
      const response = await fetch(`${BACKEND_URL}/api/start?difficulty=${selectedDifficulty}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log('Game started:', data);

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

  const moveShip = async (direction: 'up' | 'down' | 'left' | 'right') => {
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

    const interval = setInterval(updateGame, 400);
    return () => clearInterval(interval);
  }, [isGameRunning, updateGame]);

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (!isGameRunning) return;

      switch (event.key) {
        case 'ArrowUp':
          event.preventDefault();
          moveShip('up');
          break;
        case 'ArrowDown':
          event.preventDefault();
          moveShip('down');
          break;
        case 'ArrowLeft':
          event.preventDefault();
          moveShip('left');
          break;
        case 'ArrowRight':
          event.preventDefault();
          moveShip('right');
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
        {gameState.grid.map((row, i) =>
          row.map((cell, j) => {
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
                emoji = '👹';
                cellClass += ' monster';
                break;
              default:
                emoji = '';
            }

            return (
              <div key={`${i}-${j}`} className={cellClass}>
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
          <p className="subtitle">Find the Treasure Before Pirates Catch You!</p>

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
              {loading ? '⏳ Loading...' : ' START - LEVEL 1'}<br/>
              <span className="btn-desc">EASY - 5 Lives</span>
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
          <span className="level-badge"> LEVEL {currentLevel}</span>
          <span className="difficulty-badge">{difficulty}</span> | 
          Lives: <span className="lives-badge">{gameState?.lives || 3}</span> |
          Total Score: <span className="score-badge">{totalScore}</span>
        </div>

        <div className="info-bar">
          <div className="info-item">
            <span className="stat-label"> Level Score:</span>
            <span className="stat-value">{gameState?.score || 0}</span>
          </div>
          <div className="info-item">
            <span className="stat-label"> Position:</span>
            <span className="stat-value">
              ({gameState?.shipX || 0},{gameState?.shipY || 0})
            </span>
          </div>
          <div className="info-item">
            <span className="stat-label"> Treasure:</span>
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
            <span className="stat-label"> Status:</span>
            <span className="stat-value">{gameState?.status || 'LOADING'}</span>
          </div>
        </div>

        {renderGrid()}

        <div className="legend">
          <div className="legend-item">🚢 = Your Ship (Start at 1,1)</div>
          <div className="legend-item">💰 = Treasure (Complete Level!)</div>
          <div className="legend-item">🏝️ = Island (Can't sail)</div>
          <div className="legend-item">🏴 = Pirate (Chases you!)</div>
          <div className="legend-item">👹 = Monster (Guards treasure)</div>
        </div>

        <div className="controls">
          <div></div>
          <button
            className="arrow-btn"
            onClick={() => moveShip('up')}
            title="Move Up (or ↑)"
          >
            ⬆️
          </button>
          <div></div>
          <button
            className="arrow-btn"
            onClick={() => moveShip('left')}
            title="Move Left (or ←)"
          >
            ⬅️
          </button>
          <button
            className="arrow-btn"
            onClick={() => moveShip('down')}
            title="Move Down (or ↓)"
          >
            ⬇️
          </button>
          <button
            className="arrow-btn"
            onClick={() => moveShip('right')}
            title="Move Right (or →)"
          >
            ➡️
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
          🔄 Back to Menu
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
                    ▶️ Next Level
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
                      setGameOver(false);
                      setIsGameRunning(false);
                      setSessionId(null);
                      setGameState(null);
                      setCurrentLevel(1);
                      setTotalScore(0);
                    }}
                  >
                    Try Again
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