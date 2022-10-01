// Colour Switch Remake
// This program is a remake of the mobile game, 'Color Switch', in which the player bounces a ball and avoids obstacles

import processing.core.*;

public class ColourSwitchRemake extends PApplet {

// Ball variables
int xPositionBall = 300;
int yPositionBall = 500;
int velocityBall = 0;
int acceleration = 1;
int diameter = 30; 
int radius = 15;

// Obstacles variables
// so the game knows when to spawn in a new obstacle
boolean obstacleSpawned = false;
// the type of obstacle currently spawned in
int currentObstacle = 0;
// how tall each of the obstacles is; using for scrolling
int heightOfObstacle = 500;
// Rectangle Obstacle variables
int rectangleColourSetting1 = 0;
int rectangleColourSetting2 = 0;
int xPositionRectangle1 = 0;
int xPositionRectangle2 = 240;
// Diamond Obstacle variables
int xPositionDiamond[] = {175, 300, 425, 300};
int yPositionDiamond[] = {200, 75, 200, 325};
int xVelocityDiamond[] = {1, 1, -1, -1};
int yVelocityDiamond[] = {-1, 1, 1, -1};
// Square Obstacle variables
int squareColourSetting = 0;
int xPositionSquare = 175;
int yPositionSquare = 200;
// Circle Obstacle variables
float circleFactor = 0;

// Screen scrolling and Collision variables
// how much the screen has scrolled since the game has started
int screenScroll = 0;
// how much the screen has scrolled since the last obstacle was spawned in
int scrollSinceLastObstacle = 0;
// used for the collision detection with the ball's sides and colour
int colourSidesBallDetection[] = new int[4];
// so the game knows when to check for ball and obstacle colour collision detection and when not to
// this variable is always true (collision detection is on nearly all of the time), EXCEPT for the one frame when the ball is switching colours
// this is because if the detection is on when the ball is switching colours, then the detection will wrongfully be triggered and the game will end
boolean collisionDetectionOn = true;

// Extra features variables
// used for the square obstacle's colour changing mechanism
int timeElapsed = 0;
int timeSinceLastLap = 0;
// used for the death animation when the game is over
int millisWhenGameOver = 0;
int millisElapsedSinceGameOver = 0;
// keeps track of the score
int score = 0;
int highScore = 0;
boolean highScoreAchieved = false;
// so the game knows when to spawn/detect collision with the stars and colour changers
boolean starSpawned = false;
boolean colourChangerSpawned = false;

// Game variables
// tells the game what to run
int gameState = 0;
// waits for the player to bounce the ball before starting the game
boolean gameStarted = false;
// colour of background (black)
int backgroundColour = 0xFF000000;
// colour of the stars, colour changers, text, etc. (white)
int accentColour = 0xFFFFFFFF;
// all of the possible colours for the ball and obstacles in the game, sorted by palette
int gameColours[][] = {{0xFFFFE80F, 0xFF8C12FB, 0xFFFF0080, 0xFF32E2F1},
  {0xFFFF333A, 0xFFFFC31F, 0xFF76AB21, 0xFF156CA2},
  {0xFFFFF275, 0xFFFF8C42, 0xFFFF3C38, 0xFFA23E48},
  {0xFFB5179E, 0xFF7209B7, 0xFF3A0CA3, 0xFF4CC9F0},
  {0xFFE63946, 0xFFA8DADC, 0xFF457B9D, 0xFF1D3557},
  {0xFF03045E, 0xFFEFCA08, 0xFF00B4D8, 0xFF90E0EF}};
// colour palette currently selected
int currentPalette = 0;
// current colour of the ball
int currentColour = 0;

 public void setup() {
  noStroke();
  textAlign(CENTER);
}

