const { ipcRenderer } = require('electron');

const authButton = document.querySelector('.authorization')
const body = document.querySelector('body');
const openOverlay = document.querySelector('.open-overlay');

tryVerifyAccessToken();

authButton.addEventListener('click', () => {
    authorization();
});

openOverlay.addEventListener('click', () => {
    ipcRenderer.send('open-overlay');
});

ipcRenderer.on('logged', () => { tryVerifyAccessToken() });

function tryVerifyAccessToken() {
    const xhr = new XMLHttpRequest()

    xhr.open('GET', '/verifyAccessToken');

    xhr.onload = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                console.log('AccessToken is valid!')
                const about = JSON.parse(xhr.responseText);
                console.log(about);
                authButton.classList.add('hide')
                displayInformation(about)
                openOverlay.classList.remove('hide')
            } else if (xhr.status === 401) {
                console.log('AccessToken is not valid!')
            }
        }
    }

    xhr.send();
}

function authorization() {
    let xhr = new XMLHttpRequest();

    xhr.open('GET', '/authorization');

    xhr.onload = () => {
        if (xhr.readyState = XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                console.log(`Response: ${xhr.responseText}`);
                ipcRenderer.send('main-auth-url', {
                    code: 200,
                    url: xhr.responseText
                })
            }

        } else {
            console.log(`Error response: ${xhr.readyState}`)
        }
    }

    xhr.send();
}

function displayInformation(about) {
    const span = document.createElement('span');
    span.innerText = JSON.stringify(about, null, 2);
    body.appendChild(span)
}