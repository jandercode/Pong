package com.kyhgroupd.ponggroupd.gameobjects

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import com.kyhgroupd.ponggroupd.GameManager
import com.kyhgroupd.ponggroupd.SoundManager
import com.kyhgroupd.ponggroupd.UIManager

class Ball(startX: Int, startY: Int, color: Int) : GameObject(startX, startY, color) {

    var radius : Int = 0
    var speedX: Int = 0
    var speedY: Int = 0

    init {
        radius = GameManager.referenceBrick!!.height/2
        width = radius*2
        height = radius*2
        speedX = GameManager.ballSpeed
        speedY = -GameManager.ballSpeed
    }

    override fun draw(canvas: Canvas?) {
        this.paint.shader = LinearGradient(posX.toFloat(), posY.toFloat(), (posX+(radius)).toFloat(),
            (posY+(radius)).toFloat(), GameManager.gradientColor, this.paint.color, Shader.TileMode.CLAMP)
        canvas?.drawCircle((this.posX.toFloat()+this.radius), (this.posY.toFloat()+this.radius),
            this.radius.toFloat(), this.paint)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun update(){
        posX += speedX
        posY += speedY

        //Border collision check
        checkBorderCollision()

        //Object collision check (return if other game object is null)
        val gameObject: GameObject = this.collidingWith() ?: return

        //Brick collision
        when(gameObject){
            is Brick -> brickCollision(gameObject)
            is Paddle -> paddleCollision(gameObject)
            is Goal -> goalCollision()
        }
    }

    private fun goalCollision(){
        GameManager.score += 100 + (GameManager.level -1) * GameManager.bonusScorePerLevel
        GameManager.nextLevel()
    }

    private fun paddleCollision(paddle: Paddle) {
        //100% of ball speed to be shared by a percentage over y/x-axis
        val totalBallSpeed = GameManager.ballSpeed *2

        //Change ball angle depending on paddle collision zone
        val paddleZones = 6
        val widthPerZone = paddle.width/paddleZones

        var speedY: Int = 0
        var speedX: Int = 0

        //Zone 1 (far left side)
        if(this.posX < paddle.posX+widthPerZone && this.posX+this.width > paddle.posX){
            speedY = -(totalBallSpeed*0.3).toInt()
            speedX = -(totalBallSpeed*0.7).toInt()
        }
        //Zone 2
        else if(this.posX < paddle.posX+(widthPerZone*2) && this.posX+this.width > paddle.posX+widthPerZone){
            speedY = -(totalBallSpeed*0.5).toInt()
            speedX = -(totalBallSpeed*0.5).toInt()
        }
        //Zone 3
        else if(this.posX < paddle.posX+(widthPerZone*3) && this.posX+this.width > paddle.posX+(widthPerZone*2)){
            speedY = -(totalBallSpeed*0.7).toInt()
            speedX = -(totalBallSpeed*0.3).toInt()
        }
        //Zone 4
        else if(this.posX < paddle.posX+(widthPerZone*4) && this.posX+this.width > paddle.posX+(widthPerZone*3)){
            speedY = -(totalBallSpeed*0.7).toInt()
            speedX = (totalBallSpeed*0.3).toInt()
        }
        //Zone 5
        else if(this.posX < paddle.posX+(widthPerZone*5) && this.posX+this.width > paddle.posX+(widthPerZone*4)){
            speedY = -(totalBallSpeed*0.5).toInt()
            speedX = (totalBallSpeed*0.5).toInt()
        }
        //Zone 6 (far right side)
        else {
            speedY = -(totalBallSpeed*0.3).toInt()
            speedX = (totalBallSpeed*0.7).toInt()
        }

        //Player 1
        if(paddle.player == 1){
            if(this.posY + this.height > paddle.posY && this.posY + this.height < paddle.posY + radius){

                this.speedY = speedY
                this.speedX = speedX

            } else {
                speedX *= -1
            }
        }
        //Player 2
        else{
            if(this.posY < paddle.posY + paddle.height && this.posY > paddle.posY + radius){

                this.speedY = -speedY
                this.speedX = speedX

            } else {
                speedX *= -1
            }
        }

        //Reset combo
        GameManager.currentCombo = 0
        UIManager.comboText = null

        //SFX
        SoundManager.playBallBounceSFX()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun brickCollision(brick: Brick) {
        if (this.posX < brick.posX+brick.width-radius &&
            this.posX+this.width > brick.posX+radius) {
            speedY *= -1
        } else {
            speedX *= -1
        }

        //Unbreakable brick?
        if(brick.unbreakable){
            //SFX
            SoundManager.playBallBounceSFX()
            return
        }

        //Non-unbreakable brick?
        brick.health--
        if(GameManager.gameMode == "golf"){
            brick.changeColor()
        }

        //Check brick health
        if(brick.health > 0){
            //SFX
            SoundManager.playBallBounceSFX()
            return
        }

        //Finally
        brick.destroy()
        GameManager.gameObjects.remove(brick)

        //Score
        if(GameManager.gameMode == "breakout"){
            //Add scored based on level
            GameManager.score += GameManager.scorePerBrick + ((GameManager.level -1) * GameManager.bonusScorePerLevel)
            //Add score based on combo
            if(GameManager.currentCombo > 0){
                val comboValue = GameManager.currentCombo * GameManager.comboBonusScore
                GameManager.score += comboValue
                val comboText = ComboText(GameManager.screenSizeX /2, GameManager.screenSizeY /2, GameManager.gameTextColor, comboValue)
                UIManager.comboText = comboText
                SoundManager.playComboSFX()
            }
            GameManager.currentCombo++
        }

        //SFX
        SoundManager.playDestroyBrickSFX()
    }

    private fun checkBorderCollision() {
        if(GameManager.gameMode == "pong") {
            var player = 0
            if (this.posY < -GameManager.screenSizeY /6) {
                player = 2
            }
            if (this.posY+this.height > GameManager.screenSizeY +(GameManager.screenSizeY /6)){
                player = 1
            }
            if(player != 0) {
                this.loseLifePong(player)
            }
        } else {
            if (this.posY < UIManager.uiHeight) {
                this.speedY = Math.abs(this.speedY)
                //SFX
                SoundManager.playBallBounceSFX()
            }
            if (this.posY + this.height > GameManager.screenSizeY + (GameManager.screenSizeY / 6)) {
                if(GameManager.gameMode == "breakout"){
                    this.loseLife()
                } else if(GameManager.gameMode == "golf"){
                    addLife()
                }

            }
        }
        if(this.posX < 0){
            this.speedX = Math.abs(this.speedX)
            //SFX
            SoundManager.playBallBounceSFX()
        }
        if(this.posX+this.width > GameManager.screenSizeX){
            this.speedX = -Math.abs(this.speedX)
            //SFX
            SoundManager.playBallBounceSFX()
        }
    }

    //Function to check if ball is colliding with another game object
    private fun collidingWith(): GameObject? {
        for (gameObject in GameManager.gameObjects) {
            if (this.posX < gameObject.posX+gameObject.width && this.posX+this.width > gameObject.posX) {
                if (this.posY < gameObject.posY+gameObject.height && this.posY+this.height > gameObject.posY) {
                    //Return the other game object if colliding
                    return gameObject
                }
            }
        }
        //Return null if no colliding
        return null
    }

    fun resetPos(){
        //Reset ball position and speed
        this.posX = GameManager.ballStartX
        this.posY = GameManager.ballStartY
        this.speedX = GameManager.ballSpeed
        this.speedY = -GameManager.ballSpeed
    }


    fun loseLife(){
        GameManager.currentCombo = 0
        UIManager.comboText = null
        resetPos()
        //Decrement number of lives
        GameManager.lives--

        if(GameManager.lives <= 0 || GameManager.player2Lives <= 0){
            SoundManager.playGameOverSFX()
            GameManager.context?.gameOver()
        }
        else{
            SoundManager.playLoseLifeSFX()
        }
    }

    fun loseLifePong(player: Int){
        GameManager.currentCombo = 0
        UIManager.comboText = null
        resetPos()
        //Decrement number of lives
        when(player) {
            1 -> GameManager.lives--
            2 -> GameManager.player2Lives--
        }

        if(GameManager.lives <= 0 || GameManager.player2Lives <= 0){
            SoundManager.playGameOverSFX()
            GameManager.context?.gameOver()
        }
        else{
            SoundManager.playLoseLifeSFX()
        }
    }

    fun addLife(){
        resetPos()
        GameManager.lives++
        SoundManager.playLoseLifeSFX()
    }
}