 public void draw() {
  background(backgroundColour);
  checkGameState();
}

public static void main(String[] passedArgs) {
  String[] appletArgs = new String[] { "ColourSwitchRemake" };
  if (passedArgs != null) {
    PApplet.main(concat(appletArgs, passedArgs));
  } else {
    PApplet.main(appletArgs);
  }
}

// tells the game which methods to run
 public void checkGameState() {
  if (gameState == 0) { // main
    runMainMenu();
  }
  if (gameState == 1) { // game
    runGame();
  }
  if (gameState == 2) { // death animation (after game is lost)
    runDeathAnimation();
  }
  if (gameState == 3) { // game over screen (after death animation)
    runGameOverScreen();
  }
  if (gameState == 4) { // instructions menu
    runInstructionsMenu();
  }
  if (gameState == 5) { // colour palette selection menu
    runColourSelectMenu();
  }
}

// draws the main menu screen
 public void runMainMenu() {
  rectMode(CORNER);
  // draws buttons
  fill(gameColours[currentPalette][1]);
  rect(150, 260, 300, 50);
  fill(gameColours[currentPalette][2]);
  rect(150, 360, 300, 50);
  fill(gameColours[currentPalette][3]);
  rect(150, 460, 300, 50);

  // writes the title
  textSize(100);
  fill(gameColours[currentPalette][0]);
  text("COLOUR", 300, 120);
  text("SWITCH", 300, 220);

  // writes the text on the buttons
  fill(backgroundColour);
  textSize(40);
  text("PLAY", 300, 300);
  text("INSTRUCTIONS", 300, 400);
  text("COLOUR SELECT", 300, 500);
}

// runs the game
 public void runGame() {
  moveBall();
  drawBall();
  drawObstacles();
  ballAndObstacleCollision();
  drawScore();
}

// runs the death animation, which runs immediately after the game has been lost
 public void runDeathAnimation() {
  rectMode(CORNER);
  // used to make the text animate and to make the game know when to run the game over screen
  millisElapsedSinceGameOver = millis() - millisWhenGameOver;
  // obstacles and score still run during animation
  drawObstacles();
  drawScore();
  textSize(80);
  fill(accentColour);

  // the words 'game over' are drawn slowly, letter by letter, based on the amount of time that has passed
  if (millisElapsedSinceGameOver >= 2400) {
    text("GAME OVER", 300, 200);
  } else
    if (millisElapsedSinceGameOver >= 2200) {
      text("GAME OVE", 300, 200);
    } else
      if (millisElapsedSinceGameOver >= 2000) {
        text("GAME OV", 300, 200);
      } else
        if (millisElapsedSinceGameOver >= 1800) {
          text("GAME O", 300, 200);
        } else
          if (millisElapsedSinceGameOver >= 1600) {
            text("GAME ", 300, 200);
          } else
            if (millisElapsedSinceGameOver >= 1400) {
              text("GAME", 300, 200);
            } else
              if (millisElapsedSinceGameOver >= 1200) {
                text("GAM", 300, 200);
              } else
                if (millisElapsedSinceGameOver >= 1000) {
                  text("GA", 300, 200);
                } else
                  if (millisElapsedSinceGameOver >= 800) {
                    text("G", 300, 200);
                  }

  // after 4 seconds of the death animation, game switches to game over screen
  if (millisElapsedSinceGameOver > 4000) {
    gameState = 3; // game over screen
  }
}

// runs the game over screen; runs immediately after the death animation is over
 public void runGameOverScreen() {
  rectMode(CORNER);
  // draws the buttons
  fill(gameColours[currentPalette][0]);
  rect(150, 360, 300, 50);
  fill(gameColours[currentPalette][1]);
  rect(150, 460, 300, 50);

  // writes game over, the score, and the high score
  textSize(80);
  fill(accentColour);
  text("GAME OVER", 300, 200);
  textSize(40);
  text("SCORE: " + score, 300, 260);
  // determines if the player set a new high score and writes words if they did
  if (score > highScore) {
    highScore = score;
    highScoreAchieved = true;
  }
  if (highScoreAchieved == true) {
    text("NEW HIGH SCORE!", 300, 100);
  }
  text("HIGH SCORE: " + highScore, 300, 300);

  // writes the text on the buttons
  fill(backgroundColour);
  textSize(40);
  text("PLAY AGAIN", 300, 400);
  text("BACK TO MENU", 300, 500);
}

// runs the instructions menu
 public void runInstructionsMenu() {
  rectMode(CORNER);
  // writes title
  textSize(80);
  fill(gameColours[currentPalette][1]);
  text("INSTRUCTIONS", 300, 120);

  // draws button and button text
  fill(gameColours[currentPalette][2]);
  rect(150, 460, 300, 50);
  fill(backgroundColour);
  textSize(40);
  text("BACK TO MENU", 300, 500);

  // writes the instructions
  textSize(20);
  fill(gameColours[currentPalette][3]);
  text("Press spacebar to keep the ball bouncing!", 300, 180);
  text("Only go through obstacles which match your ball's colour!", 300, 220);
  text("Avoid obstacles of any colour which doesn't match your ball!", 300, 260);
  text("If you accidentally touch the wrong colour, it's game over!", 300, 300);
  text("Collect white stars to get points!", 300, 340);
  text("White circles change your ball's colour!", 300, 380);
  text("How far can you go?", 300, 420);
}

