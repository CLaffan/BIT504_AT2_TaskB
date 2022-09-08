import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class PongPanel extends JPanel implements ActionListener, KeyListener {
	
	private final static Color BACKGROUND_COLOUR = Color.BLACK;
	private final static int TIMER_DELAY = 5;
	private final static int BALL_MOVEMENT_SPEED = 5;
	private final static int POINTS_TO_WIN = 3;
	private final static int SCORE_TEXT_X = 100;
	private final static int SCORE_TEXT_Y = 100;
	private final static int SCORE_FONT_SIZE = 50;
	private final static String SCORE_FONT_FAMILY = "Serif";
	private final static int WINNER_TEXT_X = 200;
	private final static int WINNER_TEXT_Y = 200;
	private final static int WINNER_FONT_SIZE = 40;
	private final static String WINNER_FONT_FAMILY = "Serif";
	private final static String WINNER_TEXT = "WIN!";
	private final static int GAMEOVER_FONT_SIZE = 40;
	private final static String GAMEOVER_FONT_FAMILY = "Serif";
	private final static String GAMEOVER_TEXT = "Push Enter To Play Again";
	private final static int MENU_FONT_SIZE = 40;
	private final static String MENU_FONT_FAMILY = "Sans";
	private final static String MENU_TEXT = "Push Enter To Begin";
	
	GameState gameState = GameState.Menu;
	
	Ball ball;
	Paddle paddle1, paddle2;
	
	int player1Score = 0, player2Score = 0;
	Player gameWinner;
	
	public PongPanel() {
		setBackground(BACKGROUND_COLOUR);
		Timer timer = new Timer(TIMER_DELAY, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
	}

	public void createObjects() {
		ball = new Ball(getWidth(), getHeight());
		paddle1 = new Paddle(Player.One, getWidth(), getHeight());
		paddle2 = new Paddle(Player.Two, getWidth(), getHeight());
	}
	
	private void update() {
		switch(gameState) {
		case Menu: {

			createObjects();
			break;
		}
		case Initialising: {
				createObjects();
				gameState = GameState.Playing;
				ball.setXVelocity(BALL_MOVEMENT_SPEED);
				ball.setYVelocity(BALL_MOVEMENT_SPEED);
				break;
			}
			case Playing: {
				//move players paddles
				moveObject(paddle1);
				moveObject(paddle2);
				//move ball
				moveObject(ball);
				//check for wall bounce
				checkWallBounce();
				// Check for paddle bounce
				checkPaddleBounce();
				// Check if the game has been won
				checkWin();		
				break;
			}
			case GameRestart: {
				//reset ball to start position
				resetBall();
				//reset players paddles to start position
				resetPaddles();
				//reset players scores to 0 
				resetScores();
				//reset winner to clear the screen & play again 
				resetWinner();

				gameState = GameState.Playing;

			}
			case GameOver: {
				break;
			}
		}
	}

	private void resetPaddles() {
		paddle1.resetToInitialPosition();
		paddle2.resetToInitialPosition();
	}
	
	private void resetBall() {
		ball.resetToInitialPosition();
	}
	
	private void resetScores() {
		player1Score = 0;
		player2Score = 0;
	}

	private void resetWinner() {
		gameWinner = null;
	}
	
	private void moveObject(Sprite object) {
		object.setXPosition(object.getXPosition() + object.getXVelocity(),getWidth());
		object.setYPosition(object.getYPosition() + object.getYVelocity(),getHeight());
	}
	
	private void checkWallBounce() {
		if(ball.getXPosition() <= 0) {
			// Hit left side of screen
			ball.setXVelocity(-ball.getXVelocity());
			addScore(Player.Two);
			resetBall();
		} else if(ball.getXPosition() >= getWidth() - ball.getWidth()) {
			// Hit right side of screen
			ball.setXVelocity(-ball.getXVelocity());
			addScore(Player.One);
			resetBall();
		}
		if(ball.getYPosition() <= 0 || ball.getYPosition() >= getHeight() - ball.getHeight()) {
			// Hit top or bottom of screen
			ball.setYVelocity(-ball.getYVelocity());
		}
	}
	
	private void checkPaddleBounce() {
		if(ball.getXVelocity() < 0 && ball.getRectangle().intersects(paddle1.getRectangle())) {
			ball.setXVelocity(BALL_MOVEMENT_SPEED);
		}
		if(ball.getXVelocity() > 0 && ball.getRectangle().intersects(paddle2.getRectangle())) {
			ball.setXVelocity(-BALL_MOVEMENT_SPEED);
		}
	}
	
	private void checkWin() {
		if(player1Score >= POINTS_TO_WIN) {
			gameWinner = Player.One;
			gameState = GameState.GameOver;
		} else if(player2Score >= POINTS_TO_WIN) {
			gameWinner = Player.Two;
			gameState = GameState.GameOver;
		}
	}
	
	private void addScore(Player player) {
		if(player == Player.One) {
			player1Score++;
		} else if(player == Player.Two) {
			player2Score++;
		}
	}

	private void paintDottedLine(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.setPaint(Color.WHITE);
        g2d.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        g2d.dispose();
	}
	
	private void paintSprite(Graphics g, Sprite sprite) {
		g.setColor(sprite.getColour());
		g.fillRect(sprite.getXPosition(), sprite.getYPosition(), sprite.getWidth(), sprite.getHeight());
	}
	
	private void paintScores(Graphics g) {
		Font scoreFont = new Font(SCORE_FONT_FAMILY, Font.BOLD, SCORE_FONT_SIZE);
		String leftScore = Integer.toString(player1Score);
		String rightScore = Integer.toString(player2Score);
		g.setFont(scoreFont);
		g.drawString(leftScore, SCORE_TEXT_X, SCORE_TEXT_Y);
		g.drawString(rightScore, getWidth()-SCORE_TEXT_X, SCORE_TEXT_Y);
	}
	
	private void paintWinner(Graphics g) {
		if(gameWinner != null) {
			Font winnerFont = new Font(WINNER_FONT_FAMILY, Font.BOLD, WINNER_FONT_SIZE);
			g.setFont(winnerFont);
			int xPosition = getWidth() / 2;
			if(gameWinner == Player.One) {
				xPosition -= WINNER_TEXT_X;
			} else if(gameWinner == Player.Two) {
				xPosition += WINNER_TEXT_X;
			}
			g.drawString(WINNER_TEXT, xPosition, WINNER_TEXT_Y);
		}
	}
	
	private void paintGameOver(Graphics g) {
		if(gameWinner != null) {
			Font winnerFont = new Font(GAMEOVER_FONT_FAMILY, Font.BOLD, GAMEOVER_FONT_SIZE);
			g.setFont(winnerFont);
			int xPosition = getWidth() /5;
			int yPosition = getHeight()/4;
			g.drawString(GAMEOVER_TEXT, xPosition, yPosition);
		}
	}

	private void paintMenu(Graphics g) {
		if(gameState == GameState.Menu) {
			Font menuFont = new Font(MENU_FONT_FAMILY, Font.BOLD, MENU_FONT_SIZE);
			g.setFont(menuFont);
			int xPosition = getWidth()/5;
			int yPosition = getHeight()/3;
			g.drawString(MENU_TEXT, xPosition, yPosition);
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_W) {
			paddle1.setYVelocity(-10);
		} else if(event.getKeyCode() == KeyEvent.VK_S) {
			paddle1.setYVelocity(10);
		}
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			paddle2.setYVelocity(-10);
		} else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			paddle2.setYVelocity(10);
		}
		if(gameState == GameState.GameOver && event.getKeyCode() == KeyEvent.VK_ENTER) {
			gameState = GameState.GameRestart;
		}
		if(gameState == GameState.Menu && event.getKeyCode() == KeyEvent.VK_ENTER) {
			gameState = GameState.Initialising;
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_W || event.getKeyCode() == KeyEvent.VK_S) {
			paddle1.setYVelocity(0);
		}
		if(event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN) {
			paddle2.setYVelocity(0);
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		update();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintDottedLine(g);
		if(gameState == GameState.Menu) {
			paintMenu(g);
		}
		if(gameState != GameState.Initialising) {
			paintSprite(g, ball);
			paintSprite(g, paddle1);
			paintSprite(g, paddle2);
			paintScores(g);
			paintWinner(g);
			paintGameOver(g);
		}
	}

}
