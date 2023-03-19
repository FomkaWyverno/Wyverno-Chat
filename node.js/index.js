const { app, BrowserWindow, ipcMain } = require('electron')
const http = require('http');
const child_process = require("child_process")
const { spawn } = require('node:child_process')

const ipc = ipcMain;

startServer();

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

    //win.webContents.openDevTools();

    // IPC

    ipc.on('main-auth-url', (event, data) => {
        console.log(`Code: ${data.code}`)
        console.log(`URL: ${data.url}`)

        child_process.exec(`start "" "${data.url}"`)
    });

    ipc.on('open-overlay', () => {
        if (overlay === null) {
            console.log('Open overlay')
            overlay = createOverlay();
            overlay.on('close', () => {
                overlay = null;
            });
        }
    })

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

    ipcMain.on('window.main.close', () => {
        console.log('close')
        win.close()
    });

    win.on('maximize', () => {
        win.webContents.send('window.main.maximize');
    });

    win.on('unmaximize', () => {
        win.webContents.send('window.main.minimize')
    });

    // IPC-OVERLAY 

    ipcMain.on('window.overlay.collapse', () => {
        if (overlay != null) {
            console.log('overlay collapse')
            overlay.minimize();
        }

    });

    ipcMain.on('window.overlay.maximize-minimize', () => {
        if (overlay != null) {
            console.log('overlay maximize-minimize')
            if (win.isMaximized()) {
                overlay.unmaximize();
            } else {
                overlay.maximize();
            }
        }

    });

    ipcMain.on('window.overlay.close', () => {
        if (overlay != null) {
            console.log('overlay close')
            overlay.close()
        }

    });

    ipcMain.on('overlay.button.press', () => {
        if (overlay != null) {
            console.log('Overlay dragable')
            overlay.setIgnoreMouseEvents(false)
        }
    });
    ipcMain.on('overlay.button.released', () => {
        if (overlay != null) {
            console.log('Overlay now not dragable');
            overlay.setIgnoreMouseEvents(true);
        }
    });
}

function createOverlay() {
    const win = new BrowserWindow({
        width: 800,
        height: 600,
        show: false,
        minWidth: 600,
        minHeight: 500,
        transparent: true, // прозорість бекграунда.
        frame: false, // Віключаємо елементи управління.
        alwaysOnTop: true, // вікно буде завжди поверх усіх

        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false
        }
    })

    win.loadURL('http://localhost:2828/overlay')

    win.setMenu(null)

    win.setIgnoreMouseEvents(true); // Відключаємо взаємодію з вікном.

    win.once('ready-to-show', () => {
        win.show();
    });


    //win.webContents.openDevTools();
    return win;
}

app.whenReady().then(() => {
    createMainWindow()
})

app.on('window-all-closed', () => {
    closeServer()
});

function startServer() {
    // Шлях до java.exe відносно index.js
    const javaPath = './core/java/bin/java.exe';

    // Шлях до jar-файлу відносно index.js
    const jarPath = './core/JTwitchChat-1.0.jar';

    // Аргументи для запуску jar-файлу
    const jarArgs = ['-jar', jarPath];

    // Створення процесу
    const child = spawn(javaPath, jarArgs);

    // Виведення повідомлень в консоль
    child.stdout.on('data', (data) => {
        process.stdout.write(`Java: ${data}`)
        //console.log(`stdout: ${data}`);
    });

    child.stderr.on('data', (data) => {
        //console.error(`stderr: ${data}`);
        process.stderr.write(`Java: ${data}`)
    });

    // Обробка завершення процесу
    child.on('close', (code) => {
        console.log(`child process exited with code ${code}`);
    });
}

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