 public void runColourSelectMenu() {
  rectMode(CORNER);
  // writes title
  textSize(80);
  fill(gameColours[currentPalette][2]);
  text("COLOUR SELECT", 300, 120);

  // draws button and button text
  fill(gameColours[currentPalette][0]);
  rect(150, 360, 300, 50);
  fill(backgroundColour);
  textSize(40);
  text("BACK TO MENU", 300, 400);

  // draws a rectangle behind the colour palette currently selected
  fill(accentColour);
  for (int i = 0; i < 3; i++) {
    if (currentPalette == i) {
      rect(90, 70 * i + 150, 200, 50);
    }
  }
  for (int i = 0; i < 3; i++) {
    if (currentPalette == i + 3) {
      rect(90 + 220, 70 * i + 150, 200, 50);
    }
  }

  // draws the squares of all the colour options and colour palettes
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 4; j++) {
      fill(gameColours[i][j]);
      rect(50 * j + 100, 70 * i + 160, 30, 30);
    }
    for (int k = 0; k < 4; k++) {
      fill(gameColours[i + 3][k]);
      rect(50 * k + 320, 70 * i + 160, 30, 30);
    }
  }
}

// resets most of the variables in the game; runs right before the game starts each time
 public void resetVariables() {
  // Ball variables
  xPositionBall = 300;
  yPositionBall = 500;
  velocityBall = 0;

  // Obstacles variables
  obstacleSpawned = false;
  currentObstacle = 0;

  // Rectangle Obstacle variables
  rectangleColourSetting1 = 0;
  rectangleColourSetting2 = 0;
  xPositionRectangle1 = 0;
  xPositionRectangle2 = 240;

  // Diamond Obstacle variables
  xPositionDiamond[0] = 175;
  xPositionDiamond[1] = 300;
  xPositionDiamond[2] = 425;
  xPositionDiamond[3] = 300;

  yPositionDiamond[0] = 200;
  yPositionDiamond[1] = 75;
  yPositionDiamond[2] = 200;
  yPositionDiamond[3] = 325;

  xVelocityDiamond[0] = 1;
  xVelocityDiamond[1] = 1;
  xVelocityDiamond[2] = -1;
  xVelocityDiamond[3] = -1;

  yVelocityDiamond[0] = -1;
  yVelocityDiamond[1] = 1;
  yVelocityDiamond[2] = 1;
  yVelocityDiamond[3] = -1;

  // Square Obstacle variables
  squareColourSetting = 0;
  xPositionSquare = 175;
  yPositionSquare = 200;

  // Circle Obstacle variables
  circleFactor = 0;

  // Screen scrolling and Collision variables
  screenScroll = 0;
  scrollSinceLastObstacle = 0;
  collisionDetectionOn = true;

  // Extra features variables
  timeElapsed = millis()/1000;
  timeSinceLastLap = timeElapsed;
  millisWhenGameOver = 0;
  millisElapsedSinceGameOver = 0;

  rectMode(CORNER);
  score = 0;
  highScoreAchieved = false;
  starSpawned = false;
  colourChangerSpawned = false;

  gameStarted = false;
  // randomizes the ball's starting colour
  currentColour = PApplet.parseInt(random(4));

  // starts the game
  gameState = 1; // runs game
}

