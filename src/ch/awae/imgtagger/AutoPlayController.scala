package ch.awae.imgtagger

class AutoPlayController(val delay: Double, val random: Boolean, val manager: WindowManager) extends Thread {

  this.start()

  override def run {
    while (!this.isInterrupted())
      try {
        Thread.sleep((delay * 1000L).toLong)
        if (random)
          manager.navRandom
        else
          manager.navNext
      } catch {
        case _: InterruptedException => return
      }
  }

}