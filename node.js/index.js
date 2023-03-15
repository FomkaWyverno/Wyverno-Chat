const { app, BrowserWindow, ipcMain} = require('electron')
const http = require('http');
const child_process = require("child_process")

const { NativeKeyboardListener } = require('./keyboardlistener.js')

const ipc = ipcMain;

function createMainWindow() {
    const win = new BrowserWindow({
        width: 800,
        height: 800,
        show: false,
        frame: false,
        minWidth: 600,
        minHeight: 500,

        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
        }
    });

    win.loadURL('http://localhost:2828');

    win.setMenu(null);

    win.once('ready-to-show', () => {
        win.show();
    });

    let overlay = null;

    win.on('close', () => { // Коли головне вікно закрили.
        closeServer(); // Зупиняємо внутрішній сервер.
        if (overlay != null && !overlay.isDestroyed()) overlay.close();
    });

    const keyboard = new NativeKeyboardListener('Left Control',pressButton,releasedButton)

    // IPC

    ipc.on('main-auth-url', (event, data) => {
        console.log(`Code: ${data.code}`)
        console.log(`URL: ${data.url}`)

        child_process.exec(`start "" "${data.url}"`)
    });

    ipc.on('open-overlay', () => {
        if (overlay === null) {
            overlay = createOverlay();
            overlay.on('close', () => {
                overlay = null;
            });
        }
    })

    win.webContents.openDevTools();

    function pressButton() {
        //console.log('Press Control')
        if (overlay != null) {
            console.log('Overlay is drag!')
            overlay.setIgnoreMouseEvents(false);
            overlay.setAlwaysOnTop(false);
        }
    }

    function releasedButton() {
        //console.log('Released Control')
        if (overlay != null) {
            console.log('Overylay is now not drag')
            overlay.setIgnoreMouseEvents(true)
            overlay.setAlwaysOnTop(true)
        }
    }

    // Frame manipulation

    ipcMain.on('window.main.collapse', () => {
        console.log('collapse')
        win.minimize();
    });

    ipcMain.on('window.main.maximize-minimize', () => {
        console.log('maximize-minimize')
        if (win.isMaximized()) {
            win.unmaximize();
        } else {
            win.maximize();
        }
    });

    ipcMain.on('window.main.close',() => {
        console.log('close')
        win.close()
    });

    win.on('maximize', () => {
        win.webContents.send('window.main.maximize');
    });
    
    win.on('unmaximize', () => {
        win.webContents.send('window.main.minimize')
    });
}

function createOverlay() {
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

    win.loadURL('http://localhost:2828/overlay')

    win.setMenu(null)

    win.setIgnoreMouseEvents(true); // Відключаємо взаємодію з вікном.

    win.once('ready-to-show', () => {
        win.show();
    });

    return win;
}

app.whenReady().then(() => {
    createMainWindow()
})

app.on('window-all-closed', () => {
    closeServer()
});

function closeServer() {
    console.log('Try close server');
    const data = JSON.stringify({ close: true })

    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    };

    const req = http.request('http://localhost:2828/close', options, (res) => {

        console.log(`HTTP/${res.httpVersion} ${res.statusCode} ${res.statusMessage}`);

        res.setEncoding('utf8');
        let body = '';
        res.on('data', (chunk) => {
            body += chunk;
        });
        res.on('end', () => {
            console.log(body);
        })

        app.quit();
        process.exit();

    }).on('error', (err) => {
        console.log('Error: ', err.message);
        app.quit()
        process.exit()
    });

    req.write(data);
    req.end();
}