// draws the ball based off the current colour
 public void drawBall() {
  // ensures that the collision detection is on and running (when the collision detection is off, it is off for just one frame, so this turns it back on)
  collisionDetectionOn = true;
  fill(gameColours[currentPalette][currentColour]);
  ellipse(xPositionBall, yPositionBall, diameter, diameter);
}

// moves the ball based off of velocity and acceleration
 public void moveBall() {
  velocityBall = velocityBall + acceleration;

  checkIfGameHasNotStarted();

  yPositionBall = yPositionBall + velocityBall;

  checkIfBallIsOffscreen();

  scrollScreen();
}

// if the game hasn't started, the ball can't move
 public void checkIfGameHasNotStarted() {
  if (gameStarted == false) {
    velocityBall = 0;
  }
}

// prevents ball from falling offscreen
 public void checkIfBallIsOffscreen() {
  int ballBottom = yPositionBall + radius;

  if (ballBottom > height) {
    yPositionBall = height - radius;
  }
}

// makes the screen scroll when the ball is higher up on the screen
 public void scrollScreen() {
  int ballTop = yPositionBall - radius;
  // boundary; stops the ball from bouncing too high
  if (ballTop < 50) {
    yPositionBall = 50 + radius; // mostly to prevent glitches with the boundary e.g. spamming the spacebar, holding down the spacebar etc.
    velocityBall = 1; // halts the ball from bouncing too high by making it velocity positive (i.e. making it fall)
  }
  // if the ball is above a certain point, the screen will scroll
  if (ballTop < 400) {
    screenScroll = screenScroll + 1 * ((500 - yPositionBall) / 100); // screen scrolls faster and faster when the ball goes higher and higher; makes the scrolling feel smooth and natural and not jagged
  }
}

// draws an obstacle, spawns an obstacle in if there isn't one, and draws a star and colour changer based off of the obstacle's current position
 public void drawObstacles() {
  // if there is an obstacle spawned in, draws an obstacle based on the current obstacle type
  if (obstacleSpawned == true) {
    if (currentObstacle == 0) {
      rectMode(CORNER);
      drawRectangleObstacle();
    }
    if (currentObstacle == 1) {
      drawDiamondObstacle();
    }
    if (currentObstacle == 2) {
      rectMode(CENTER);
      drawSquareObstacle();
    }
    if (currentObstacle == 3) {
      drawCircleObstacle();
    }
  }
  // if there is no obstacle spawned in, picks a random obstacle type, resets it, and spawns it in
  if (obstacleSpawned == false) {
    pickObstacle();
    resetObstacle(currentObstacle);
  }
}

// randomly picks one of the four obstacle types to spawn in
 public void pickObstacle() {
  int randomNumber = PApplet.parseInt(random(4));
  // ensures that one type of obstacle cannot spawn twice in a row
  if (randomNumber != currentObstacle || gameStarted == false) {
    currentObstacle = randomNumber;
  } else {
    pickObstacle();
  }
}

// resets an obstacle's position and spawns it in again
 public void resetObstacle(int obstacle) {
  if (obstacle == 0) {
    rectangleColourSetting1 = 0;
    rectangleColourSetting2 = 0;
    xPositionRectangle1 = 0;
    xPositionRectangle2 = 240;
  }

  if (obstacle == 1) {
    xPositionDiamond[0] = 175;
    xPositionDiamond[1] = 300;
    xPositionDiamond[2] = 425;
    xPositionDiamond[3] = 300;

    yPositionDiamond[0] = 200;
    yPositionDiamond[1] = 75;
    yPositionDiamond[2] = 200;
    yPositionDiamond[3] = 325;

    xVelocityDiamond[0] = 1;
    xVelocityDiamond[1] = 1;
    xVelocityDiamond[2] = -1;
    xVelocityDiamond[3] = -1;

    yVelocityDiamond[0] = -1;
    yVelocityDiamond[1] = 1;
    yVelocityDiamond[2] = 1;
    yVelocityDiamond[3] = -1;
  }

  if (obstacle == 2) {
    squareColourSetting = 0;
    xPositionSquare = 175;
    yPositionSquare = 200;
  }

  if (obstacle == 3) {
    circleFactor = 0;
  }

  // spawns in the obstacle, and its star and colour changer
  obstacleSpawned = true;
  starSpawned = true;
  colourChangerSpawned = true;
  // resets the scroll, so the obstacle will scroll from top to bottom of the screen again
  scrollSinceLastObstacle = screenScroll;
}

