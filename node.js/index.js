const { app, BrowserWindow, ipcMain} = require('electron')
const http = require('http');
const path = require('path');

const ipc = ipcMain;

function createMainWindow() {
    const win = new BrowserWindow({
        width: 800,
        height: 800,
        show: false,

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

    // IPC

    ipc.on('main-auth-url', (event, data) => {
        console.log(`Code: ${data.code}`)
        console.log(`URL: ${data.url}`)

        const authWin = createAuthorization(data.url);

        authWin.on('closed', () => {
            win.webContents.send('logged')
        });
        
    });

    ipc.on('open-overlay', () => {
        if (overlay === null) overlay = createOverlay();
    })

    win.webContents.openDevTools();
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

function createAuthorization(url) {
    const win = new BrowserWindow({
        width: 650,
        height: 800,
        show: false,

        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
            enableRemoteModule: false,
        }
    });

    win.loadURL(url);

    win.setMenu(null);

    win.once('ready-to-show', () => {
        win.show();
    });

    win.webContents.openDevTools()

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

    }).on('error', (err) => {
        console.log('Error: ', err.message);
    });

    req.write(data);
    req.end();
}

