const gkm = require('gkm')

class NativeKeyboardListener {
    constructor(button, pressedAction, releasedAction) {

        this.pressedAction = pressedAction;
        this.releasedAction = releasedAction;

        gkm.events.on('key.pressed', key => {
            if (!this.isPress && key[0] === button) {
                this.isPress = true;
                pressedAction()
            }
        });
        
        gkm.events.on('key.released', key => {
            if (this.isPress && key[0] === button) {
                this.isPress = false;
                releasedAction();
            }
        });
    }


}

module.exports = {NativeKeyboardListener}