// draws the first obstacle; the rectangle obstacle
// two rows of differently-coloured rectangles move from left to right of the screen
 public void drawRectangleObstacle() {
  // five rectangles onscreen at a time per row
  for (int i = 0; i < 5; i++) {
    // when a rectangle goes offscreen to the right in row 1, resets the rectangle back at the left side of the screen
    if (xPositionRectangle1 + (i - 1) * 150 > width) {
      xPositionRectangle1 = 0;
      // swaps the colours of the rectangles when a rectangle resets (make it look like there is an infinite number of rectangles)
      rectangleColourSetting1 = rectangleColourSetting1 + 1;
      rectangleColourSetting1 = rectangleColourSetting1 % 4;
    } else {
      // draws a rectangle based off of the current colour setting and the current position
      fill(gameColours[currentPalette][(i + ((4 - rectangleColourSetting1) % 4)) % 4]);
      rect(xPositionRectangle1 + (i - 1) * 150, 150 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle), 100, 30);
    }

    // when a rectangle goes offscreen to the right in row 2, resets the rectangle back at the left side of the screen
    if (xPositionRectangle2 + (i - 1) * 150 > width) {
      xPositionRectangle2 = 0;
      // swaps the colours of the rectangles when a rectangle resets (make it look like there is an infinite number of rectangles)
      rectangleColourSetting2 = rectangleColourSetting2 + 1;
      rectangleColourSetting2 = rectangleColourSetting2 % 4;
    } else {
      // draws a rectangle based off of the current colour setting and the current position
      fill(gameColours[currentPalette][(i + ((4 - rectangleColourSetting2) % 4)) % 4]);
      rect(xPositionRectangle2 + (i - 1) * 150, 450 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle), 100, 30);
    }
  }
  // row 2 (bottom row) moves twice as fast as row 1 (top row)
  xPositionRectangle1 = xPositionRectangle1 + 1;
  xPositionRectangle2 = xPositionRectangle2 + 2;

  // if there is a star spawned in, draws it based off of the obstacle's current coordinates
  if (starSpawned == true) {
    drawStar(150 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) + 165);
  }

  // if there is a colour changer spawned in, draws it based off of the obstacle's current coordinates
  if (colourChangerSpawned == true) {
    drawColourChanger(150 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) - 100);
  }

  // if the obstacle is offscreen, despawns the obstacle
  if (150 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) > height) {
    obstacleSpawned = false;
  }
}

// draws the second obstacle; the diamond obstacle
// 4 circles of different colours move in their own diamond formations and spread all throughout the screen
 public void drawDiamondObstacle() {
  // draws 4 circles
  for (int i = 0; i < xPositionDiamond.length; i++) {
    fill(gameColours[currentPalette][i]);
    ellipse(xPositionDiamond[i], yPositionDiamond[i] - heightOfObstacle + (screenScroll - scrollSinceLastObstacle), 80, 80);
    // moves each circle based off of their current velocity (velocities are different for each circle, hence why they all move in different directions)
    xPositionDiamond[i] = xPositionDiamond[i] + xVelocityDiamond[i];
    yPositionDiamond[i] = yPositionDiamond[i] + yVelocityDiamond[i];
  }

  // once the first (leftmost) circle reaches a certain position, flips the velocities (changes the directions) of all 4 circles
  if (xPositionDiamond[0] >= 425 || xPositionDiamond[0] <= 175) {
    for (int i = 0; i < xVelocityDiamond.length; i++) {
      xVelocityDiamond[i] = xVelocityDiamond[i] * -1;
    }
  }
  if (yPositionDiamond[0] <= 75 || yPositionDiamond[0] >= 325) {
    for (int i = 0; i < yVelocityDiamond.length; i++) {
      yVelocityDiamond[i] = yVelocityDiamond[i] * -1;
    }
  }

  // if there is a star spawned in, draws it based off of the obstacle's current coordinates
  if (starSpawned == true) {
    drawStar(200 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle));
  }

  // if there is a colour changer spawned in, draws it based off of the obstacle's current coordinates
  if (colourChangerSpawned == true) {
    drawColourChanger(75 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) - 100);
  }

  // if the obstacle is offscreen, despawns the obstacle
  int numberOfCirclesOffscreen = 0;
  for (int i = 0; i < yPositionDiamond.length; i++) {
    if (yPositionDiamond[i] - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) - 40 > height) {
      numberOfCirclesOffscreen = numberOfCirclesOffscreen + 1;
    }
  }

  // obstacle despawns only if all 4 circles are offscreen
  if (numberOfCirclesOffscreen >= 4) {
    obstacleSpawned = false;
  }
}

