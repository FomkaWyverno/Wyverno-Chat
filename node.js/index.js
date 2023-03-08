const { app, BrowserWindow } = require('electron')
const http = require('http');
const path = require('path');

function createMainWindow() {
    const win = new BrowserWindow({
        width: 800,
        height: 800,
        show: false,

        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
            enableRemoteModule: false
        }
    });

    win.loadURL('http://localhost:2828');

    win.setMenu(null);

    win.once('ready-to-show', () => {
        win.show();
    });

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

    win.loadFile('index.html')

    win.setMenu(null)

    win.setIgnoreMouseEvents(true); //

    win.once('ready-to-show', () => {
        win.show();
    });
}

app.whenReady().then(() => {
    createMainWindow()
})

app.on('window-all-closed', () => {
    app.quit()
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

    }).on('error', (err) => {
        console.log('Error: ', err.message);
    });

    req.write(data);
    req.end();
}

