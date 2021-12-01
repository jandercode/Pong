package com.kyhgroupd.ponggroupd

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity

class GameView(context: Context): SurfaceView(context), SurfaceHolder.Callback, Runnable {

    private var thread: Thread? = null
    private var running = false
    lateinit var canvas: Canvas
    var mHolder: SurfaceHolder? = holder


    init {
        mHolder?.addCallback(this)

        var activity: AppCompatActivity = context as AppCompatActivity
        var displayMetrics: DisplayMetrics = activity.resources.displayMetrics
        DataManager.screenSizeX = displayMetrics.widthPixels
        DataManager.screenSizeY = displayMetrics.heightPixels

        setup()
    }

    private fun setup() {
        var ball = Ball(475, 1000, Color.WHITE)
        val numberOfBricks = 30
        var testBrick : Brick = Brick(100, 300, Color.WHITE)

        for (y in testBrick.height..testBrick.height*3 step testBrick.height) {
            for (x in 0..DataManager.screenSizeX step testBrick.width) {
                var brick = Brick(x, y, Color.WHITE)
                DataManager.gameObjects.add(brick)
                println("x: $x y: $y")
            }
        }
        var paddle : Paddle = Paddle(500,1600, Color.WHITE)

        DataManager.paddle = paddle
        DataManager.gameObjects.add(ball)
        DataManager.gameObjects.add(paddle)

    }

    fun start() {
        running = true
        thread = Thread(this)
        thread?.start()
    }

    fun stop() {
        running = false
        try {
            thread?.join()
        } catch (e: InterruptedException){
            e.printStackTrace()
        }
    }

    fun update(){
        for (gameObject in DataManager.gameObjects) {
            gameObject.update()
        }
    }

    private fun draw(){
        canvas = mHolder!!.lockCanvas()
        canvas.drawColor(Color.BLACK)

        for (gameObject in DataManager.gameObjects) {
            gameObject.draw(canvas)
        }

        mHolder!!.unlockCanvasAndPost(canvas)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        stop()
    }

    override fun run() {
        while (running){
            update()
            draw()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event != null){
            val paddleY = DataManager.paddle?.posY
            if (paddleY != null) {
                val touchY = event.y.toInt()
                val offset = 100
                if(touchY > paddleY-offset && touchY < paddleY+offset){
                    DataManager.paddle!!.posX = event.x.toInt() - (DataManager.paddle!!.width/2)
                }
            }
        }
        return true
    }
}