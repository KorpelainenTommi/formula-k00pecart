package formula.application
abstract class Screen {
def activate()
def redraw()
def handleKey(e: java.awt.event.KeyEvent) = {}
}