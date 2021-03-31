package xyz.wrabzy.knowledge

class Controller {
    companion object {
        private var knowledge: Knowledge = Knowledge(null, null)
        private var loaded = false

        fun loadKnowledge() {
            Thread {
                knowledge = Initializer.go()
                loaded = true
            }.start()
        }

        fun getKnowledge() = knowledge
        fun isNotLoaded() = !loaded
    }
}