// draws the third obstacle; the square obstacle
// there is a square and each side is a different colour; the colours flip every second
 public void drawSquareObstacle() {
  timeElapsed = millis()/1000;

  // draws all four sides of the square, colours them based off of the current colour setting
  fill(gameColours[currentPalette][(squareColourSetting + 0) % 4]);
  rect(xPositionSquare, yPositionSquare - heightOfObstacle + (screenScroll - scrollSinceLastObstacle), 50, 150);
  fill(gameColours[currentPalette][(squareColourSetting + 1) % 4]);
  rect(xPositionSquare + 125, yPositionSquare - 125 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle), 150, 50);
  fill(gameColours[currentPalette][(squareColourSetting + 2) % 4]);
  rect(xPositionSquare + 125 * 2, yPositionSquare - heightOfObstacle + (screenScroll - scrollSinceLastObstacle), 50, 150);
  fill(gameColours[currentPalette][(squareColourSetting + 3) % 4]);
  rect(xPositionSquare + 125, yPositionSquare + 125 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle), 150, 50);

  // flips the colours once a second has passed
  if (timeElapsed > timeSinceLastLap) {
    timeSinceLastLap = timeElapsed;
    squareColourSetting = (squareColourSetting + 1) % 4;
  }

  // if there is a star spawned in, draws it based off of the obstacle's current coordinates
  if (starSpawned == true) {
    drawStar(yPositionSquare - heightOfObstacle + (screenScroll - scrollSinceLastObstacle));
  }

  // if there is a colour changer spawned in, draws it based off of the obstacle's current coordinates
  if (colourChangerSpawned == true) {
    drawColourChanger(yPositionSquare - 125 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) - 25 - 100);
  }

  // if the obstacle is offscreen, despawns the obstacle
  if (yPositionSquare - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) - 150 > height) {
    obstacleSpawned = false;
  }
}

// draws the fourth obstacle; the circle obstacle
// 8 circles (4 different colours) form a big circle, and the 8 circles each move clockwise in a circular motion (the big circle spins)
 public void drawCircleObstacle() {
  // draws 8 circles
  for (int i = 0; i < 8; i++) {
    fill(gameColours[currentPalette][i % 4]);
    // draws the circle based off of the sine and cosine of the current angle
    // this is because the y coordinate of a point moving in a circle is a sine wave, and the x coordinate of a point moving in a circle is a cosine wave
    // by the way, I learned that fact from a Numberphile video!
    ellipse(cos(TWO_PI * (circleFactor + 0.125f * i)) * 150 + 300, sin(TWO_PI * (circleFactor + 0.125f * i)) * 150 + 250 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle), 100, 100);
  }

  // changes the value of angle a tiny bit each frame; makes the circles move slowly and smoothly
  circleFactor = circleFactor + 0.001953125f; // 1/512 in decimal form

  // resets the angle after one full rotation (360 degrees/two pi radians)
  if (circleFactor == 1) {
    circleFactor = 0;
  }

  // if there is a star spawned in, draws it based off of the obstacle's current coordinates
  if (starSpawned == true) {
    drawStar(250 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle));
  }

  // if there is a colour changer spawned in, draws it based off of the obstacle's current coordinates
  if (colourChangerSpawned == true) {
    drawColourChanger(250 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) - 190 - 100);
  }

  // if the obstacle is offscreen, despawns the obstacle
  int numberOfCirclesOffscreen = 0;
  for (int i = 0; i < 8; i++) {
    // the -50 at the end ensures it is checking the TOP of each circle, and not the centre
    if (sin(TWO_PI * (circleFactor + 0.125f * i)) * 150 + 250 - heightOfObstacle + (screenScroll - scrollSinceLastObstacle) - 50 > height) {
      numberOfCirclesOffscreen = numberOfCirclesOffscreen + 1;
    }
  }

  // obstacle despawns only if all 8 circles are offscreen
  if (numberOfCirclesOffscreen >= 8) {
    obstacleSpawned = false;
  }
}

