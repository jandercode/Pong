package com.kyhgroupd.ponggroupd

import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import com.kyhgroupd.ponggroupd.gameobjects.ComboText
import com.kyhgroupd.ponggroupd.gameobjects.GameObject
import com.kyhgroupd.ponggroupd.gameobjects.GameText

/**
 * Singleton class to manage UI objects drawn on the screen (score text, lives text, etc.)
 */
object UIManager {

    var uiHeight: Int = 0
    var uiBorderWidth = 3f
    val uiHeightFactor = 12

    //UI border paint
    val uiPaint = Paint()

    //UI objects
    val uiObjects = mutableListOf<GameObject>()

    //UI Text
    var scoreText: GameText? = null
    var scoreTextPlayer1: GameText? = null
    var scoreTextPlayer2: GameText? = null
    var highScoreText: GameText? = null
    var livesText: GameText? = null
    var levelText: GameText? = null
    var comboText: ComboText? = null
    var textSize: Float = 0f
    val textSizeFactor: Int = 25

    /**
     * Resets UI text when new game is started
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun resetUI(){

        //UI
        uiHeight = GameManager.screenSizeY / uiHeightFactor
        textSize = when(GameManager.gameMode)
        { "pong" -> (GameManager.screenSizeX / textSizeFactor * 2).toFloat()
            else -> (GameManager.screenSizeX / textSizeFactor).toFloat() }
        uiPaint.style = Paint.Style.STROKE
        uiPaint.color = Color.WHITE
        uiPaint.strokeWidth = uiBorderWidth

        //Reset UI objects
        uiObjects.clear()

        //UI objects
        if (GameManager.gameMode != "pong") {
            this.addUiText()
        } else {
         addPongUiText()
        }

        //UI Data
        GameManager.score = 0
        GameManager.player2Score = 0
        GameManager.highScore = DataManager.loadHighScore(GameManager.gameMode)
        GameManager.lives = 3
        GameManager.player2Lives = 3
        GameManager.level = 1
        GameManager.currentCombo = 0
        comboText = null

    }

    /**
     * Creates GameText objects to display user score, high score, number of lives
     * and current level in breakout and golf game modes.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addUiText(){
        val scoreText = GameText(
            GameManager.screenSizeX /20, (uiHeight/2.5).toInt(),
            GameManager.gameTextColor
        )
        this.scoreText = scoreText
        uiObjects.add(scoreText)
        val highScoreText = GameText(
            GameManager.screenSizeX /2, (uiHeight/2.5).toInt(),
            GameManager.gameTextColor
        )
        this.highScoreText = highScoreText
        uiObjects.add(highScoreText)
        val livesText = GameText(
            GameManager.screenSizeX /20, (uiHeight/1.25).toInt(),
            GameManager.gameTextColor
        )
        this.livesText = livesText
        uiObjects.add(livesText)
        val levelText = GameText(
            GameManager.screenSizeX /2, (uiHeight/1.25).toInt(),
            GameManager.gameTextColor
        )
        this.levelText = levelText
        uiObjects.add(levelText)
    }

    /**
     * Creates GameText objects to display score of players in pong game mode.
     * In pong single player mode user player's score and AI player's score are shown
     * facing the user, the AI player's score toward the top right of the screen and
     * the user player's score toward to bottom left of the screen. In pong two-player mode
     * both players' scores are shown side-on at the left edge of the screen.
     *
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addPongUiText(){
        var pongScoreText: GameText
        var pongScoreText2: GameText
        if (GameManager.pongPlayerMode == 2) {
            pongScoreText2 = GameText((GameManager.screenSizeX*0.75).toInt(),(GameManager.screenSizeX*0.65).toInt(),
                GameManager.gameTextColor)
            pongScoreText = GameText((GameManager.screenSizeX*0.25).toInt(),(GameManager.screenSizeX*0.65).toInt(),
                GameManager.gameTextColor)
        } else {
            pongScoreText2 = GameText((GameManager.screenSizeX*0.95-(textSize*0.75)).toInt(),(GameManager.screenSizeY*0.25).toInt(),
                GameManager.gameTextColor)
            pongScoreText = GameText((GameManager.screenSizeX*0.05).toInt(),(GameManager.screenSizeY*0.75).toInt(),
                GameManager.gameTextColor)
        }

        this.scoreTextPlayer1 = pongScoreText
        this.scoreTextPlayer2 = pongScoreText2
        uiObjects.add(pongScoreText)
        uiObjects.add(pongScoreText2)
    }
}