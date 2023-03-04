const { app, BrowserWindow } = require('electron')

function createWindow() {
    const win = new BrowserWindow({
        width: 800,
        height: 600,
        show: false,
        transparent: true, // прозорість бекграунда.
        frame: false, // Віключаємо елементи управління.
        alwaysOnTop: true, // вікно буде завжди поверх усіх

        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
            enableRemoteModule: false
        }
    })

    win.loadFile('index.html')

    win.setMenu(null)

    win.setIgnoreMouseEvents(true); //

    win.once('ready-to-show', () => {
        win.show();
    });

    win.webContents.openDevTools();
}

app.whenReady().then(() => {
    createWindow()
})

app.on('window-all-closed', () => {
    app.quit()
})