// draws a star based off of the current obstacle's current position
// the obstacle's current position is sent in as a parameter, and that position becomes the centre of the star
 public void drawStar(int centre) {
  fill(accentColour);

  // draws star based off of the position sent in as a parameter
  triangle(300 - 35, centre - 20, 300 + 35, centre - 20, 300, centre + 15);
  triangle(300, centre - 45, 300, centre + 15, 300 - 25, centre + 30);
  triangle(300, centre - 45, 300, centre + 15, 300 + 25, centre + 30);

  // checks if the ball is colliding with the star
  ballAndStarCollision(centre + 15);
}

// draws a colour changer based off of the current obstacle's current position
// the obstacle's current position is sent in as a parameter, and that position becomes the centre of the colour changer
 public void drawColourChanger(int centre) {
  fill(accentColour);

  // draws colour changer based off of the position sent in as a parameter
  ellipse(300, centre, 50, 50);

  // checks if the ball is colliding with the colour changer
  ballAndColourChangerCollision(centre + 25);
}

// checks if the ball is colliding with the star
 public void ballAndStarCollision(int starBottom) {
  int ballTop = yPositionBall - radius;

  // if the top of the ball is higher than the bottom of the star, and there is a star spawned in...
  if (ballTop < starBottom && starSpawned == true) {
    // ...the star despawns and the player's score increases
    starSpawned = false;
    score = score + 1;
  }
}

// checks if the ball is colliding with the colour changer
 public void ballAndColourChangerCollision(int circleBottom) {
  int ballTop = yPositionBall - radius;

  // if the top of the ball is higher than the bottom of the colour changer, and there is a colour changer spawned in...
  if (ballTop < circleBottom && colourChangerSpawned == true) {
    // ...the colour changer despawns
    colourChangerSpawned = false;
    // ...the collision detection between the ball and the obstacles (the colour collision detection) is turned off (for just one frame)
    collisionDetectionOn = false;
    // ...and a new colour is selected for the ball
    changeBallColour();
  }
}

// switches the colour of the ball
 public void changeBallColour() {
  int randomNumber = PApplet.parseInt(random(4));
  // prevents the ball from switching to the same colour; the colour must swap everytime, so this ensures that the ball won't 'swap' to the ball's current colour (e.g. swapping from pink to pink)
  if (randomNumber != currentColour) {
    currentColour = randomNumber;
  } else {
    changeBallColour();
  }
}

// checks for collision between the ball and the differently-coloured obstacles]
// this collision is based off of COLOUR, unlike the star and colour changer collision which is based off of POSITION
 public void ballAndObstacleCollision() {
  // only runs if the collision detection is on; detection is on all the time except for the one frame when the ball is changing colours
  // this is because if the ball is changing colours (e.g. going from blue to yellow) and the collision detection is on...
  // ...the game will wrongfully think that the ball is colliding with something of a different colour, which will cause a game over!
  // (e.g. game will think blue ball is colliding with something yellow, when the ball is just switching to yellow)
  if (collisionDetectionOn == true) {
    // gets the colour values for the four points (sides) of the ball
    // left side of ball
    colourSidesBallDetection[0] = getPixelValue(xPositionBall - radius + 1, yPositionBall);
    // top of ball
    colourSidesBallDetection[1] = getPixelValue(xPositionBall, yPositionBall - radius + 1);
    // right side of ball
    colourSidesBallDetection[2] = getPixelValue(xPositionBall + radius - 2, yPositionBall);
    // bottom of ball
    colourSidesBallDetection[3] = getPixelValue(xPositionBall, yPositionBall + radius - 2);

    // checks all four sides of the ball
    // since this detection only checks 4 points on the ball, it is not 100% perfect, however, programming perfect detection for the ball would have been way too complicated
    for (int i = 0; i < 4; i++) {
      // if one of the 4 points of the ball is currently the colour of one of the 3 colours not currently selected, (e.g. the ball is purple but one of the points is currently yellow)...
      // ...that means the ball is colliding with an obstacle of a different colour! that means game over!
      if (colourSidesBallDetection[i] == gameColours[currentPalette][(currentColour + 1) % 4] ||
        colourSidesBallDetection[i] == gameColours[currentPalette][(currentColour + 2) % 4] ||
        colourSidesBallDetection[i] == gameColours[currentPalette][(currentColour + 3) % 4]) {
        // ends the game and starts running death animation
        gameState = 2; // runs death animation
        millisWhenGameOver = millis(); // records the number of milliseconds the game has been running for at the moment the game ends; the death animation is based off of this value
      }
    }
  }
}

// draws the player's current score at the top of the screen
 public void drawScore() {
  textSize(40);
  fill(accentColour);
  text("SCORE: " + score, 300, 50);
}

// used for the ball and obstacle colour collision detection; this is used to check the colour a certain pixel is at the current frame
 public int getPixelValue(int x, int y) {
  loadPixels();
  return pixels[x + width*y];
}

// makes the player's key presses work; bouncing the ball by using the spacebar relies on this
 public void keyPressed() {
  // when the space bar is pressed, the ball's velocity becomes negative (ball starts bouncing upward)
  if (keyCode == 32) {
    velocityBall = -12;
    // if the spacebar has been pressed and the game has not started yet, then the game has now officially started because the ball has started bouncing
    if (gameStarted == false) {
      gameStarted = true;
    }
  }
}

// makes the player's mouse presses work; all of the buttons rely on this
 public void mousePressed() {
  if (gameState == 0) { // makes the buttons on the main menu work
    if (mouseX > 150 && mouseX < 450 && mouseY > 260 && mouseY < 310) {
      resetVariables(); // resets variables, then starts game
    }
    if (mouseX > 150 && mouseX < 450 && mouseY > 360 && mouseY < 410) {
      gameState = 4; // runs instructions menu
    }
    if (mouseX > 150 && mouseX < 450 && mouseY > 460 && mouseY < 510) {
      gameState = 5; // runs colour select menu
    }
  }

  if (gameState == 3) { // makes the buttons on the game over screen work
    if (mouseX > 150 && mouseX < 450 && mouseY > 360 && mouseY < 410) {
      resetVariables(); // restarts the game (resets variables, then starts game)
    }
    if (mouseX > 150 && mouseX < 450 && mouseY > 460 && mouseY < 510) {
      gameState = 0; // runs main menu
    }
  }

  if (gameState == 4) { // makes the button on the instructions menu work
    if (mouseX > 150 && mouseX < 450 && mouseY > 460 && mouseY < 510) {
      gameState = 0; // runs main menu
    }
  }

  if (gameState == 5) { // makes the button on the colour select menu work
    if (mouseX > 90 && mouseX < 290 && mouseY > 150 && mouseY < 200) {
      currentPalette = 0; // selects first palette
    }
    if (mouseX > 90 && mouseX < 290 && mouseY > 220 && mouseY < 270) {
      currentPalette = 1; // selects second palette
    }
    if (mouseX > 90 && mouseX < 290 && mouseY > 290 && mouseY < 340) {
      currentPalette = 2; // selects third palette
    }
    if (mouseX > 310 && mouseX < 510 && mouseY > 150 && mouseY < 200) {
      currentPalette = 3; // selects fourth palette
    }
    if (mouseX > 310 && mouseX < 510 && mouseY > 220 && mouseY < 270) {
      currentPalette = 4; // selects fifth palette
    }
    if (mouseX > 310 && mouseX < 510 && mouseY > 290 && mouseY < 340) {
      currentPalette = 5; // selects sixth palette
    }
    if (mouseX > 150 && mouseX < 450 && mouseY > 360 && mouseY < 410) {
      gameState = 0; // runs main menu
    }
  }
}


  public void settings() { size(600, 600); }
}
